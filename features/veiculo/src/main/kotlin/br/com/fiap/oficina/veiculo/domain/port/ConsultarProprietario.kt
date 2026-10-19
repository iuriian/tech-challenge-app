package br.com.fiap.oficina.veiculo.domain.port

import br.com.fiap.oficina.veiculo.domain.ProprietarioRef

interface ConsultarProprietario {
    fun buscarPorId(ref: ProprietarioRef): DadosProprietario?
}
