package com.agencia.viagens.model;

import jakarta.persistence.*;

/**
 * Entidade JPA que representa um usuário do sistema.
 *
 * Usada pelo Spring Security para autenticação:
 * o UsuarioDetailsService busca o usuário pelo username e
 * o Spring Security compara a senha fornecida com o hash BCrypt.
 *
 * Perfis de acesso (roles):
 * - ROLE_ADMIN: cadastrar, editar, avaliar e excluir destinos
 * - ROLE_USER:  listar, pesquisar, visualizar e reservar destinos
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Usuario() {}

    public Usuario(String username, String senha, String role) {
        this.username = username;
        this.senha    = senha;
        this.role     = role;
        this.ativo    = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}