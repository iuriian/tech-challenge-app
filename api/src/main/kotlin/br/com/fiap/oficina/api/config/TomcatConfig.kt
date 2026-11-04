package br.com.fiap.oficina.api.config

import org.apache.catalina.Context
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TomcatConfig {
    @Bean
    fun aprCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory?> =
        WebServerFactoryCustomizer { factory: TomcatServletWebServerFactory? ->
            factory?.addContextCustomizers(
                TomcatContextCustomizer { context: Context? ->
                    context?.allowCasualMultipartParsing = true
                },
            )
        }
}
