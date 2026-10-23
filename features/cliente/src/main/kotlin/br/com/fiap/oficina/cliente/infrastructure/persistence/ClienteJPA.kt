package br.com.fiap.oficina.cliente.infrastructure.persistence

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "clientes")
internal data class ClienteJPA(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val nome: String,
    @Embedded
    val documento: DocumentoEmbeddable,
    @Column(nullable = false, unique = true)
    val email: String,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    val endereco: EnderecoJPA? = null,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "cliente_id")
    val contatos: MutableList<ContatoJPA> = mutableListOf(),
)
