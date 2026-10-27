# Ambiente local com kind

Sobe a stack completa (PostgreSQL + aplicação) em um cluster Kubernetes
real rodando dentro do Docker, **reutilizando os mesmos manifests de `k8s/`** que
serão usados no GKE. Serve para validar os manifests antes de gastar recursos na nuvem.

## Pré-requisitos

| Ferramenta | Instalação (Windows) |
|---|---|
| Docker Desktop | https://www.docker.com/products/docker-desktop |
| kind | `choco install kind` ou `winget install Kubernetes.kind` |
| kubectl | `choco install kubernetes-cli` ou `winget install Kubernetes.kubectl` |

Recomendado: 4 CPUs e 6 GB de RAM disponíveis para o Docker.

Os scripts são bash — no Windows, rode pelo **Git Bash** ou **WSL**.

## Uso

```bash
./kind/deploy-local.sh                     # cria o cluster, builda a imagem e faz o deploy
SKIP_BUILD=true ./kind/deploy-local.sh     # redeploy reaproveitando a imagem
APP_HOST_PORT=19000 ./kind/deploy-local.sh # se as portas padrão estiverem ocupadas
./kind/down.sh                             # destrói o cluster
```

Variáveis aceitas: `APP_HOST_PORT` (18080), `AUTH_ISSUER_URI` (http://localhost:8081),
`CLUSTER_NAME`, `IMAGE`, `NAMESPACE`, `SKIP_BUILD`, `ROLLOUT_TIMEOUT`.

Endereços depois do deploy:

| | URL |
|---|---|
| App | http://localhost:18080 |
| Health | http://localhost:18080/actuator/health |
| Swagger UI | http://localhost:18080/swagger-ui/index.html |

O emissor dos tokens é externo ao cluster: aponte `AUTH_ISSUER_URI` para ele antes
do deploy. O script sobrescreve essa variável direto no Deployment, porque o
`app-config` versionado nasce com o valor vazio.

## Arquivos

| Arquivo | Papel |
|---|---|
| `kind-cluster.yaml` | Cluster de um nó com `extraPortMappings` (30080→18080) |
| `nodeport-services.yaml` | Converte o Service `app` de LoadBalancer para NodePort |
| `deploy-local.sh` | Cria o cluster, builda, carrega a imagem e aplica os manifests de `k8s/` |
| `down.sh` | Deleta o cluster |

## Diferenças em relação ao GKE

| | GKE (staging) | kind (local) |
|---|---|---|
| Exposição | `Service` LoadBalancer com IP público | `NodePort` + `extraPortMappings` |
| Imagem | `ghcr.io/...` via secret `ghcr-creds` | `oficina-app:local` via `kind load docker-image` |
| Réplicas da app | 2 | 1 (economia de recursos) |
| Volume do Postgres | Persistent Disk do GCP | `local-path` (diretório no nó) |

Os manifests de `k8s/` **não são duplicados**: o `deploy-local.sh` aplica os mesmos
arquivos e só sobrepõe os Services no final.

## Detalhes que costumam confundir

**Por que NodePort e não LoadBalancer?** O kind não tem cloud controller, então um
Service LoadBalancer fica em `EXTERNAL-IP: <pending>` para sempre.

**Por que `kind load docker-image`?** O nó do kind é um container com seu próprio
container runtime — ele não enxerga as imagens do daemon Docker do host. Sem esse
passo o pod fica em `ErrImagePull` mesmo com a imagem buildada localmente.

**Por que o issuer precisa ser alcançável de dentro do pod?** A aplicação apenas
valida assinaturas: ela busca o JWKS em `${AUTH_ISSUER_URI}/.well-known/jwks.json`
na subida do contexto. Um endereço que só resolve no host, e não de dentro do nó do
kind, faz o pod falhar no startup.

## Troubleshooting

```bash
kubectl get pods
kubectl logs -l app=app -c wait-for-db --tail=30   # travado esperando o banco
kubectl logs -l app=app -c app --tail=100          # aplicação / Flyway
kubectl get events --sort-by=.lastTimestamp | tail -30
```

| Sintoma | Causa provável |
|---|---|
| `ErrImagePull` / `ImagePullBackOff` | Faltou o `kind load docker-image` ou o nome da imagem divergiu |
| `port is already allocated` na criação | Outra coisa usa a porta. Ache com `netstat -ano \| grep :18080` ou rode com `APP_HOST_PORT=19000` |
| Cluster existe mas a porta não responde | O cluster foi criado com outras portas — `extraPortMappings` só vale na criação. Recrie: `./kind/down.sh && ./kind/deploy-local.sh` |
| Pods em `Pending` | Docker sem CPU/memória suficiente — aumente nas configurações do Docker Desktop |

| App reinicia após o init container | Flyway falhou — ver logs do container `app` |
