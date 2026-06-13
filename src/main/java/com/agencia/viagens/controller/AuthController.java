package com.agencia.viagens.controller;

import com.agencia.viagens.dto.LoginRequestDTO;
import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.UsuarioRepository;
import com.agencia.viagens.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de autenticação — endpoints públicos da API.
 *
 * Responsabilidades:
 * 1. POST /auth/login   → autentica usuário e retorna JWT
 * 2. POST /auth/cadastro → cadastra novo usuário com senha BCrypt
 *
 * Estes endpoints são PUBLIC no SecurityConfig (permitAll()),
 * pois o usuário ainda não tem token para se autenticar.
 *
 * Fluxo do login:
 * 1. Cliente envia username + senha
 * 2. AuthenticationManager valida contra o banco via BCrypt
 * 3. Se válido → JwtUtil gera o token
 * 4. Token retornado ao cliente
 * 5. Cliente usa o token em todas as próximas requisições:
 *    Authorization: Bearer {token}
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil               = jwtUtil;
        this.usuarioRepository     = usuarioRepository;
        this.passwordEncoder       = passwordEncoder;
    }

    // ─── POST /auth/login ────────────────────────────────────────────────────

    /**
     * Autentica o usuário e retorna um token JWT.
     *
     * O AuthenticationManager delega para o DaoAuthenticationProvider
     * que usa o UsuarioDetailsService para buscar o usuário no banco
     * e o BCryptPasswordEncoder para verificar a senha.
     *
     * Resposta de sucesso (200 OK):
     * {
     *   "token": "eyJhbGci...",
     *   "tipo": "Bearer",
     *   "username": "michel",
     *   "role": "ROLE_ADMIN"
     * }
     *
     * Resposta de erro (401 Unauthorized):
     * {
     *   "erro": "Credenciais inválidas"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        try {
            // Autentica via Spring Security
            // Lança BadCredentialsException se inválido
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUsername(),
                            dto.getSenha()
                    )
            );

            // Busca a role do usuário no banco para incluir no token
            Usuario usuario = usuarioRepository
                    .findByUsername(dto.getUsername())
                    .orElseThrow();

            // Gera o token JWT com username e role
            String token = jwtUtil.gerarToken(
                    usuario.getUsername(),
                    usuario.getRole()
            );

            // Monta a resposta com o token e informações do usuário
            Map<String, String> resposta = new HashMap<>();
            resposta.put("token", token);
            resposta.put("tipo", "Bearer");
            resposta.put("username", usuario.getUsername());
            resposta.put("role", usuario.getRole());

            return ResponseEntity.ok(resposta);

        } catch (AuthenticationException e) {
            // Credenciais inválidas — não revelar detalhes específicos
            // (não dizer se o usuário não existe ou se a senha está errada)
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Credenciais inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
        }
    }

    // ─── POST /auth/cadastro ─────────────────────────────────────────────────

    /**
     * Cadastra um novo usuário no sistema.
     *
     * A senha é criptografada com BCrypt antes de salvar —
     * NUNCA armazenamos senha em texto plano.
     *
     * Por padrão, novos usuários recebem ROLE_USER.
     * ROLE_ADMIN deve ser atribuído diretamente no banco
     * por um administrador — boa prática de segurança.
     *
     * Resposta de sucesso (201 Created):
     * {
     *   "mensagem": "Usuário cadastrado com sucesso",
     *   "username": "michel"
     * }
     */
    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, String>> cadastrar(
            @Valid @RequestBody LoginRequestDTO dto) {

        // Verifica se o username já está em uso
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", "Username já está em uso");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
        }

        // Criptografa a senha com BCrypt antes de salvar
        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        // Novos usuários sempre começam como ROLE_USER
        Usuario usuario = new Usuario(
                dto.getUsername(),
                senhaCriptografada,
                "ROLE_USER"
        );

        usuarioRepository.save(usuario);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Usuário cadastrado com sucesso");
        resposta.put("username", usuario.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}