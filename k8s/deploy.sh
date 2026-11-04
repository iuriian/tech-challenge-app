#!/usr/bin/env bash
#
# Deploy da aplicacao no GKE (ambiente de staging).
#
# O banco e um Cloud SQL externo ao cluster, provisionado pelo repositorio
# tech-challenge-database. Este script NAO sobe Postgres: o k8s/postgres/ e
# usado apenas pelo ambiente local com kind (kind/deploy-local.sh).
#
# Uso:
#   DB_HOST=<ip-privado> AUTH_ISSUER_URI=<url> ./k8s/deploy.sh <IMAGEM> [AMBIENTE] [NAMESPACE]
#
# Exemplo:
#   DB_HOST=10.20.0.3 AUTH_ISSUER_URI=https://oficina-auth-gw-xxxx.uc.gateway.dev \
#     ./k8s/deploy.sh ghcr.io/iuriian/tech-challenge@sha256:abc... staging tech-challenge-staging
#
# Pre-requisitos:
#   - kubectl autenticado no cluster (gcloud container clusters get-credentials ...)
#   - imagem ja publicada no registry
#   - secret db-credentials criado no namespace (o cd-staging.yml faz isso)
#   - instancia Cloud SQL ligada (repo tech-challenge-database: ./scripts/cloudsql.sh on)
#
set -euo pipefail

IMAGE="${1:-}"
ENVIRONMENT="${2:-staging}"
NAMESPACE="${3:-default}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

ROLLOUT_TIMEOUT="${ROLLOUT_TIMEOUT:-900s}"

# Emissor dos tokens. Substituido no app-config: um valor vazio deixaria o
# placeholder e a validacao do iss falharia em toda requisicao.
AUTH_ISSUER_URI="${AUTH_ISSUER_URI:-}"

# IP privado do Cloud SQL. O cd-staging.yml o descobre via gcloud.
# sslmode=require casa com o ssl_mode ENCRYPTED_ONLY da instancia.
DB_HOST="${DB_HOST:-}"
DB_SSLMODE="${DB_SSLMODE:-require}"

if [[ -z "$IMAGE" ]]; then
  echo "Uso: $0 <IMAGEM> [AMBIENTE] [NAMESPACE]" >&2
  exit 1
fi

if [[ -z "$AUTH_ISSUER_URI" ]]; then
  echo "ERRO: AUTH_ISSUER_URI nao definido - o app-config ficaria com o placeholder" >&2
  echo "      e a aplicacao nao subiria. Ex.: AUTH_ISSUER_URI=https://auth.exemplo $0 ..." >&2
  exit 1
fi

if [[ -z "$DB_HOST" ]]; then
  echo "ERRO: DB_HOST nao definido (IP privado do Cloud SQL)." >&2
  echo "      Descubra com:" >&2
  echo "        gcloud sql instances describe tech-challenge-staging-pg --format='value(ipAddresses[0].ipAddress)'" >&2
  exit 1
fi

echo "==> Ambiente : $ENVIRONMENT"
echo "==> Namespace: $NAMESPACE"
echo "==> Imagem   : $IMAGE"
echo "==> Banco    : $DB_HOST (sslmode=$DB_SSLMODE)"

kubectl get namespace "$NAMESPACE" >/dev/null 2>&1 || kubectl create namespace "$NAMESPACE"

# ---------------------------------------------------------------------------
# 1. Secrets
# O db-credentials e criado pelo cd-staging.yml a partir do Secret Manager.
# Aqui apenas exigimos que exista: um valor padrao de desenvolvimento nunca
# casaria com a senha do usuario no Cloud SQL, e o erro so apareceria depois,
# como pod em CrashLoopBackOff.
# ---------------------------------------------------------------------------
if ! kubectl get secret db-credentials -n "$NAMESPACE" >/dev/null 2>&1; then
  echo "ERRO: secret db-credentials nao existe no namespace $NAMESPACE." >&2
  echo "      Crie com a senha do Secret Manager:" >&2
  echo "        kubectl create secret generic db-credentials -n $NAMESPACE \\" >&2
  echo "          --from-literal=POSTGRES_USER=oficina_app \\" >&2
  echo "          --from-literal=POSTGRES_PASSWORD=\"\$(gcloud secrets versions access latest --secret=tech-challenge-staging-db-password)\"" >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# 2. Checksum dos secrets, injetado no pod template do Deployment.
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
# 3. ConfigMap da aplicacao
# ---------------------------------------------------------------------------
echo "==> Aplicando ConfigMap"
sed -e "s|__AUTH_ISSUER_URI__|${AUTH_ISSUER_URI}|g" \
    -e "s|__DB_HOST__|${DB_HOST}|g" \
    -e "s|__DB_SSLMODE__|${DB_SSLMODE}|g" \
    "$SCRIPT_DIR/app/configmap.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -

# ---------------------------------------------------------------------------
# 4. Aplicacao
#    O emissor dos tokens e externo ao cluster (API Gateway do
#    tech-challenge-auth) e chega pelo app-config, sem Service aqui.
# ---------------------------------------------------------------------------
echo "==> Aplicando aplicacao"
sed -e "s|__IMAGE__|${IMAGE}|g" \
    -e "s|__SECRETS_HASH__|${SECRETS_HASH}|g" \
    "$SCRIPT_DIR/app/deployment.yaml" \
  | kubectl apply -n "$NAMESPACE" -f -
kubectl rollout status deployment/app -n "$NAMESPACE" --timeout="$ROLLOUT_TIMEOUT"

# HPA depois do Deployment: aplicado antes, o alvo nao existiria. No GKE o
# metrics-server ja vem instalado, entao nao ha nada a provisionar.
echo "==> Aplicando HPA"
kubectl apply -f "$SCRIPT_DIR/app/hpa.yaml" -n "$NAMESPACE"

# ---------------------------------------------------------------------------
# Resumo
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
echo " Banco    : Cloud SQL $DB_HOST"
echo " Issuer   : $AUTH_ISSUER_URI"
echo "=========================================="
kubectl get pods,svc -n "$NAMESPACE"