package br.com.fiap.oficina.veiculo.domain.port

import br.com.fiap.oficina.veiculo.domain.ProprietarioRef

data class DadosProprietario(
    val ref: ProprietarioRef,
    val nome: String,
    val numeroDocumento: String,
)
