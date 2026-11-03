package br.com.fiap.oficina.servico.application.port

import java.math.BigDecimal

data class ServicoQueryDto(val id: String, val descricao: String, val valor: BigDecimal, val ativo: Boolean)
