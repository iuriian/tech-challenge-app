package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.valueobject.AggregateId
import java.util.UUID

data class ProprietarioId(
    override val value: UUID,
) : AggregateId() {
    companion object {
        fun toUUID(uuid: String): ProprietarioId = ProprietarioId(parse(uuid))
    }
}
