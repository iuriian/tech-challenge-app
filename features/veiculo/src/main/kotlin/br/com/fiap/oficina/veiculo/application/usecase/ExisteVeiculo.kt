package br.com.fiap.oficina.veiculo.application.usecase

import br.com.fiap.oficina.shared.domain.valueobject.Placa

interface ExisteVeiculo {
    fun executar(placa: Placa): Boolean
}
