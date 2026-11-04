package br.com.fiap.oficina.api.observability

import br.com.fiap.oficina.ordemservico.application.dto.OrdemServicoResponse
import br.com.fiap.oficina.ordemservico.domain.OrdemServicoStatus
import com.newrelic.api.agent.NewRelic
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Emite custom events [EVENT_TYPE] para o New Relic (dashboards / alertas de OS).
 * Sem o Java Agent anexado, [NewRelic.getAgent] é no-op.
 */
@Component
class OrdemServicoTelemetry {
    fun registrarCriacao(response: OrdemServicoResponse) {
        record(
            action = ACTION_CRIADA,
            resultado = RESULTADO_SUCESSO,
            ordemId = response.id,
            status = response.status.name,
            etapa = etapaDe(response.status),
        )
    }

    fun registrarMudancaStatus(action: String, response: OrdemServicoResponse, statusAnterior: String? = null) {
        record(
            action = action,
            resultado = RESULTADO_SUCESSO,
            ordemId = response.id,
            status = response.status.name,
            statusAnterior = statusAnterior,
            etapa = etapaDe(response.status),
            duracaoMs = duracaoMsPara(response),
        )
    }

    fun registrarFalha(action: String, ordemId: String?, status: String? = null, erro: Throwable) {
        record(
            action = action,
            resultado = RESULTADO_FALHA,
            ordemId = ordemId,
            status = status,
            erroTipo = erro.javaClass.simpleName,
            erroMensagem = erro.message?.take(ERRO_MENSAGEM_MAX_LENGTH),
        )
    }

    private fun record(
        action: String,
        resultado: String,
        ordemId: String?,
        status: String? = null,
        statusAnterior: String? = null,
        etapa: String? = null,
        duracaoMs: Long? = null,
        erroTipo: String? = null,
        erroMensagem: String? = null,
    ) {
        val attrs =
            linkedMapOf<String, Any>(
                "action" to action,
                "resultado" to resultado,
            )
        ordemId?.let { attrs["ordemId"] = it }
        status?.let { attrs["status"] = it }
        statusAnterior?.let { attrs["statusAnterior"] = it }
        etapa?.let { attrs["etapa"] = it }
        duracaoMs?.let { attrs["duracaoMs"] = it }
        erroTipo?.let { attrs["erroTipo"] = it }
        erroMensagem?.let { attrs["erroMensagem"] = it }

        NewRelic.getAgent().insights.recordCustomEvent(EVENT_TYPE, attrs)
    }

    private fun etapaDe(status: OrdemServicoStatus): String? = when (status) {
        OrdemServicoStatus.RECEBIDA,
        OrdemServicoStatus.EM_DIAGNOSTICO,
        OrdemServicoStatus.AGUARDANDO_APROVACAO,
        -> ETAPA_DIAGNOSTICO

        OrdemServicoStatus.EM_EXECUCAO -> ETAPA_EXECUCAO

        OrdemServicoStatus.FINALIZADA,
        OrdemServicoStatus.ENTREGUE,
        -> ETAPA_FINALIZACAO

        OrdemServicoStatus.CANCELADA -> null
    }

    private fun duracaoMsPara(response: OrdemServicoResponse): Long? {
        val inicio = response.dataInicioExecucao ?: return null
        val fim = response.dataFinalizacao ?: return null
        return Duration.between(inicio, fim).toMillis().takeIf { it >= 0 }
    }

    companion object {
        // Limite do atributo de evento no New Relic: acima disso o valor e truncado la.
        private const val ERRO_MENSAGEM_MAX_LENGTH = 255

        const val EVENT_TYPE = "OrdemServicoEvent"
        const val ACTION_CRIADA = "criada"
        const val ACTION_AVANCADA = "avancada"
        const val ACTION_STATUS_ALTERADO = "status_alterado"
        const val RESULTADO_SUCESSO = "sucesso"
        const val RESULTADO_FALHA = "falha"
        const val ETAPA_DIAGNOSTICO = "Diagnostico"
        const val ETAPA_EXECUCAO = "Execucao"
        const val ETAPA_FINALIZACAO = "Finalizacao"
    }
}
