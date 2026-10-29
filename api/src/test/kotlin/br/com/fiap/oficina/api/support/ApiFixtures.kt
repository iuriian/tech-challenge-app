package br.com.fiap.oficina.api.support

import br.com.fiap.oficina.cliente.application.dto.ClienteRequest
import br.com.fiap.oficina.cliente.application.dto.ClienteResponse
import br.com.fiap.oficina.cliente.application.dto.ContatoRequest
import br.com.fiap.oficina.cliente.application.dto.ContatoResponse
import br.com.fiap.oficina.cliente.application.dto.EnderecoRequest
import br.com.fiap.oficina.cliente.application.dto.EnderecoResponse
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioRequest
import br.com.fiap.oficina.funcionario.application.dto.FuncionarioResponse
import br.com.fiap.oficina.peca.application.dto.PecaRequest
import br.com.fiap.oficina.peca.application.dto.PecaResponse
import br.com.fiap.oficina.veiculo.application.dto.ProprietarioResponse
import br.com.fiap.oficina.veiculo.application.dto.VeiculoRequest
import br.com.fiap.oficina.veiculo.application.dto.VeiculoResponse
import java.math.BigDecimal

/** Massa de teste compartilhada pelos testes de controller do modulo api. */
internal object ApiFixtures {
    const val CLIENTE_ID = "00000000-0000-0000-0000-000000000100"
    const val FUNCIONARIO_ID = "00000000-0000-0000-0000-000000000200"
    const val PECA_ID = "00000000-0000-0000-0000-000000000300"
    const val VEICULO_ID = "00000000-0000-0000-0000-000000000400"
    const val PROPRIETARIO_ID = "00000000-0000-0000-0000-000000000500"

    const val CPF = "01234567890"
    const val NOME_CLIENTE = "Joao Silva"
    const val EMAIL = "joao.silva@email.com"
    const val CODIGO_PECA = "PEC001"
    const val PLACA = "ABC1234"

    fun enderecoRequest() = EnderecoRequest(
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = "Apto 42",
        bairro = "Centro",
        cidade = "Sao Paulo",
        estado = "SP",
        cep = "01001000",
    )

    fun enderecoResponse() = EnderecoResponse(
        logradouro = "Rua das Flores",
        numero = "100",
        complemento = "Apto 42",
        bairro = "Centro",
        cidade = "Sao Paulo",
        estado = "SP",
        cep = "01001000",
    )

    fun clienteRequest(nome: String = NOME_CLIENTE, numeroDocumento: String = CPF) = ClienteRequest(
        nome = nome,
        numeroDocumento = numeroDocumento,
        tipoPessoa = "PESSOA_FISICA",
        email = EMAIL,
        endereco = enderecoRequest(),
        contatos = listOf(ContatoRequest(tipo = "CELULAR", nome = nome, telefone = "11999998888")),
    )

    fun clienteResponse(id: String = CLIENTE_ID, nome: String = NOME_CLIENTE) = ClienteResponse(
        id = id,
        nome = nome,
        numeroDocumento = "012.345.678-90",
        tipoPessoa = "PESSOA_FISICA",
        email = EMAIL,
        endereco = enderecoResponse(),
        contatos = listOf(ContatoResponse(tipo = "CELULAR", nome = nome, telefone = "11999998888")),
    )

    fun funcionarioRequest(nome: String = "Vini Jr.", cargo: String = "MECANICO", cpf: String = CPF) =
        FuncionarioRequest(nome = nome, cargo = cargo, cpf = cpf)

    fun funcionarioResponse(id: String = FUNCIONARIO_ID, nome: String = "Vini Jr.") = FuncionarioResponse(
        id = id,
        nome = nome,
        cargoDescricao = "Mecanico",
        cpf = "012.345.678-90",
    )

    fun pecaRequest(codigo: String = CODIGO_PECA, nome: String = "Pastilha de freio") = PecaRequest(
        codigo = codigo,
        nome = nome,
        descricao = "Pastilha dianteira ceramica",
        fabricante = "Bosch",
        fornecedor = "Distribuidora XYZ",
        precoDeCompra = BigDecimal("80.00"),
        precoDeVenda = BigDecimal("150.00"),
        qtdEstoque = 20,
    )

    fun pecaResponse(id: String = PECA_ID, codigo: String = CODIGO_PECA, qtdEstoque: Int = 20, ativo: Boolean = true) =
        PecaResponse(
            id = id,
            codigo = codigo,
            nome = "Pastilha de freio",
            descricao = "Pastilha dianteira ceramica",
            fabricante = "Bosch",
            fornecedor = "Distribuidora XYZ",
            precoDeCompra = BigDecimal("80.00"),
            precoDeVenda = BigDecimal("150.00"),
            qtdEstoque = qtdEstoque,
            ativo = ativo,
        )

    fun veiculoRequest(placa: String = PLACA, nome: String = "Civic") = VeiculoRequest(
        nome = nome,
        marca = "Honda",
        modelo = "EXL 2.0",
        ano = "2020",
        placa = placa,
        proprietarioId = PROPRIETARIO_ID,
    )

    fun veiculoResponse(id: String = VEICULO_ID, placa: String = PLACA) = VeiculoResponse(
        id = id,
        nome = "Civic",
        marca = "Honda",
        modelo = "EXL 2.0",
        ano = "2020",
        placa = placa,
        proprietario = ProprietarioResponse(
            id = PROPRIETARIO_ID,
            nome = NOME_CLIENTE,
            numeroDocumento = "012.345.678-90",
        ),
    )
}
