package com.agencia.viagens.config;

import com.agencia.viagens.security.JwtAuthFilter;
import com.agencia.viagens.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central do Spring Security.
 *
 * Define três aspectos fundamentais da segurança da API:
 *
 * 1. REGRAS DE ACESSO — quem pode acessar o quê:
 *    - POST /auth/**          → público (login e cadastro)
 *    - GET  /destinos/**      → qualquer usuário autenticado
 *    - POST/PUT/PATCH/DELETE  → apenas ROLE_ADMIN
 *
 * 2. SESSÃO — como o estado é mantido:
 *    - STATELESS: sem sessão no servidor
 *    - Cada requisição deve conter o token JWT
 *    - Permite escalabilidade horizontal
 *
 * 3. AUTENTICAÇÃO — como validar credenciais:
 *    - DaoAuthenticationProvider: busca usuários no banco
 *    - BCryptPasswordEncoder: verifica senhas com hash
 *    - JwtAuthFilter: valida tokens em cada requisição
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          UsuarioDetailsService usuarioDetailsService) {
        this.jwtAuthFilter         = jwtAuthFilter;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /**
     * Define a cadeia de filtros de segurança — o coração da configuração.
     *
     * Ordem de processamento de cada requisição:
     * 1. CSRF desabilitado (APIs REST stateless não precisam)
     * 2. Regras de autorização por endpoint
     * 3. Política de sessão STATELESS
     * 4. JwtAuthFilter executado antes do filtro padrão de autenticação
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Desabilita CSRF — não necessário para APIs REST com JWT
            // CSRF é necessário apenas para aplicações com formulários HTML
            .csrf(csrf -> csrf.disable())

            // Define regras de acesso por endpoint e método HTTP
            .authorizeHttpRequests(auth -> auth

                // Endpoints públicos — não precisam de token
                .requestMatchers("/auth/**").permitAll()

                // Leitura de destinos — qualquer usuário autenticado
                .requestMatchers(HttpMethod.GET, "/destinos/**").authenticated()

                // Operações de escrita — apenas ROLE_ADMIN
                .requestMatchers(HttpMethod.POST, "/destinos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/destinos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/destinos/**").hasRole("ADMIN")

                // Avaliação e reserva — qualquer usuário autenticado
                .requestMatchers(HttpMethod.PATCH, "/destinos/*/avaliar").authenticated()
                .requestMatchers(HttpMethod.POST, "/destinos/*/reservar").authenticated()

                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )

            // Política STATELESS: Spring Security não cria nem usa sessões HTTP
            // Cada requisição é independente e deve conter o token JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Registra o provedor de autenticação com banco de dados e BCrypt
            .authenticationProvider(authenticationProvider())

            // Adiciona o JwtAuthFilter ANTES do filtro padrão de autenticação
            // Isso garante que o token JWT seja validado primeiro
            .addFilterBefore(jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Codificador de senhas com BCrypt.
     *
     * BCrypt é o algoritmo recomendado para senhas porque:
     * - Aplica salt aleatório automaticamente (evita rainbow tables)
     * - É adaptativo: o custo pode ser aumentado conforme hardware evolui
     * - É lento por design: dificulta ataques de força bruta
     *
     * @Bean registra este objeto no contexto Spring para injeção automática
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provedor de autenticação que integra banco de dados e BCrypt.
     *
     * Quando o usuário faz login:
     * 1. DaoAuthenticationProvider chama UsuarioDetailsService
     * 2. UsuarioDetailsService busca o usuário no banco
     * 3. BCryptPasswordEncoder compara a senha fornecida com o hash
     * 4. Se válido → autenticação bem-sucedida
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager — gerenciador central de autenticação.
     *
     * Usado pelo AuthController para processar o login:
     * authManager.authenticate(new UsernamePasswordAuthenticationToken(
     *     username, senha
     * ));
     * Se a autenticação falhar → lança exceção → retorna 401
     * Se válida → retorna Authentication → geramos o JWT
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}