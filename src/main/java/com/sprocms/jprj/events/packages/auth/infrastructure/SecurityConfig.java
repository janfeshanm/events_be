package com.sprocms.jprj.events.packages.auth.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/s/**").authenticated()
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults());
        // .exceptionHandling((exceptions) -> exceptions
        // .authenticationEntryPoint(new EventsOAuth2AuthenticationEntryPoint())
        // .accessDeniedHandler(new EventsOAuth2AccessDeniedHandler()));

        //http.headers().frameOptions().disable();// for h2-console
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        //http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        http.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        // http.oauth2ResourceServer()
        // .jwt()
        // .jwtAuthenticationConverter(authenticationConverter());

        return http.build();
    }
}
