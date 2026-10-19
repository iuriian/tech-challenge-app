package br.com.fiap.oficina.veiculo.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class VeiculoRequest(
    @field:Size(min = 3, max = 20)
    val nome: String,
    @field:Size(min = 2, max = 20)
    val marca: String,
    val modelo: String,
    val ano: String,
    @field:NotBlank
    val placa: String,
    @field:NotBlank
    val proprietarioId: String,
)
