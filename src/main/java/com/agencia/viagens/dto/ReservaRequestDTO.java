package com.agencia.viagens.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para receber os dados de uma nova reserva de viagem.
 *
 * Valida todos os campos antes de chegarem ao ReservaService,
 * garantindo que nenhuma reserva seja criada com dados inválidos.
 *
 * Usado em: POST /destinos/{id}/reservar
 */
public class ReservaRequestDTO {

    /** Nome completo do cliente — obrigatório */
    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nomeCliente;

    /**
     * E-mail válido do cliente.
     * @Email valida o formato (usuario@dominio.com)
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String emailCliente;

    /** Data prevista para a viagem — formato YYYY-MM-DD */
    @NotBlank(message = "A data da viagem é obrigatória")
    private String dataViagem;

    /**
     * Quantidade de pessoas no pacote.
     * Mínimo 1 pessoa, máximo 20 por reserva.
     */
    @NotNull(message = "A quantidade de pessoas é obrigatória")
    @Min(value = 1, message = "Mínimo 1 pessoa por reserva")
    @Max(value = 20, message = "Máximo 20 pessoas por reserva")
    private Integer quantidadePessoas;

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getDataViagem() { return dataViagem; }
    public void setDataViagem(String dataViagem) { this.dataViagem = dataViagem; }

    public Integer getQuantidadePessoas() { return quantidadePessoas; }
    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }
}