package br.com.fiap.oficina.api.observability

import jakarta.servlet.FilterChain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RequestCorrelationFilterTest {
    private val filter = RequestCorrelationFilter()

    @Test
    fun `deve gerar requestId quando o header nao vem na requisicao`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        var chainCalled = false
        val chain =
            FilterChain { _, _ ->
                chainCalled = true
                assertNotNull(MDC.get(RequestCorrelationFilter.MDC_REQUEST_ID))
            }

        filter.doFilter(request, response, chain)

        val requestId = response.getHeader(RequestCorrelationFilter.HEADER_REQUEST_ID)
        assertNotNull(requestId)
        assertTrue(requestId!!.isNotBlank())
        assertTrue(chainCalled)
        assertNull(MDC.get(RequestCorrelationFilter.MDC_REQUEST_ID))
    }

    @Test
    fun `deve reutilizar o X-Request-Id recebido`() {
        val request =
            MockHttpServletRequest().apply {
                addHeader(RequestCorrelationFilter.HEADER_REQUEST_ID, "req-fixo-123")
            }
        val response = MockHttpServletResponse()
        var chainCalled = false
        val chain = FilterChain { _, _ -> chainCalled = true }

        filter.doFilter(request, response, chain)

        assertEquals(
            "req-fixo-123",
            response.getHeader(RequestCorrelationFilter.HEADER_REQUEST_ID),
        )
        assertTrue(chainCalled)
        assertNull(MDC.get(RequestCorrelationFilter.MDC_REQUEST_ID))
    }
}
