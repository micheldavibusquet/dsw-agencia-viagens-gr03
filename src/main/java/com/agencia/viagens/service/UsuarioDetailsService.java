package com.agencia.viagens.service;

import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço que integra o Spring Security com o banco de dados.
 *
 * Implementa a interface UserDetailsService do Spring Security —
 * contrato que o framework usa para carregar usuários durante
 * o processo de autenticação.
 *
 * Fluxo de autenticação no login:
 * 1. Cliente envia POST /auth/login com username e senha
 * 2. Spring Security chama loadUserByUsername(username)
 * 3. Este método busca o usuário no PostgreSQL via UsuarioRepository
 * 4. Retorna um UserDetails com username, senha (hash BCrypt) e roles
 * 5. Spring Security compara a senha fornecida com o hash BCrypt
 * 6. Se válido → gera JWT e retorna ao cliente
 * 7. Se inválido → retorna 401 Unauthorized
 *
 * Por que implementar UserDetailsService?
 * Por padrão, o Spring Security usa usuários em memória.
 * Ao implementar esta interface, dizemos ao Spring:
 * "use o banco de dados para buscar usuários, não a memória"
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carrega o usuário do banco de dados pelo username.
     *
     * Chamado automaticamente pelo Spring Security durante:
     * - Login (AuthController → AuthenticationManager)
     * - Validação de token (JwtAuthFilter)
     *
     * @param username Nome de usuário fornecido no login
     * @return UserDetails com dados do usuário para o Spring Security
     * @throws UsernameNotFoundException se o usuário não existir no banco
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Busca o usuário no banco — lança exceção se não encontrar
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + username));

        // Verifica se o usuário está ativo
        // Usuários inativos não podem autenticar mesmo com senha correta
        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException(
                    "Usuário inativo: " + username);
        }

        // Converte a role do banco (ex: "ROLE_ADMIN") em GrantedAuthority
        // que o Spring Security usa para verificar permissões nos endpoints
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(usuario.getRole());

        // Retorna o UserDetails que o Spring Security vai usar para:
        // 1. Comparar a senha com BCrypt
        // 2. Verificar as permissões (authorities/roles)
        return new User(
                usuario.getUsername(),  // username
                usuario.getSenha(),     // senha hash BCrypt
                List.of(authority)      // roles: [ROLE_ADMIN] ou [ROLE_USER]
        );
    }
}