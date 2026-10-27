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

SECRETS_HASH="$(kubectl get secret db-credentials \
  -n "$NAMESPACE" -o jsonpath='{.metadata.name}={.data}' \
  | "${HASHER[@]}" | cut -c1-16)"
echo "==> Checksum dos secrets: $SECRETS_HASH"

# ---------------------------------------------------------------------------
# 2. ConfigMaps gerados a partir dos arquivos versionados em conf/
#    (evita duplicar o script de init entre docker-compose e Kubernetes)
# ---------------------------------------------------------------------------
echo "==> Aplicando ConfigMaps"
kubectl create configmap postgres-initdb \
  --from-file="$ROOT_DIR/conf/init-db/" \
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
# 4. Aplicacao
#    O emissor dos tokens e externo ao cluster: chega pelo app-config
#    (AUTH_ISSUER_URI), sem Service nem descoberta de IP aqui.
# ---------------------------------------------------------------------------
echo "==> Aplicando aplicacao"
sed -e "s|__IMAGE__|${IMAGE}|g" \
    -e "s|__SECRETS_HASH__|${SECRETS_HASH}|g" "$SCRIPT_DIR/app/deployment.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -
kubectl rollout status deployment/app -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# HPA depois do Deployment: aplicado antes, o alvo nao existiria. No GKE o
# metrics-server ja vem instalado, entao nao ha nada a provisionar.
echo "==> Aplicando HPA"
kubectl apply -f "$SCRIPT_DIR/app/hpa.yaml" -n "$NAMESPACE"

# ---------------------------------------------------------------------------
# 5. Resumo
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
echo "=========================================="
kubectl get pods,svc -n "$NAMESPACE"
