package br.com.fiap.oficina.api.observability

import com.newrelic.api.agent.NewRelic
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * Folga sobre a precedência máxima: o filtro roda cedo, mas depois dos filtros de
 * infraestrutura do Spring, para que todo log da requisição já saia correlacionado.
 */
private const val ORDER_OFFSET = 20

/**
 * Propaga/gera [HEADER_REQUEST_ID] e preenche o MDC para logs JSON correlacionados.
 * Quando o Java Agent do New Relic está ativo, também copia trace.id / span.id do linking metadata.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + ORDER_OFFSET)
class RequestCorrelationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestId =
            request.getHeader(HEADER_REQUEST_ID)?.takeIf { it.isNotBlank() }
                ?: UUID.randomUUID().toString()

        MDC.put(MDC_REQUEST_ID, requestId)
        response.setHeader(HEADER_REQUEST_ID, requestId)
        NewRelic.addCustomParameter(MDC_REQUEST_ID, requestId)

        NewRelic.getAgent().linkingMetadata.forEach { (key, value) ->
            if (value.isNotBlank()) {
                MDC.put(key, value)
            }
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    companion object {
        const val HEADER_REQUEST_ID = "X-Request-Id"
        const val MDC_REQUEST_ID = "requestId"
    }
}
