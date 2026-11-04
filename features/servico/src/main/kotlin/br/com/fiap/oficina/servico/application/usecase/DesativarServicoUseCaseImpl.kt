package br.com.fiap.oficina.servico.application.usecase

import br.com.fiap.oficina.servico.domain.ServicoId
import br.com.fiap.oficina.servico.domain.ServicoRepository
import br.com.fiap.oficina.shared.domain.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
internal class DesativarServicoUseCaseImpl(private val repository: ServicoRepository) : DesativarServicoUseCase {
    @Transactional
    override fun executar(id: String) {
        val servico =
            repository.buscarPorId(ServicoId.toUUID(id))
                ?: throw EntityNotFoundException("Serviço não encontrado com o id: $id")

        repository.salvar(servico.desativar())
    }
}
