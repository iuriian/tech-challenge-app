package br.com.fiap.oficina.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/actuator/health/**", "/actuator/info")
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/funcionarios/**")
                    .hasRole("FUNCIONARIO")
                    .requestMatchers("/api/v2/funcionarios/**")
                    .hasRole("ATENDENTE")
                    .anyRequest()
                    .authenticated()
            }.oauth2ResourceServer { rs ->
                rs.jwt { it.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }.build()

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter =
        JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(
                JwtGrantedAuthoritiesConverter().apply {
                    setAuthoritiesClaimName("roles")
                    setAuthorityPrefix("ROLE_")
                },
            )
            setPrincipalClaimName("sub")
        }
}
