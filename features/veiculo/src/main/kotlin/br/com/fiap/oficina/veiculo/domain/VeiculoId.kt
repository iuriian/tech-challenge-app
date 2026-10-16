package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.valueobject.AggregateId
import java.util.UUID

data class VeiculoId(
    override val value: UUID,
) : AggregateId() {
    companion object {
        fun generate(): VeiculoId = VeiculoId(newId())

        fun toUUID(uuid: String): VeiculoId = VeiculoId(parse(uuid))
    }
}
