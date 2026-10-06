package br.com.fiap.oficina.peca.application.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class PecaRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 15)
    val codigo: String,
    @field:NotBlank
    @field:Size(min = 3, max = 100)
    val nome: String,
    @field:Size(max = 255)
    val descricao: String? = null,
    @field:NotBlank
    @field:Size(max = 100)
    val fabricante: String,
    @field:Size(max = 100)
    val fornecedor: String? = null,
    @field:NotNull
    @field:DecimalMin("0.00")
    val precoDeCompra: BigDecimal,
    @field:NotNull
    @field:DecimalMin("0.00")
    val precoDeVenda: BigDecimal,
    @field:NotNull
    @field:Min(0)
    val qtdEstoque: Int = 0,
)
