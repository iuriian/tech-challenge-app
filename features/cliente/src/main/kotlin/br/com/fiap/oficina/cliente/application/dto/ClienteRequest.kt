package br.com.fiap.oficina.cliente.application.dto

data class ClienteRequest(
    val nome: String,
    val numeroDocumento: String,
    val tipoPessoa: String,
    val email: String,
    val endereco: EnderecoRequest? = null,
    val contatos: List<ContatoRequest> = emptyList(),
)
