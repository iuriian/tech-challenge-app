package br.com.fiap.oficina.veiculo.application.port

import java.util.UUID

interface VeiculoQueryPort {
    fun buscarPorId(id: UUID): VeiculoQueryDto?
}
