package br.com.fiap.oficina.servico.infrastructure.persistence

import br.com.fiap.oficina.servico.ServicoFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Infraestrutura - Entidade JPA de Serviço")
class ServicoJPATest {
    @Test
    @DisplayName("Dado a entidade, quando instanciar sem argumentos, então o Hibernate deve conseguir materializá-la")
    fun givenEntity_whenInstantiatingWithoutArguments_thenHibernateCanMaterializeIt() {
        // O construtor sem argumentos vem do plugin kotlin.plugin.jpa, nao do codigo:
        // sem ele o Hibernate nao instancia a entidade e o erro so aparece em runtime.
        val construtor = ServicoJPA::class.java.getDeclaredConstructor()
        construtor.isAccessible = true

        assertNotNull(construtor.newInstance())
    }

    @Test
    @DisplayName("Dado os campos do catálogo, quando construir a entidade, então deve expô-los sem conversão")
    fun givenCatalogFields_whenBuildingEntity_thenExposeThemWithoutConversion() {
        val jpa = ServicoFixtures.servicoJPA(ativo = false)

        assertEquals(UUID.fromString(ServicoFixtures.ID_1), jpa.id)
        assertEquals(ServicoFixtures.DESCRICAO, jpa.descricao)
        assertEquals(ServicoFixtures.VALOR, jpa.valor)
        assertEquals(false, jpa.ativo)
    }

    @Test
    @DisplayName("Dado duas entidades com os mesmos campos, quando comparar, então devem ser iguais")
    fun givenTwoEntitiesWithSameFields_whenComparing_thenTheyAreEqual() {
        assertEquals(ServicoFixtures.servicoJPA(), ServicoFixtures.servicoJPA())
        assertEquals(ServicoFixtures.servicoJPA().hashCode(), ServicoFixtures.servicoJPA().hashCode())
    }

    @Test
    @DisplayName("Dado uma entidade, quando alterar a situação por copy, então deve manter os demais campos")
    fun givenEntity_whenChangingStatusByCopy_thenKeepRemainingFields() {
        val jpa = ServicoFixtures.servicoJPA(ativo = true)

        val desativado = jpa.copy(ativo = false)

        assertEquals(jpa.id, desativado.id)
        assertEquals(jpa.descricao, desativado.descricao)
        assertEquals(jpa.valor, desativado.valor)
        assertNotEquals(jpa, desativado)
    }
}
