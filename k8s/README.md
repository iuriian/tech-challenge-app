# Deploy no Kubernetes (GKE) — Staging

Implementação mínima para subir a aplicação **oficina-mecanica** em um cluster GKE.

## Arquivos

| Arquivo | Conteúdo |
|---|---|
| `app/configmap.yaml` | ConfigMap `app-config` (nome da base, `DB_HOST` e `DB_SSLMODE` do Cloud SQL, issuer e audience dos tokens) |
| `postgres/pvc.yaml` | PersistentVolumeClaim de 10Gi do PostgreSQL — **só para o kind** |
| `postgres/deployment.yaml` | Deployment + Service do PostgreSQL 16 — **só para o kind** |
| `app/deployment.yaml` | Deployment (2 réplicas) + Service `LoadBalancer` da aplicação |
| `deploy.sh` | Orquestra o deploy no GKE: valida o secret, aplica ConfigMap, Deployment e HPA, e acompanha o rollout |

> A ordem de aplicação é definida pelo `deploy.sh`, não pelo nome dos arquivos.

Para testar tudo isso localmente antes do GKE, veja [`kind/README.md`](../kind/README.md).

## Banco de dados

O banco é um **Cloud SQL externo ao cluster**, provisionado pelo repositório
`tech-challenge-database`. O `deploy.sh` não sobe PostgreSQL nenhum: ele exige
`DB_HOST` (IP privado da instância) e grava esse valor no `app-config`, de onde o
Deployment monta a URL JDBC. O pipeline resolve o IP com `gcloud sql instances
describe` e aborta o deploy se a instância estiver desligada.

```bash
DB_HOST=10.20.0.3 AUTH_ISSUER_URI=https://... ./k8s/deploy.sh <IMAGEM> staging <NS>
```

A pasta `postgres/` e o ConfigMap `postgres-initdb` (gerado a partir de
`conf/init-db/`, os mesmos arquivos do `docker-compose.yml`) pertencem ao ambiente
local: quem os usa é o `kind/deploy-local.sh`, que sobe um Postgres descartável no
cluster e aponta `DB_HOST=postgres`.

## Autenticação

O emissor dos tokens é externo ao cluster. A aplicação só valida assinaturas: lê
`AUTH_ISSUER_URI` e `AUTH_AUDIENCE` do `app-config` e busca o JWKS em
`${AUTH_ISSUER_URI}/.well-known/jwks.json`.

> O `app/configmap.yaml` versionado traz `AUTH_ISSUER_URI: ""`. Preencha com a URL
> real antes do deploy: com a variável definida e vazia o Spring não aplica o
> default e a aplicação não sobe.

## Pré-requisitos no GKE

```bash
gcloud container clusters create-auto tech-challenge-staging --region=us-central1
# ou, para cluster standard:
gcloud container clusters create tech-challenge-staging \
  --zone=us-central1-a --num-nodes=2 --machine-type=e2-standard-2

gcloud container clusters get-credentials tech-challenge-staging \
  --zone us-central1-a --project SEU_PROJECT_ID
```

## Deploy manual

```bash
NS=tech-challenge-staging

# O deploy.sh cria o namespace, mas os secrets precisam dele antes
kubectl create namespace $NS

# Secrets (o script cria valores padrão de desenvolvimento se não existirem)
kubectl create secret generic db-credentials -n $NS \
  --from-literal=POSTGRES_USER=user \
  --from-literal=POSTGRES_PASSWORD='SENHA_FORTE'

# Secret para puxar a imagem do GHCR (repositório privado)
kubectl create secret docker-registry ghcr-creds -n $NS \
  --docker-server=ghcr.io \
  --docker-username=SEU_USUARIO \
  --docker-password=SEU_PAT

./k8s/deploy.sh ghcr.io/seu-usuario/tech-challenge:latest staging $NS
```

Variáveis de ambiente aceitas pelo script: `POSTGRES_USER`, `POSTGRES_PASSWORD`,
`ROLLOUT_TIMEOUT`.

## Deploy pelo pipeline

