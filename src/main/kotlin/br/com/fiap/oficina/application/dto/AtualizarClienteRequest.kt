package br.com.fiap.oficina.application.dto

import br.com.fiap.oficina.cliente.application.dto.ContatoRequest
import br.com.fiap.oficina.cliente.application.dto.EnderecoRequest

data class AtualizarClienteRequest(
    val nome: String,
    val numeroDocumento: String,
    val tipoPessoa: String,
    val email: String,
    val endereco: EnderecoRequest? = null,
    val contatos: List<ContatoRequest> = emptyList(),
)
