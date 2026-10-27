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
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("TechChallenge API")
                .version("1.0.0")
                .description(
                    "Obtenha um token em POST {AUTH_ISSUER_URI}/auth/funcionario/token e use o botão **Authorize**.",
                ),
        ).components(
            Components().addSecuritySchemes(
                "bearer",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"),
            ),
        ).addSecurityItem(SecurityRequirement().addList("bearer"))

    @Bean
    fun convencaoDeErros(): OpenApiCustomizer = OpenApiCustomizer { openApi ->
        registrarSchemasDeErro(openApi)

        openApi.paths?.forEach { (caminho, item) ->
            val temIdentificador = "{" in caminho

            item.readOperationsMap().forEach { (metodo, operacao) ->
                val respostas = operacao.responses ?: ApiResponses().also { operacao.responses = it }

                respostas.erroInterno()
                respostas.naoAutenticado()
                respostas.semPermissao()

                if (metodo in MUTACOES) {
                    respostas.dadosInvalidos()
                    respostas.conflito()
                    respostas.regraDeNegocio()
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

        fun corpo(ref: String): Content = Content().addMediaType(
            "application/json",
            MediaType().schema(Schema<Any>().`$ref`(ref)),
        )

        fun ApiResponses.resposta(codigo: String, descricao: String, ref: String) =
            addApiResponse(codigo, ApiResponse().description(descricao).content(corpo(ref)))

        fun ApiResponses.dadosInvalidos() = resposta("400", "Dados inválidos", REF_VALIDACAO)

        fun ApiResponses.naoEncontrado() = resposta("404", "Recurso não encontrado", REF_ERRO)

        fun ApiResponses.conflito() = resposta("409", "Conflito com o estado atual do recurso", REF_ERRO)

        fun ApiResponses.erroInterno() = resposta("500", "Erro interno do servidor", REF_ERRO)

        fun ApiResponses.naoAutenticado() = resposta("401", "Token ausente, inválido ou expirado", REF_ERRO)

        fun ApiResponses.semPermissao() = resposta("403", "Perfil sem permissão para a operação", REF_ERRO)

        fun ApiResponses.regraDeNegocio() = resposta("422", "Regra de negócio violada", REF_ERRO)

        fun registrarSchemasDeErro(openApi: OpenAPI) {
            val componentes = openApi.components ?: Components().also { openApi.components = it }
            val conversor = ModelConverters.getInstance()

            (conversor.read(ErrorResponse::class.java) + conversor.read(ValidationErrorResponse::class.java))
                .forEach { (nome, schema) -> componentes.addSchemas(nome, schema) }
        }
    }
}
