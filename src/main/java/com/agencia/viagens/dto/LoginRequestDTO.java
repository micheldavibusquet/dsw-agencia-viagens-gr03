package com.agencia.viagens.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para receber as credenciais de login.
 *
 * Separa o contrato de autenticação do modelo interno (Usuario),
 * evitando expor campos desnecessários como id, ativo e role.
 *
 * Usado em: POST /auth/login
 */
public class LoginRequestDTO {

    /** Username do usuário — campo obrigatório */
    @NotBlank(message = "O username é obrigatório")
    private String username;

    /** Senha em texto plano — será comparada com o hash BCrypt */
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}