O workflow `.github/workflows/cd.yml` dispara em push nas branches **`release/**`**
(e por `workflow_dispatch`), e encadeia: `build` → `smoke-test` → `promote` → `deploy-staging`.
O job de deploy chama `./k8s/deploy.sh "<imagem>" staging <namespace>`.

### Configuração do Environment `staging`

Toda a configuração de acesso ao cluster vive no **GitHub Environment**, não em
secrets do repositório. Crie em *Settings → Environments → New environment → `staging`*.

**Environment secrets:**

| Secret | Uso |
|---|---|
| `GCP_SA_KEY` | JSON da service account com papel `roles/container.developer` |
| `GCP_PROJECT_ID` | ID do projeto GCP |
| `GKE_CLUSTER` | Nome do cluster |
| `GKE_ZONE` | Zona/região do cluster |
| `DB_PASSWORD` | Senha do PostgreSQL |

**Environment variables:**

| Variável | Obrigatória | Uso |
|---|---|---|
| `KUBE_NAMESPACE` | não | Padrão: `tech-challenge-staging` |

**Secret do repositório** (não é específico do ambiente):

| Secret | Uso |
|---|---|
| `GHCR_PAT` | PAT com escopo `read:packages` para o cluster puxar a imagem |

O job valida essa configuração no primeiro passo e falha nomeando o que estiver
faltando, em vez de deixar o `gcloud` errar com "cluster not found".

### Por que Environment e não secrets do repositório

Secrets de ambiente só ficam disponíveis para jobs que declaram `environment: staging` —
o job de build, por exemplo, nunca enxerga a chave do GCP. O ambiente também é onde se
configuram **regras de proteção**: aprovadores obrigatórios, janela de espera e
restrição de branches. Nada disso existe para secrets de repositório.

### Por que o `GHCR_PAT` importa

Sem ele o workflow cai no `GITHUB_TOKEN`, que **expira quando o job termina**. O deploy
passa (a imagem é puxada durante o job) e, horas depois, o primeiro pod que reiniciar
— restart, escala, upgrade de nó do GKE — falha com `ImagePullBackOff` sem que nada
tenha mudado no código. Crie um PAT clássico com escopo `read:packages` e salve como
`GHCR_PAT`, ou torne o package público no GHCR.

## Verificação

```bash
NS=tech-challenge-staging
kubectl get pods -n $NS
kubectl get svc -n $NS                # pega o EXTERNAL-IP de app
curl http://<APP_IP>/actuator/health  # deve retornar {"status":"UP"}
```

Swagger UI: `http://<APP_IP>/swagger-ui/index.html`

O token vem do emissor externo configurado em `AUTH_ISSUER_URI`; use o botão
**Authorize** do Swagger UI com o bearer obtido lá.

## Troubleshooting

```bash
kubectl describe pod -l app=app
kubectl logs -l app=app -c wait-for-db --tail=30   # esperando o banco
kubectl logs -l app=app -c app --tail=100          # aplicação / Flyway
kubectl get events --sort-by=.lastTimestamp | tail -30
```

| Sintoma | Causa provável |
|---|---|
| `ImagePullBackOff` | Secret `ghcr-creds` ausente ou PAT sem escopo `read:packages` |
| App reinicia após o init container | Flyway falhou — ver logs do container `app` |
| `401` mesmo com token válido | Issuer ou audience divergentes de `AUTH_ISSUER_URI` / `AUTH_AUDIENCE` |
| App não sobe, erro de placeholder | `AUTH_ISSUER_URI` vazio no `app-config` — o JWKS fica sem host |
| PVC `Pending` (só no kind) | Cluster sem StorageClass padrão ou sem quota de disco |
| Init container preso em `aguardando postgres...` | `DB_HOST` errado, Cloud SQL desligado ou sem rota privada do cluster até a instância |
| Deploy aborta antes do rollout | Cloud SQL com `activationPolicy` diferente de `ALWAYS` — ligue pela action do repositório `tech-challenge-database` |

## Limpeza

```bash
kubectl delete -f k8s/app/deployment.yaml -f k8s/app/configmap.yaml
kubectl delete hpa app

# O banco nao e apagado aqui: o Cloud SQL vive fora do cluster e tem ciclo de
# vida proprio, no repositorio tech-challenge-database.
```
