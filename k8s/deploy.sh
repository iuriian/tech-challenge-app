#!/usr/bin/env bash
#
# Deploy da aplicacao no GKE (ambiente de staging).
#
# Uso:
#   ./k8s/deploy.sh <IMAGEM> [AMBIENTE] [NAMESPACE]
#
# Exemplo:
#   ./k8s/deploy.sh ghcr.io/iuriian/tech-challenge@sha256:abc... staging default
#
# Pre-requisitos: kubectl autenticado no cluster (gcloud container clusters
# get-credentials ...) e a imagem ja publicada no registry.
#
set -euo pipefail

IMAGE="${1:-}"
ENVIRONMENT="${2:-staging}"
NAMESPACE="${3:-default}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

# Tempo maximo (segundos) de espera pelo IP externo do Service do Keycloak.
KEYCLOAK_IP_TIMEOUT="${KEYCLOAK_IP_TIMEOUT:-300}"
ROLLOUT_TIMEOUT="${ROLLOUT_TIMEOUT:-900s}"

if [[ -z "$IMAGE" ]]; then
  echo "Uso: $0 <IMAGEM> [AMBIENTE] [NAMESPACE]" >&2
  exit 1
fi

echo "==> Ambiente : $ENVIRONMENT"
echo "==> Namespace: $NAMESPACE"
echo "==> Imagem   : $IMAGE"

kubectl get namespace "$NAMESPACE" >/dev/null 2>&1 || kubectl create namespace "$NAMESPACE"

# ---------------------------------------------------------------------------
# 1. Secrets
# No pipeline (cd.yml) o db-credentials ja e criado a partir dos GitHub Secrets.
# Em execucao local, criamos valores de desenvolvimento para nao travar o deploy.
# ---------------------------------------------------------------------------
if ! kubectl get secret db-credentials -n "$NAMESPACE" >/dev/null 2>&1; then
  echo "==> Criando secret db-credentials (valores padrao de desenvolvimento)"
  kubectl create secret generic db-credentials \
    --from-literal=POSTGRES_USER="${POSTGRES_USER:-user}" \
    --from-literal=POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-password}" \
    -n "$NAMESPACE"
fi

#if ! kubectl get secret keycloak-credentials -n "$NAMESPACE" >/dev/null 2>&1; then
#  echo "==> Criando secret keycloak-credentials"
#  if [[ -z "${KEYCLOAK_ADMIN_PASSWORD:-}" ]]; then
#    # O Service do Keycloak e LoadBalancer: no GKE o console de admin fica
#    # acessivel pela internet. Deixar admin/admin ai e um convite.
#    echo "!! ATENCAO: KEYCLOAK_ADMIN_PASSWORD nao definido - usando a senha padrao 'admin'." >&2
#    echo "!!          Aceitavel apenas em ambiente local. No GKE, defina o secret antes:" >&2
#    echo "!!          kubectl create secret generic keycloak-credentials \\" >&2
#    echo "!!            --from-literal=KEYCLOAK_ADMIN_USER=admin \\" >&2
#    echo "!!            --from-literal=KEYCLOAK_ADMIN_PASSWORD='SENHA_FORTE' -n $NAMESPACE" >&2
#  fi
#  kubectl create secret generic keycloak-credentials \
#    --from-literal=KEYCLOAK_ADMIN_USER="${KEYCLOAK_ADMIN_USER:-admin}" \
#    --from-literal=KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin}" \
#    -n "$NAMESPACE"
#fi

# ---------------------------------------------------------------------------
# 1b. Checksum dos secrets, injetado no pod template dos Deployments.
# E o que faz uma senha corrigida realmente chegar ao pod: o template muda, o
# Kubernetes cria um ReplicaSet novo e o pod antigo e substituido. Sem isso o
# apply e um no-op e o pod continua com o valor antigo.
# ---------------------------------------------------------------------------
if command -v sha256sum >/dev/null 2>&1; then
  HASHER=(sha256sum)
else
  HASHER=(shasum -a 256)
fi

#SECRETS_HASH="$(kubectl get secret db-credentials keycloak-credentials \
#  -n "$NAMESPACE" -o jsonpath='{range .items[*]}{.metadata.name}={.data}{end}' \
#  | "${HASHER[@]}" | cut -c1-16)"
#echo "==> Checksum dos secrets: $SECRETS_HASH"

