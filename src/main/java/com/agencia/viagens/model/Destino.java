package com.agencia.viagens.model;

import jakarta.persistence.*;

/**
 * Entidade JPA que representa um destino de viagem.
 *
 * Diferença fundamental em relação ao GR02:
 * - GR02: classe Java simples (POJO) — dados perdidos ao reiniciar
 * - GR03: entidade JPA com @Entity — dados persistidos no PostgreSQL
 *
 * O Hibernate lê esta classe e cria automaticamente a tabela 'destinos':
 * CREATE TABLE destinos (
 *   id                    BIGSERIAL PRIMARY KEY,
 *   nome                  VARCHAR NOT NULL,
 *   localizacao           VARCHAR NOT NULL,
 *   descricao             TEXT,
 *   preco                 DOUBLE PRECISION NOT NULL,
 *   nota_media            DOUBLE PRECISION,
 *   quantidade_avaliacoes INTEGER
 * )
 */
@Entity
@Table(name = "destinos")
public class Destino {

    /**
     * Chave primária gerada automaticamente pelo banco.
     * IDENTITY: usa auto_increment/serial do PostgreSQL.
     * O banco garante unicidade — nunca dois destinos com o mesmo ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do destino — obrigatório, não pode ser nulo no banco */
    @Column(name = "nome", nullable = false)
    private String nome;

    /** Localização geográfica — obrigatório */
    @Column(name = "localizacao", nullable = false)
    private String localizacao;

    /**
     * Descrição detalhada do destino.
     * columnDefinition = "TEXT" permite textos longos
     * sem limite de caracteres (diferente de VARCHAR).
     */
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    /** Preço base do pacote por pessoa — obrigatório */
    @Column(name = "preco", nullable = false)
    private Double preco;

    /**
     * Média das avaliações (0.0 a 10.0).
     * Calculada pelo DestinoService a cada nova avaliação
     * usando a fórmula: (media_atual * qtd + nova_nota) / (qtd + 1)
     */
    @Column(name = "nota_media")
    private Double notaMedia = 0.0;

    /** Total de avaliações recebidas — usado no cálculo da média */
    @Column(name = "quantidade_avaliacoes")
    private Integer quantidadeAvaliacoes = 0;

    // ─── Construtores ────────────────────────────────────────────────────────

    /** Construtor padrão obrigatório para o JPA/Hibernate */
    public Destino() {
        this.notaMedia            = 0.0;
        this.quantidadeAvaliacoes = 0;
    }

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public Double getNotaMedia() { return notaMedia; }
    public void setNotaMedia(Double notaMedia) { this.notaMedia = notaMedia; }

    public Integer getQuantidadeAvaliacoes() { return quantidadeAvaliacoes; }
    public void setQuantidadeAvaliacoes(Integer q) { this.quantidadeAvaliacoes = q; }
}