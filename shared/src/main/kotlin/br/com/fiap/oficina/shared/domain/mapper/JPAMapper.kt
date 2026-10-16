package br.com.fiap.oficina.shared.domain.mapper

interface JPAMapper<Domain, JPA> {
    fun toDomain(jpa: JPA): Domain

    fun toJPA(domain: Domain): JPA
}