# ---------------------------------------------------------------------------
# 2. ConfigMaps gerados a partir dos arquivos versionados em conf/
#    (evita duplicar realm e script de init entre docker-compose e Kubernetes)
# ---------------------------------------------------------------------------
echo "==> Aplicando ConfigMaps"
kubectl create configmap postgres-initdb \
  --from-file="$ROOT_DIR/conf/init-db/" \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap keycloak-realm \
  --from-file=realm.json="$ROOT_DIR/conf/realm-export.json" \
  -n "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -f "$SCRIPT_DIR/app/configmap.yaml" -n "$NAMESPACE"

# ---------------------------------------------------------------------------
# 3. PostgreSQL
# ---------------------------------------------------------------------------
echo "==> Aplicando PostgreSQL"
# O PVC precisa existir antes do Deployment que o monta, senao o pod fica Pending.
kubectl apply -f "$SCRIPT_DIR/postgres/pvc.yaml" -n "$NAMESPACE"
kubectl apply -f "$SCRIPT_DIR/postgres/deployment.yaml" -n "$NAMESPACE"
kubectl rollout status deployment/postgres -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# ---------------------------------------------------------------------------
# 4. Service do Keycloak + descoberta do IP externo
#    O issuer dos tokens precisa ser a URL publica; o Deployment so e aplicado
#    depois que esse endereco e conhecido.
# ---------------------------------------------------------------------------
echo "==> Aplicando Service do Keycloak"
kubectl apply -f "$SCRIPT_DIR/keycloak/service.yaml" -n "$NAMESPACE"

echo "==> Aguardando IP externo do Keycloak (ate ${KEYCLOAK_IP_TIMEOUT}s)"
KEYCLOAK_IP=""
ELAPSED=0
while [[ $ELAPSED -lt $KEYCLOAK_IP_TIMEOUT ]]; do
  KEYCLOAK_IP="$(kubectl get svc keycloak -n "$NAMESPACE" \
    -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)"
  [[ -n "$KEYCLOAK_IP" ]] && break
  sleep 10
  ELAPSED=$((ELAPSED + 10))
done

if [[ -n "$KEYCLOAK_IP" ]]; then
  KEYCLOAK_URL="http://${KEYCLOAK_IP}:8080"
else
  # Sem IP publico o deploy continua, mas apenas tokens emitidos de dentro do
  # cluster serao aceitos (issuer interno).
  KEYCLOAK_URL="http://keycloak:8080"
  echo "!! IP externo nao atribuido a tempo; usando issuer interno $KEYCLOAK_URL" >&2
fi
echo "==> Keycloak URL: $KEYCLOAK_URL"

# ---------------------------------------------------------------------------
# 5. Keycloak
# ---------------------------------------------------------------------------
echo "==> Aplicando Keycloak"
echo "    O primeiro boot leva ~2 min (augmentation do Quarkus + import do realm)."
echo "    Ate la o startupProbe registra 'connection refused' na porta 9000: e esperado."
echo "    NAO interrompa - o Ctrl+C aqui mata o script inteiro, antes de aplicar a app."
sed -e "s|__KEYCLOAK_URL__|${KEYCLOAK_URL}|g" \
    -e "s|__SECRETS_HASH__|${SECRETS_HASH}|g" "$SCRIPT_DIR/keycloak/deployment.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -
kubectl rollout status deployment/keycloak -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# ---------------------------------------------------------------------------
# 6. Aplicacao
# ---------------------------------------------------------------------------
echo "==> Aplicando aplicacao"
sed -e "s|__IMAGE__|${IMAGE}|g" \
    -e "s|__KEYCLOAK_URL__|${KEYCLOAK_URL}|g" \
    -e "s|__SECRETS_HASH__|${SECRETS_HASH}|g" "$SCRIPT_DIR/app/deployment.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -
kubectl rollout status deployment/app -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# HPA depois do Deployment: aplicado antes, o alvo nao existiria. No GKE o
# metrics-server ja vem instalado, entao nao ha nada a provisionar.
echo "==> Aplicando HPA"
kubectl apply -f "$SCRIPT_DIR/app/hpa.yaml" -n "$NAMESPACE"

# ---------------------------------------------------------------------------
# 7. Resumo
# ---------------------------------------------------------------------------
APP_IP="$(kubectl get svc app -n "$NAMESPACE" \
  -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || true)"

echo ""
echo "=========================================="
echo " Deploy concluido ($ENVIRONMENT)"
echo "=========================================="
if [[ -n "$APP_IP" ]]; then
  echo " App      : http://$APP_IP"
  echo " Swagger  : http://$APP_IP/swagger-ui/index.html"
  echo " Health   : http://$APP_IP/actuator/health"
else
  echo " App      : IP externo ainda nao atribuido (kubectl get svc app -w)"
fi
echo " Keycloak : $KEYCLOAK_URL"
echo "=========================================="
kubectl get pods,svc -n "$NAMESPACE"
