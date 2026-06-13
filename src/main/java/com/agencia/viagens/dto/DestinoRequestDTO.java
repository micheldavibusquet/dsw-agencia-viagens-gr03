package com.agencia.viagens.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para receber dados de criação e atualização de destino.
 *
 * Vantagens de usar DTO em vez de expor a entidade diretamente:
 * 1. Segurança: o cliente não pode definir id, notaMedia ou
 *    quantidadeAvaliacoes — esses campos são gerenciados internamente
 * 2. Validação: @NotBlank, @Positive garantem dados válidos antes
 *    de chegarem ao Service
 * 3. Evolução independente: o modelo do banco pode mudar sem
 *    alterar o contrato da API
 *
 * Usado em: POST /destinos e PUT /destinos/{id}
 */
public class DestinoRequestDTO {

    /** Nome do destino — obrigatório, não pode ser vazio */
    @NotBlank(message = "O nome do destino é obrigatório")
    private String nome;

    /** Localização geográfica — obrigatório */
    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;

    /** Descrição detalhada — opcional */
    private String descricao;

    /**
     * Preço base por pessoa — obrigatório e positivo.
     * @Positive garante que o preço seja maior que zero.
     */
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private Double preco;

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
}