package br.com.fiap.oficina.funcionario.application.port

import java.util.UUID

interface FuncionarioQueryPort {
    fun buscarPorId(id: UUID): FuncionarioQueryDto?
}
