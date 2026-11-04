package br.com.fiap.oficina.cliente.domain

import br.com.fiap.oficina.shared.domain.valueobject.Document
import br.com.fiap.oficina.shared.domain.valueobject.TipoDocumento

enum class TipoPessoa(val tipoDocumento: TipoDocumento) {
    PESSOA_FISICA(TipoDocumento.CPF),
    PESSOA_JURIDICA(TipoDocumento.CNPJ),
}

val Document.tipoPessoa: TipoPessoa
    get() =
        when (this.tipo) {
            TipoDocumento.CPF -> TipoPessoa.PESSOA_FISICA
            TipoDocumento.CNPJ -> TipoPessoa.PESSOA_JURIDICA
        }
