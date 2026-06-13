package com.agencia.viagens.repository;

import com.agencia.viagens.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA para a entidade Usuario.
 *
 * Papel central na segurança da aplicação:
 * o UsuarioDetailsService usa este repository para buscar
 * o usuário pelo username durante o processo de login.
 *
 * O Spring Security chama loadUserByUsername(username)
 * → que chama findByUsername(username)
 * → que executa: SELECT * FROM usuarios WHERE username = ?
 * → retorna o usuário para validação da senha com BCrypt
 *
 * Por que Optional<Usuario> e não Usuario direto?
 * Optional é uma boa prática Java moderna que força o tratamento
 * explícito do caso "usuário não encontrado", evitando
 * NullPointerException — um dos erros mais comuns em Java.
 *
 * Sem Optional (perigoso):
 *   Usuario u = repo.findByUsername("michel");
 *   u.getSenha(); // NullPointerException se não existir!
 *
 * Com Optional (seguro):
 *   repo.findByUsername("michel")
 *       .orElseThrow(() -> new UsernameNotFoundException("..."));
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário pelo username para autenticação.
     * Query gerada: SELECT * FROM usuarios WHERE username = ?
     *
     * @param username Nome de usuário para login
     * @return Optional com o usuário se encontrado, vazio caso contrário
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica se já existe usuário com o username informado.
     * Usado no cadastro para evitar duplicatas.
     * Query gerada: SELECT COUNT(*) > 0 FROM usuarios WHERE username = ?
     *
     * @param username Username a verificar
     * @return true se já existe, false caso contrário
     */
    boolean existsByUsername(String username);
}