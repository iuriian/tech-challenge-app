package br.com.fiap.oficina.api.support

import br.com.fiap.oficina.api.exception.GlobalExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

/**
 * Infraestrutura comum aos testes de controller do modulo api.
 *
 * O MockMvc e montado em modo standalone: exercita roteamento, desserializacao,
 * validacao e o [GlobalExceptionHandler] sem subir o contexto do Spring Boot, que
 * exigiria datasource e JPA por conta do OfficinaApplication.
 */
internal object ApiTestSupport {
    val objectMapper: ObjectMapper =
        ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())

    fun mockMvc(controller: Any): MockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .setValidator(LocalValidatorFactoryBean().apply { afterPropertiesSet() })
        .build()

    fun json(valor: Any): String = objectMapper.writeValueAsString(valor)
}
