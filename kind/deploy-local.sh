#!/usr/bin/env bash
#
# Sobe a stack completa (Postgres + aplicacao) em um cluster kind
# local, reutilizando exatamente os mesmos manifests usados no GKE (pasta k8s/).
#
# Uso:
#   ./kind/deploy-local.sh              # cria cluster, builda a imagem e faz o deploy
#   SKIP_BUILD=true ./kind/deploy-local.sh   # reaproveita a imagem ja buildada
#
# Pre-requisitos: docker, kind e kubectl no PATH.
#
set -euo pipefail

CLUSTER_NAME="${CLUSTER_NAME:-oficina-local}"
IMAGE="${IMAGE:-oficina-app:local}"
NAMESPACE="${NAMESPACE:-default}"
SKIP_BUILD="${SKIP_BUILD:-false}"
ROLLOUT_TIMEOUT="${ROLLOUT_TIMEOUT:-900s}"

# Portas publicadas na maquina host. Os padroes evitam as portas usadas pelo
# docker-compose (80, 5432, 8090) e a porta 8080, comum demais.
# Para trocar:  APP_HOST_PORT=19000 ./kind/deploy-local.sh
APP_HOST_PORT="${APP_HOST_PORT:-18080}"

# O emissor dos tokens e externo ao cluster: a aplicacao so valida assinaturas,
# buscando o JWKS nesse endereco.
AUTH_ISSUER_URI="${AUTH_ISSUER_URI:-http://localhost:8081}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
K8S_DIR="$ROOT_DIR/k8s"

for bin in docker kind kubectl; do
  command -v "$bin" >/dev/null 2>&1 || { echo "ERRO: '$bin' nao encontrado no PATH" >&2; exit 1; }
done

# Aborta cedo se a porta ja estiver ocupada no host: o erro nativo do kind para
# esse caso ("port is already allocated") vem enterrado em um docker run gigante.
check_port_free() {
  local port="$1" label="$2" var="$3"
  if (exec 3<>"/dev/tcp/127.0.0.1/${port}") 2>/dev/null; then
    exec 3<&- 3>&- 2>/dev/null || true
    echo "ERRO: a porta ${port} (${label}) ja esta em uso nesta maquina." >&2
    echo "      Descubra quem esta usando:" >&2
    echo "        docker ps --format 'table {{.Names}}\t{{.Ports}}'" >&2
    echo "        netstat -ano | grep ':${port} '" >&2
    echo "      Ou escolha outra porta:  ${var}=1${port} ./kind/deploy-local.sh" >&2
    exit 1
  fi
}

# ---------------------------------------------------------------------------
# 1. Cluster
# ---------------------------------------------------------------------------
NODE_NAME="${CLUSTER_NAME}-control-plane"

if kind get clusters 2>/dev/null | grep -qx "$CLUSTER_NAME"; then
  echo "==> Cluster '$CLUSTER_NAME' ja existe"
  # extraPortMappings so vale no momento da criacao do no: um cluster criado
  # antes, ou com outras portas, nunca vai publicar essas portas no host.
  if ! docker port "$NODE_NAME" 2>/dev/null | grep -q ":${APP_HOST_PORT}$"; then
    echo "AVISO: o no '$NODE_NAME' nao publica a porta ${APP_HOST_PORT} no host." >&2
    echo "       O deploy vai funcionar, mas http://localhost:${APP_HOST_PORT} nao respondera." >&2
    echo "       Para corrigir, recrie o cluster:  ./kind/down.sh && ./kind/deploy-local.sh" >&2
  fi
else
  check_port_free "$APP_HOST_PORT" "aplicacao" "APP_HOST_PORT"

  echo "==> Criando cluster '$CLUSTER_NAME' (app: ${APP_HOST_PORT})"
  CLUSTER_CONFIG="$(mktemp)"
  trap 'rm -f "$CLUSTER_CONFIG"' EXIT
  sed -e "s|hostPort: 18080|hostPort: ${APP_HOST_PORT}|" \
      "$SCRIPT_DIR/kind-cluster.yaml" > "$CLUSTER_CONFIG"
  kind create cluster --config "$CLUSTER_CONFIG"
fi

kubectl config use-context "kind-${CLUSTER_NAME}" >/dev/null
echo "==> Contexto kubectl: $(kubectl config current-context)"

# ---------------------------------------------------------------------------
# 2. Imagem da aplicacao
# O kind nao enxerga o daemon local do Docker: a imagem precisa ser carregada
# para dentro do no com 'kind load'.
# ---------------------------------------------------------------------------
if [[ "$SKIP_BUILD" != "true" ]]; then
  echo "==> Buildando a imagem $IMAGE (pode demorar no primeiro build)"
  docker build -t "$IMAGE" "$ROOT_DIR"
