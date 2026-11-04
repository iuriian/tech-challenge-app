package br.com.fiap.oficina.servico.application.dto

import java.math.BigDecimal

data class ServicoResponse(val id: String, val descricao: String, val valor: BigDecimal, val ativo: Boolean)
