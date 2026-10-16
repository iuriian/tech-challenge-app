package br.com.fiap.oficina.veiculo.domain

import br.com.fiap.oficina.shared.domain.AggregateId
import java.util.UUID

data class ProprietarioRef(
    override val value: UUID,
) : AggregateId() {
    companion object {
        fun toUUID(uuid: String): ProprietarioRef = ProprietarioRef(parse(uuid))
    }
}