fi

echo "==> Carregando $IMAGE no cluster"
kind load docker-image "$IMAGE" --name "$CLUSTER_NAME"

# ---------------------------------------------------------------------------
# 3. Secrets (valores de desenvolvimento)
# ---------------------------------------------------------------------------
echo "==> Aplicando Secrets"
kubectl create secret generic db-credentials \
  --from-literal=POSTGRES_USER="${POSTGRES_USER:-user}" \
  --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-password}" \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

# Placeholder: a imagem local nao precisa de credencial, mas o Deployment
# referencia imagePullSecrets e o kubelet loga warning se o secret nao existir.
kubectl create secret docker-registry ghcr-creds \
  --docker-server=ghcr.io \
  --docker-username=local \
  --docker-password=local \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

# ---------------------------------------------------------------------------
# 4. ConfigMaps gerados a partir de conf/ (mesmos arquivos do docker-compose)
# ---------------------------------------------------------------------------
echo "==> Aplicando ConfigMaps"
kubectl create configmap postgres-initdb \
  --from-file="$ROOT_DIR/conf/init-db/" \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

sed -e "s|__AUTH_ISSUER_URI__|${AUTH_ISSUER_URI}|g" \
    -e "s|__DB_HOST__|postgres|g" \
    -e "s|__DB_SSLMODE__|disable|g" \
    "$K8S_DIR/app/configmap.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -

# ---------------------------------------------------------------------------
# 5. PostgreSQL
# ---------------------------------------------------------------------------
echo "==> Aplicando PostgreSQL"
kubectl apply -f "$K8S_DIR/postgres/pvc.yaml" -n "$NAMESPACE"
kubectl apply -f "$K8S_DIR/postgres/deployment.yaml" -n "$NAMESPACE"
kubectl rollout status deployment/postgres -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# ---------------------------------------------------------------------------
# 6. Aplicacao (1 replica local, para economizar CPU/memoria da maquina)
# ---------------------------------------------------------------------------
echo "==> Aplicando aplicacao (issuer: $AUTH_ISSUER_URI)"
sed -e "s|__IMAGE__|${IMAGE}|g" \
    "$K8S_DIR/app/deployment.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -

# ---------------------------------------------------------------------------
# 7. Converte os Services para NodePort (aplicado DEPOIS dos manifests, que
#    declaram LoadBalancer para o GKE)
# ---------------------------------------------------------------------------
kubectl apply -f "$SCRIPT_DIR/nodeport-services.yaml" -n "$NAMESPACE"

# A tag local e sempre a mesma (oficina-app:local), entao apos um rebuild o
# `apply` retorna "unchanged" e o pod antigo continua com a imagem velha.
# O restart forca o kubelet a recriar o pod, que ai pega a imagem recem-carregada.
if [[ "$SKIP_BUILD" != "true" ]]; then
  kubectl rollout restart deployment/app -n "$NAMESPACE"
fi

kubectl rollout status deployment/app -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# ---------------------------------------------------------------------------
# 8. metrics-server + HPA
#
# Diferente do GKE, o kind NAO traz metrics-server. Sem ele o HPA fica preso em
# <unknown> indefinidamente, mesmo com tudo o mais correto. O --kubelet-insecure-tls
# e necessario porque os certificados do kubelet no kind nao sao validos para o
# metrics-server.
# ---------------------------------------------------------------------------
if ! kubectl get deployment metrics-server -n kube-system >/dev/null 2>&1; then
  echo "==> Instalando metrics-server (nao vem no kind)"
  kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
  kubectl patch deployment metrics-server -n kube-system --type=json \
    -p='[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]'
  kubectl rollout status deployment/metrics-server -n kube-system --timeout=180s
fi

echo "==> Aplicando HPA"
kubectl apply -f "$K8S_DIR/app/hpa.yaml" -n "$NAMESPACE"

# ---------------------------------------------------------------------------
# 9. Smoke test
# ---------------------------------------------------------------------------
echo "==> Verificando /actuator/health"
HEALTH="$(curl -fsS --max-time 10 "http://localhost:${APP_HOST_PORT}/actuator/health" 2>/dev/null || echo 'indisponivel')"

cat <<RESUMO

==========================================
 Ambiente local no ar (cluster: $CLUSTER_NAME)
==========================================
 App       : http://localhost:${APP_HOST_PORT}
 Health    : http://localhost:${APP_HOST_PORT}/actuator/health  -> $HEALTH
 Swagger   : http://localhost:${APP_HOST_PORT}/swagger-ui/index.html
 Issuer    : ${AUTH_ISSUER_URI}

 Destruir: ./kind/down.sh
==========================================
RESUMO

kubectl get pods,svc -n "$NAMESPACE"
