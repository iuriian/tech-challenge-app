package br.com.fiap.oficina.cliente.infrastructure.persistence

import br.com.fiap.oficina.cliente.domain.TipoPessoa
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
internal data class DocumentoEmbeddable(
    @Column(name = "documento_numero", nullable = false, unique = true)
    val numero: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    val tipoPessoa: TipoPessoa,
)
