package br.com.fiap.oficina.servico.application.port

import java.util.UUID

interface ServicoQueryPort {
    fun buscarPorId(id: UUID): ServicoQueryDto?
}
