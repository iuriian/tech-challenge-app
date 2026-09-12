package br.com.fiap.oficina.funcionario.application.dto

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class FuncionarioRequest(
    val id: String? = null,
    @field:NotBlank
    val nome: String,
    @field:NotBlank
    val cargo: String,
    @field:NotBlank
    val cpf: String,
)
