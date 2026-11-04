# Escalabilidade automática — como usar e reproduzir

Como provocar o `HorizontalPodAutoscaler` da aplicação e comprovar que ele criou réplicas,
usando a pasta **`99 - Escalabilidade automática (carga)`** da coleção
[`TechChallengeAPI-Completa-Escalabilidade.postman_collection.json`](TechChallengeAPI-Completa-Escalabilidade.postman_collection.json).

## 1. O que está configurado

O autoscaling tem duas camadas, e só as duas juntas escalam de verdade:

| Camada | Onde | O que faz |
|---|---|---|
| **HPA** (pods) | [`k8s/app/hpa.yaml`](../k8s/app/hpa.yaml) | Cria réplicas do Deployment `app` quando a CPU passa do alvo |
| **Cluster autoscaler** (nós) | [`tech-challenge-infra-k8s`](https://github.com/iuriian/tech-challenge-infra-k8s) | Cria nós para acomodar os pods novos |

Parâmetros atuais do HPA:

```yaml
minReplicas: 1
maxReplicas: 3
metrics:
  - resource:
      name: cpu
      target:
        averageUtilization: 70   # 70% do REQUEST, não do limite
behavior:
  scaleDown:
    stabilizationWindowSeconds: 300
```

Dois detalhes que explicam o comportamento que você vai observar:

- O alvo é **70% do `requests.cpu`, que é `100m`** — ou seja, o gatilho dispara com
  **~70 milicores por pod**, cerca de 7% de um núcleo. É um valor propositalmente baixo:
  em staging o objetivo é demonstrar o escalonamento, não sustentar produção.
- `scaleDown.stabilizationWindowSeconds: 300` significa que os pods extras **permanecem por
  ~5 minutos** depois que a carga cessa. Isso não é lentidão do cluster: é a janela que evita
  oscilação. Não encerre a observação antes disso achando que travou.

O Deployment **não declara `replicas`** de propósito. Com um HPA existente, manter o campo faria
cada `kubectl apply` resetar a contagem e brigar com o autoscaler.

## 2. Pré-requisitos

- Cluster com **metrics-server** ativo. Sem ele o HPA fica preso em `<unknown>/70%` e nunca
  escala. No GKE já vem instalado; no kind, o [`kind/deploy-local.sh`](../kind/deploy-local.sh)
  instala e aplica o patch `--kubelet-insecure-tls` automaticamente.
- Postman (Collection Runner) ou `newman` (`npm install -g newman`).
- `kubectl` apontando para o cluster.

## 3. Subir o ambiente

### Local (kind) — recomendado para reproduzir

```bash
./kind/deploy-local.sh
```

O script cria o cluster, builda a imagem, aplica os manifests de `k8s/`, instala o
metrics-server e aplica o HPA. Ao final:

| | URL |
|---|---|
| App | http://localhost:18080 |
| Keycloak | http://localhost:18081 |

### Staging (GKE)

```bash
./k8s/deploy.sh ghcr.io/<usuario>/tech-challenge:<tag> staging <namespace>
kubectl get svc app -n <namespace>   # pega o EXTERNAL-IP
```

Confirme que o HPA subiu e que já enxerga métricas:

```bash
kubectl get hpa app
# NAME  REFERENCE        TARGETS     MINPODS  MAXPODS  REPLICAS
# app   Deployment/app   2%/70%      1        3        1
```

Se `TARGETS` mostrar `<unknown>/70%`, o metrics-server ainda não coletou (espere ~1 min) ou
não está instalado — nada mais adiante vai funcionar até isso resolver.

## 4. Apontar a coleção

Na coleção, ajuste as variáveis para o ambiente **com autoscaling** (não para o
docker-compose):

| Variável | kind | GKE |
|---|---|---|
| `baseUrl` | `http://localhost:18080` | `http://<APP_IP>` |
| `keycloakUrl` | `http://localhost:18081` | `http://<KEYCLOAK_IP>:8080` |
| `clientId` | `oficina` | `oficina` |
| `username` / `password` | `admin` / `admin` | `admin` / `admin` |

> O `keycloakUrl` precisa ser **a mesma URL pública** que a aplicação recebeu em
> `KEYCLOAK_URL`. O Spring valida o claim `iss` do token; URLs diferentes resultam em `401`
> mesmo com token válido.

O token é obtido e renovado sozinho pelo pre-request script da coleção — importante aqui,
porque o token do Keycloak expira em ~5 minutos e uma rodada de carga passa disso.

Antes de cada campanha, rode uma vez (**fora do Runner**) o request
`99.22 - Reset dos contadores de carga`, que zera latências e contadores preservando a massa
de dados.

## 5. Abrir a observação

Em terminais separados, **antes** de disparar a carga:

```bash
# 1 - o HPA decidindo
kubectl get hpa app -w

# 2 - os pods nascendo
kubectl get pods -l app=app -w

# 3 - a CPU real por pod (o número que o HPA compara com 70m)
watch -n 5 kubectl top pods -l app=app
```

## 6. Gerar a carga

Cada iteração executa um ciclo completo de oficina: cria uma OS, consulta, calcula o
orçamento, lista as ordens do cliente, roda a métrica de tempo médio e avança o status.

### Opção A — Collection Runner (Postman)

1. **Run collection** → selecione **apenas** a pasta `99 - Escalabilidade automática (carga)`.
2. **Iterations**: `300`. **Delay**: `0 ms`.
3. **Desmarque** o request `99.22 - Reset dos contadores de carga`.
4. Run.

### Opção B — newman em paralelo (gera mais CPU)

Uma execução sequencial única costuma manter a CPU perto do gatilho. Para atravessá-lo com
folga, rode vários processos ao mesmo tempo:

```bash
npm install -g newman

# 4 processos x 200 iterações = 800 ordens de serviço
for i in 1 2 3 4; do
  newman run "docs/TechChallengeAPI-Completa-Escalabilidade.postman_collection.json" \
    --folder "99 - Escalabilidade automática (carga)" \
    --iteration-count 200 \
    --env-var "baseUrl=http://localhost:18080" \
    --env-var "keycloakUrl=http://localhost:18081" \
    --reporters cli &
done
wait
```

Se ainda assim a CPU não passar de 70m, aumente para 8 processos — ou reduza temporariamente
`averageUtilization` no `hpa.yaml`. Nunca reduza o `requests.cpu`: isso muda o
dimensionamento real do pod, não só o gatilho.

## 7. O que deve acontecer

A sequência esperada, em ordem:

1. **Latência sobe.** O console do Runner registra o p95 crescendo a cada bloco de 25 iterações.
2. **CPU passa de 70m.** `kubectl top pods` mostra o pod acima do alvo.
3. **HPA reage** (avalia a cada ~15s, com janela de subida curta):

```
NAME   REFERENCE        TARGETS     MINPODS   MAXPODS   REPLICAS
app    Deployment/app   142%/70%    1         3         1
app    Deployment/app   142%/70%    1         3         2
app    Deployment/app    88%/70%    1         3         3
```

4. **Pods novos entram em serviço.** Passam por `Pending` → `Init` (o init container espera o
   Postgres) → `Running`. O `readinessProbe` só libera tráfego depois do
   `/actuator/health/readiness`, então a latência melhora alguns segundos após o pod aparecer.
5. **Latência estabiliza** com a carga distribuída entre as réplicas.
6. **Após ~5 min de ociosidade**, o HPA volta para 1 réplica (a janela de `scaleDown`).

Ao final, o request `99.21` imprime o relatório consolidado no console:

```
==================================================================
  RELATÓRIO DE CARGA - TechChallenge API
==================================================================
  Ordens de serviço criadas ..: 300
  Requisições medidas ........: 2100
  Erros (HTTP >= 400) ........: 0 (0.00%)
------------------------------------------------------------------
  Latência média .............: 84 ms
  Latência p95 ...............: 310 ms
  Latência p99 ...............: 522 ms
------------------------------------------------------------------
```

O `99.21` também falha o teste se o p95 passar de `loadP95AlvoMs` (1500 ms) ou a taxa de erro
passar de `loadErroMaxPct` (5%) — ambos ajustáveis por variável de coleção.

## 8. Evidências para a entrega

Os eventos do HPA são a prova documental do escalonamento:

```bash
kubectl describe hpa app | tail -20
```

```
Events:
  Type    Reason             Age   From                       Message
  ----    ------             ----  ----                       -------
  Normal  SuccessfulRescale  2m    horizontal-pod-autoscaler  New size: 2; reason: cpu resource utilization above target
  Normal  SuccessfulRescale  1m    horizontal-pod-autoscaler  New size: 3; reason: cpu resource utilization above target
```

Vale capturar também:

- `kubectl get hpa app -w` durante a subida (mostra `TARGETS` e `REPLICAS` mudando juntos);
- `kubectl get pods -l app=app` antes e depois;
- o relatório do `99.21` no console do Runner.

## 9. Voltar ao estado inicial

```bash
kubectl get hpa app -w        # aguarde voltar a 1 réplica (~5 min)
./kind/down.sh                # ou destrua o cluster local
```

As ordens de serviço criadas pela carga ficam no banco com a descrição
`[CARGA <lote>] Ordem de serviço #N`, o que permite identificá-las e removê-las depois.
A massa de apoio (cliente, veículo, peça e serviço de catálogo de carga) é reaproveitada
entre campanhas — o `99.22` não a apaga de propósito.

## 10. Quando não escala

| Sintoma | Causa provável |
|---|---|
| `TARGETS` em `<unknown>/70%` | metrics-server ausente ou sem `--kubelet-insecure-tls` (kind) |
| CPU sobe mas `REPLICAS` fica em 1 | Carga insuficiente — rode mais processos em paralelo |
| Pods novos em `Pending` | Falta capacidade de nó: é o cluster autoscaler que precisa entrar, não o HPA |
| `REPLICAS` chega a 3 e para | É o teto: `maxReplicas: 3` |
| Réplicas não caem após o teste | Comportamento correto: `scaleDown` só age após 5 min de carga baixa |
| Muitos `401` durante a carga | `keycloakUrl` diferente do `KEYCLOAK_URL` da aplicação (claim `iss`) |
| Muitos `409`/`400` no `99.15` | Esperado: a OS já chegou ao fim da máquina de estados |

## Lacunas conhecidas

- O HPA usa **apenas CPU**. Uma saturação de memória ou de pool de conexões do banco não
  dispara escalonamento.
- `maxReplicas: 3` é um teto de staging; não foi dimensionado a partir de um teste de carga
  com meta de throughput.
- A carga é gerada pelo Postman/newman, adequado para demonstrar o mecanismo, mas não
  substitui uma ferramenta de teste de carga (k6, Gatling) para medir capacidade real.
