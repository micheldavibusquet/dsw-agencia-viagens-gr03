package com.agencia.viagens.security;

import com.agencia.viagens.service.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta TODAS as requisições HTTP da API.
 *
 * Estende OncePerRequestFilter — garante execução de apenas UMA vez
 * por requisição, evitando validações duplicadas.
 *
 * Posição na cadeia de filtros do Spring Security:
 * Requisição HTTP
 *   → [outros filtros Spring]
 *   → JwtAuthFilter  ← estamos aqui
 *   → SecurityFilterChain (verifica permissões)
 *   → Controller
 *
 * Fluxo de verificação:
 * 1. Lê o header "Authorization: Bearer {token}"
 * 2. Extrai o token (remove o prefixo "Bearer ")
 * 3. Extrai o username do payload do token
 * 4. Carrega o usuário do banco via UsuarioDetailsService
 * 5. Valida o token (assinatura + expiração + username)
 * 6. Registra o usuário no SecurityContext
 * 7. Passa para o próximo filtro
 *
 * Se o token for ausente ou inválido:
 * - Endpoints públicos (POST /auth/login): passam normalmente
 * - Endpoints protegidos: bloqueados pelo SecurityFilterChain → 401
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioDetailsService usuarioDetailsService;

    /** Injeção via construtor — boa prática sobre @Autowired em campo */
    public JwtAuthFilter(JwtUtil jwtUtil,
                         UsuarioDetailsService usuarioDetailsService) {
        this.jwtUtil               = jwtUtil;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lê o header Authorization da requisição HTTP
        String authHeader = request.getHeader("Authorization");

        // 2. Verifica se o header existe e tem o formato correto
        //    Formato esperado: "Bearer eyJhbGci..."
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Sem token — passa para o próximo filtro
            // Endpoints públicos como POST /auth/login funcionam aqui
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove o prefixo "Bearer " (7 caracteres) para obter só o token
        String token    = authHeader.substring(7);
        String username = jwtUtil.extrairUsername(token);

        // 4. Só processa se tiver username E não houver autenticação ativa
        //    SecurityContextHolder.getContext().getAuthentication() == null
        //    evita reprocessar uma requisição já autenticada
        if (username != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Busca o usuário no banco de dados via UsuarioDetailsService
            UserDetails userDetails =
                    usuarioDetailsService.loadUserByUsername(username);

            // 6. Valida o token: assinatura correta + não expirado + username bate
            if (jwtUtil.validarToken(token, userDetails.getUsername())) {

                // 7. Cria objeto de autenticação com as permissões do usuário
                //    authorities contém as ROLES: [ROLE_ADMIN] ou [ROLE_USER]
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,        // principal (quem é)
                                null,               // credentials (não necessário após autenticação)
                                userDetails.getAuthorities() // permissões
                        );

                // Adiciona detalhes da requisição (IP, session) ao token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Registra no SecurityContext — a partir daqui o Spring
                //    sabe quem está fazendo a requisição e quais são suas permissões
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Passa para o próximo filtro da cadeia independente do resultado
        filterChain.doFilter(request, response);
    }
}