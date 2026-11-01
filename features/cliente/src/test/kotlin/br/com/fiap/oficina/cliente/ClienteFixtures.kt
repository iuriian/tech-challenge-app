package br.com.fiap.oficina.cliente

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ContatoRequest
import br.com.fiap.oficina.cliente.application.dto.EnderecoRequest
import br.com.fiap.oficina.cliente.domain.Cliente
import br.com.fiap.oficina.cliente.domain.Contato
import br.com.fiap.oficina.cliente.domain.Endereco
import br.com.fiap.oficina.cliente.domain.TipoPessoa
import br.com.fiap.oficina.cliente.infrastructure.persistence.ClienteJPA
import br.com.fiap.oficina.cliente.infrastructure.persistence.ContatoJPA
import br.com.fiap.oficina.cliente.infrastructure.persistence.DocumentoEmbeddable
import br.com.fiap.oficina.cliente.infrastructure.persistence.EnderecoJPA
import br.com.fiap.oficina.shared.domain.valueobject.Document
import java.util.UUID

/** Massa de teste compartilhada pelos testes de cliente. */
internal object ClienteFixtures {
    const val ID_1 = "00000000-0000-0000-0000-000000000100"
    const val ID_2 = "00000000-0000-0000-0000-000000000200"
    const val ENDERECO_ID = "00000000-0000-0000-0000-000000000300"
    const val CONTATO_ID = "00000000-0000-0000-0000-000000000400"

    const val CPF = "01234567890"
    const val CPF_OUTRO = "52998224725"
    const val CNPJ = "11222333000181"

    const val NOME = "João Silva"
    const val EMAIL = "joao.silva@email.com"

    fun endereco(id: String = ENDERECO_ID, complemento: String? = "Apto 42") = Endereco.reconstruir(
        id = id,
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = complemento,
        bairro = "Centro",
        cidade = "São Paulo",
        estado = "SP",
        cep = "01001000",
    )

    fun contato(id: String = CONTATO_ID, tipo: String = "CELULAR") = Contato.reconstruir(
        id = id,
        tipo = tipo,
        nome = "João Silva",
        telefone = "11999998888",
    )

    fun cliente(
        id: String = ID_1,
        nome: String = NOME,
        documento: String = CPF,
        email: String = EMAIL,
        endereco: Endereco? = endereco(),
        contatos: List<Contato> = listOf(contato()),
    ) = Cliente.reconstruir(
        id = id,
        nome = nome,
        documento = Document.from(documento),
        email = email,
        endereco = endereco,
        contatos = contatos,
    )

    fun enderecoRequest(complemento: String? = "Apto 42") = EnderecoRequest(
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = complemento,
        bairro = "Centro",
        cidade = "São Paulo",
        estado = "SP",
        cep = "01001000",
    )

    fun contatoRequest(tipo: String = "CELULAR") = ContatoRequest(
        tipo = tipo,
        nome = "João Silva",
        telefone = "11999998888",
    )

    fun clienteRequest(
        nome: String = NOME,
        numeroDocumento: String = CPF,
        tipoPessoa: String = TipoPessoa.PESSOA_FISICA.name,
        email: String = EMAIL,
        endereco: EnderecoRequest? = enderecoRequest(),
        contatos: List<ContatoRequest> = listOf(contatoRequest()),
    ) = ClienteRequest(
        nome = nome,
        numeroDocumento = numeroDocumento,
        tipoPessoa = tipoPessoa,
        email = email,
        endereco = endereco,
        contatos = contatos,
    )

    fun enderecoJPA(id: String = ENDERECO_ID, complemento: String? = "Apto 42") = EnderecoJPA(
        id = UUID.fromString(id),
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = complemento,
        bairro = "Centro",
        cidade = "São Paulo",
        estado = "SP",
        cep = "01001000",
    )

    fun contatoJPA(id: String = CONTATO_ID, tipo: String = "CELULAR") = ContatoJPA(
        id = UUID.fromString(id),
        tipo = tipo,
        nome = "João Silva",
        telefone = "11999998888",
    )

    fun clienteJPA(
        id: String = ID_1,
        nome: String = NOME,
        documento: String = CPF,
        tipoPessoa: TipoPessoa = TipoPessoa.PESSOA_FISICA,
        email: String = EMAIL,
        endereco: EnderecoJPA? = enderecoJPA(),
        contatos: MutableList<ContatoJPA> = mutableListOf(contatoJPA()),
    ) = ClienteJPA(
        id = UUID.fromString(id),
        nome = nome,
        documento = DocumentoEmbeddable(documento, tipoPessoa),
        email = email,
        endereco = endereco,
        contatos = contatos,
    )
}
