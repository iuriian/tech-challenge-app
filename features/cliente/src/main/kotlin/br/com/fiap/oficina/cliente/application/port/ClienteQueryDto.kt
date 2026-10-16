package br.com.fiap.oficina.cliente.application.port

data class ClienteQueryDto(
    val id: String,
    val nome: String,
    val numeroDocumento: String,
)
