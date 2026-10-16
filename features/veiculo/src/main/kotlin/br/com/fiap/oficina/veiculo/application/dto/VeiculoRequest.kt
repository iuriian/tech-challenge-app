package br.com.fiap.oficina.veiculo.application.dto

import br.com.fiap.oficina.veiculo.domain.ProprietarioId
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VeiculoRequest(
    @field:Size(min = 3, max = 20)
    val nome: String,
    val marca: String,
    val modelo: String,
    val ano: String,
    @field:Pattern(
        regexp = "^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$",
        message = "Placa inválida. Use o formato antigo (ABC1234) ou Mercosul (ABC1D23)",
    )
    val placa: String,
    @field:NotNull
    val proprietarioId: String,
    val id: String? = null,
)
