# Observabilidade — New Relic

APM da aplicação **oficina mecânica** com New Relic Java Agent (Docker / Kubernetes).

| | |
|---|---|
| Conta | `8639027` |
| App local (Compose) | `oficina-mecanica-local` |
| App staging (K8s) | `oficina-mecanica-staging` |
| Dashboard (JSON) | [`newrelic/dashboard-oficina.json`](newrelic/dashboard-oficina.json) |
| Custom event | `OrdemServicoEvent` via [`OrdemServicoTelemetry`](../api/src/main/kotlin/br/com/fiap/oficina/api/observability/OrdemServicoTelemetry.kt) na controller de OS |
| Logs JSON | `logback-spring.xml` + `RequestCorrelationFilter` (`X-Request-Id`, `trace.id`) |

## 1. Importar / atualizar o dashboard

1. Abra [Dashboards](https://one.newrelic.com/dashboards).
2. Clique em **Import dashboard**.
3. Cole o conteúdo de [`newrelic/dashboard-oficina.json`](newrelic/dashboard-oficina.json).
4. Conta: **8639027** · permissões: leitura/escrita para o time.
5. **Save**.

Se já existir um dashboard antigo, pode importar de novo (cria outra cópia) ou apagar o anterior.

Documentação oficial: [Import/export dashboards](https://docs.newrelic.com/docs/query-your-data/explore-query-data/dashboards/dashboards-charts-import-export-data/).

## 2. Gerar dados (local)

```bash
docker compose --profile full up -d --build
curl -s http://localhost:8090/actuator/health
```

Para `OrdemServicoEvent`, use a collection Postman (criar OS, avançar status) com token JWT.
Eventos aparecem em **Query Your Data** em ~1 minuto:

```sql
SELECT * FROM OrdemServicoEvent SINCE 30 minutes ago
```

Link APM:
[oficina-mecanica-local](https://one.newrelic.com/redirect/entity/ODYzOTAyN3xBUE18QVBQTElDQVRJT058MTA3NTI0NTQxMg).

## 3. Cobertura do enunciado

| Requisito | Fonte |
|---|---|
| Latência das APIs | APM Transaction |
| Health / uptime | Actuator + throughput APM |
| Erros e falhas | TransactionError + `OrdemServicoEvent` (`resultado = 'falha'`) |
| Volume diário de OS | `OrdemServicoEvent` onde `action = 'criada'` |
| Tempo médio por status/etapa | `average(duracaoMs) FACET etapa` (Diagnostico / Execucao / Finalizacao) |
| CPU/memória Kubernetes | Quando o cluster voltar (`nri-bundle`) |

### Atributos do evento

| Atributo | Exemplos |
|---|---|
| `action` | `criada`, `avancada`, `status_alterado` |
| `resultado` | `sucesso`, `falha` |
| `status` | `RECEBIDA`, `EM_EXECUCAO`, … |
| `etapa` | `Diagnostico`, `Execucao`, `Finalizacao` |
| `duracaoMs` | tempo execução→finalização (quando houver timestamps) |
| `erroTipo` / `erroMensagem` | em falhas (ex. transição inválida 422) |

## 4. Alerta rápido (portal)

**Falhas de processamento de OS:**

```sql
SELECT count(*) FROM OrdemServicoEvent WHERE resultado = 'falha'
```

Threshold: ex. **> 0** em 5 minutos → e-mail.

**Taxa de erro HTTP em `/servicos`:**

```sql
SELECT percentage(count(*), WHERE error IS true)
FROM Transaction
WHERE appName LIKE 'oficina-mecanica%'
  AND (request.uri LIKE '%/servicos%' OR name LIKE '%OrdemServico%')
```

## 5. Logs JSON e correlação

A API emite logs em **JSON** no stdout. Cada requisição leva:

| Campo | Origem |
|---|---|
| `requestId` | Header `X-Request-Id` (ou UUID gerado) |
| `trace.id` / `span.id` | Linking metadata do Java Agent (quando ativo) |

No New Relic: **Logs** filtrando por `requestId` ou drill-down a partir de um trace no APM.

```bash
docker compose --profile full up -d --build
curl -si -H "X-Request-Id: demo-123" http://localhost:8090/actuator/health
docker compose logs app | tail -20
```

## 6. Próximas etapas

1. Criar alerta NRQL no portal (`OrdemServicoEvent` com `resultado = 'falha'`).
2. Helm `nri-bundle` no GKE (CPU/memória).
3. ADR de observabilidade + diagrama atualizado.
