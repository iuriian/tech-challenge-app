package br.com.fiap.oficina.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["br.com.fiap.oficina"])
@EntityScan(basePackages = ["br.com.fiap.oficina"])
@EnableJpaRepositories(basePackages = ["br.com.fiap.oficina"])
class OfficinaApplication

fun main(args: Array<String>) {
    runApplication<OfficinaApplication>(*args)
}
