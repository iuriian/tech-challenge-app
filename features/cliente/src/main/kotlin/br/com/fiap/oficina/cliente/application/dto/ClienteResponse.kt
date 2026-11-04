package br.com.fiap.oficina.cliente.application.dto

data class ClienteResponse(
    val id: String,
    val nome: String,
    val numeroDocumento: String,
    val tipoPessoa: String,
    val email: String,
    val endereco: EnderecoResponse? = null,
    val contatos: List<ContatoResponse> = emptyList(),
)
