package br.com.fiap.oficina.funcionario.application.dto

import jakarta.validation.constraints.NotBlank

data class FuncionarioRequest(
    @field:NotBlank
    val nome: String,
    @field:NotBlank
    val cargo: String,
    @field:NotBlank
    val cpf: String,
)
