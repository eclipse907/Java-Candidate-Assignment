package com.infinum.assignment.application.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors { }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            authorizeRequests {
                authorize(HttpMethod.GET, "api/v1/authors/{id}", permitAll)
                authorize(HttpMethod.POST, "api/v1/authors", hasAuthority("SCOPE_ADMIN"))
                authorize(HttpMethod.PATCH, "api/v1/authors/{id}", hasAuthority("SCOPE_ADMIN"))
                authorize(HttpMethod.GET, "api/v1/authors", permitAll)
                authorize(HttpMethod.GET, "api/v1/books/isbn/{isbn}", permitAll)
                authorize(HttpMethod.POST, "api/v1/books", hasAuthority("SCOPE_AUTHOR"))
                authorize(HttpMethod.GET, "api/v1/books/title/{title}", permitAll)
                authorize(HttpMethod.GET, "api/v1/books/genre/{genre}", permitAll)
                authorize(HttpMethod.GET, "api/v1/books", permitAll)
                authorize(HttpMethod.GET, "api/v1/books/isbn/{isbn}/authors", permitAll)
                authorize(HttpMethod.GET, "api/v1/authors/{id}/books", permitAll)
            }
            oauth2ResourceServer {
                jwt {}
            }
        }
        return http.build()
    }

}