package br.com.fiap.oficina.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
// jsr250Enabled e obrigatorio: sem ele o Spring ignora os @RolesAllowed dos
// contratos e a autorizacao cai para o anyRequest().authenticated() abaixo,
// liberando qualquer operacao para qualquer perfil autenticado.
//
// proxyTargetClass fica explicito porque os controllers implementam os contratos:
// com proxy JDK o Spring MVC recusa o handler. O Boot forcaria proxy de classe pelo
// default de spring.aop.proxy-target-class, mas a autorizacao nao deve depender disso.
@EnableMethodSecurity(jsr250Enabled = true, proxyTargetClass = true)
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests {
            it
                .requestMatchers("/actuator/health/**", "/actuator/info")
                .permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
                // O papel exigido por operacao fica nos @RolesAllowed dos contratos,
                // junto da documentacao OpenAPI. Aqui so se decide o que e publico e o
                // que exige token: regra de path e de metodo se somam, e uma regra mais
                // restritiva aqui venceria a anotacao sem que ela fosse consultada.
                .anyRequest()
                .authenticated()
        }.oauth2ResourceServer { rs ->
            rs.jwt { it.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
        }.build()

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter = JwtAuthenticationConverter().apply {
        setJwtGrantedAuthoritiesConverter(
            JwtGrantedAuthoritiesConverter().apply {
                setAuthoritiesClaimName("roles")
                setAuthorityPrefix("ROLE_")
            },
        )
        setPrincipalClaimName("sub")
    }
}
