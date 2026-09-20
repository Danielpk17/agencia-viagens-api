package com.senai.agenciaviagens.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.agenciaviagens.exception.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN = "ROLE_ADMIN";
    private static final String USER = "ROLE_USER";

    private static final String[] ROTAS_PUBLICAS = {
            "/api/publico/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error"
    };

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(ROTAS_PUBLICAS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/destinos/*/avaliacoes").hasAnyAuthority(USER, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/destinos/*/avaliacoes").hasAnyAuthority(USER, ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/destinos", "/api/destinos/**").hasAnyAuthority(USER, ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/destinos").hasAuthority(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/destinos/**").hasAuthority(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/destinos/**").hasAuthority(ADMIN)
                        .requestMatchers("/api/avaliacoes/**").hasAuthority(ADMIN)
                        .requestMatchers("/api/usuarios/**").hasAuthority(ADMIN)
                        .requestMatchers("/api/auth/**").authenticated()
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint()))
                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuracao) throws Exception {
        return configuracao.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (requisicao, resposta, excecao) -> escrever(requisicao, resposta,
                HttpStatus.UNAUTHORIZED, "Nao autenticado",
                "Informe usuario e senha validos no cabecalho Authorization");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (requisicao, resposta, excecao) -> escrever(requisicao, resposta,
                HttpStatus.FORBIDDEN, "Acesso negado",
                "O perfil autenticado nao possui permissao para esta operacao");
    }

    private void escrever(HttpServletRequest requisicao, HttpServletResponse resposta,
                          HttpStatus status, String erro, String mensagem) throws IOException {
        resposta.setStatus(status.value());
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setCharacterEncoding("UTF-8");
        ErroResponse corpo = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                requisicao.getRequestURI(),
                null);
        objectMapper.writeValue(resposta.getWriter(), corpo);
    }
}
