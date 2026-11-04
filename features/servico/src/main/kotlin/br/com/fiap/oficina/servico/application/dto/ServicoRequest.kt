package br.com.fiap.oficina.servico.application.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ServicoRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val descricao: String,
    @field:DecimalMin("0.00")
    val valor: BigDecimal,
)
