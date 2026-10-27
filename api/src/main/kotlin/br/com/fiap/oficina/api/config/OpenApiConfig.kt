package br.com.fiap.oficina.api.config

import br.com.fiap.oficina.api.exception.ErrorResponse
import br.com.fiap.oficina.api.exception.ValidationErrorResponse
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem.HttpMethod
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("TechChallenge API")
                .version("1.0.0")
                .description(
                    "API de gerenciamento de oficina mecânica. " +
                        "Use o botão **Authorize** e informe usuário e senha para autenticar.",
                ),
        )

    @Bean
    fun convencaoDeErros(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            registrarSchemasDeErro(openApi)

            openApi.paths?.forEach { (caminho, item) ->
                val temIdentificador = "{" in caminho

                item.readOperationsMap().forEach { (metodo, operacao) ->
                    val respostas = operacao.responses ?: ApiResponses().also { operacao.responses = it }

                    respostas.erroInterno()

                    if (metodo in MUTACOES) {
                        respostas.dadosInvalidos()
                        respostas.conflito()
                    }
                    if (temIdentificador) {
                        respostas.naoEncontrado()
                    }
                }
            }
        }

    private companion object {
        val MUTACOES = setOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH)

        const val REF_ERRO = "#/components/schemas/ErrorResponse"
        const val REF_VALIDACAO = "#/components/schemas/ValidationErrorResponse"

        fun corpo(ref: String): Content =
            Content().addMediaType(
                "application/json",
                MediaType().schema(Schema<Any>().`$ref`(ref)),
            )

        fun ApiResponses.resposta(
            codigo: String,
            descricao: String,
            ref: String,
        ) = addApiResponse(codigo, ApiResponse().description(descricao).content(corpo(ref)))

        fun ApiResponses.dadosInvalidos() = resposta("400", "Dados inválidos", REF_VALIDACAO)

        fun ApiResponses.naoEncontrado() = resposta("404", "Recurso não encontrado", REF_ERRO)

        fun ApiResponses.conflito() = resposta("409", "Conflito com o estado atual do recurso", REF_ERRO)

        fun ApiResponses.erroInterno() = resposta("500", "Erro interno do servidor", REF_ERRO)

        fun registrarSchemasDeErro(openApi: OpenAPI) {
            val componentes = openApi.components ?: Components().also { openApi.components = it }
            val conversor = ModelConverters.getInstance()

            (conversor.read(ErrorResponse::class.java) + conversor.read(ValidationErrorResponse::class.java))
                .forEach { (nome, schema) -> componentes.addSchemas(nome, schema) }
        }
    }
}
