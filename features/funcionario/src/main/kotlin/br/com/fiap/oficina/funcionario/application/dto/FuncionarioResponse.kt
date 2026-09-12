package br.com.fiap.oficina.funcionario.application.dto

data class FuncionarioResponse(
    val id: String,
    val nome: String,
    val cargoDescricao: String,
    val cpf: String,
)
