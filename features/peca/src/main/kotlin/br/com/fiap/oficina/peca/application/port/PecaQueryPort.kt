package br.com.fiap.oficina.peca.application.port

import java.util.UUID

interface PecaQueryPort {
    fun buscarPorId(id: UUID): PecaQueryDto?
}
