package com.agencia.viagens.model;

import jakarta.persistence.*;

/**
 * Entidade JPA que representa um usuário do sistema.
 *
 * Esta entidade é usada pelo Spring Security para autenticação:
 * o UsuarioDetailsService busca o usuário pelo username e
 * o Spring Security compara a senha fornecida com o hash BCrypt.
 *
 * O Hibernate cria automaticamente a tabela 'usuarios':
 * CREATE TABLE usuarios (
 *   id       BIGSERIAL PRIMARY KEY,
 *   username VARCHAR UNIQUE NOT NULL,
 *   senha    VARCHAR NOT NULL,
 *   role     VARCHAR NOT NULL,
 *   ativo    BOOLEAN NOT NULL DEFAULT TRUE
 * )
 *
 * Perfis de acesso (roles):
 * - ROLE_ADMIN: cadastrar, editar, avaliar e excluir destinos
 * - ROLE_USER:  listar, pesquisar, visualizar e reservar destinos
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    /**
     * Chave primária gerada automaticamente pelo banco.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome de usuário único — usado para login.
     * unique = true cria um índice UNIQUE no banco,
     * garantindo que não existam dois usuários com o mesmo username.
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;