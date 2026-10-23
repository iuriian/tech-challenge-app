package br.com.fiap.oficina.cliente.application.dto

data class EnderecoRequest(
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val cep: String,
)
