package com.agencia.viagens.model;

/**
 * Modelo que representa uma reserva de pacote de viagem.
 *
 * Nota: nesta versão (GR03) a Reserva não é uma entidade JPA (@Entity)
 * pois o foco do desafio é a persistência de Destinos e a segurança.
 * Em uma evolução futura, bastaria adicionar @Entity, @Table e @Id
 * para persistir reservas no PostgreSQL seguindo o mesmo padrão.
 *
 * O valorTotal é calculado automaticamente pelo ReservaService:
 * valorTotal = preço do destino × quantidade de pessoas
 */
public class Reserva {

    /** Identificador único gerado pelo contador AtomicLong no Service */
    private Long id;

    /** ID do destino associado a esta reserva */
    private Long destinoId;

    /** Nome completo do cliente */
    private String nomeCliente;

    /** E-mail do cliente para contato */
    private String emailCliente;

    /** Data prevista para a viagem (formato: YYYY-MM-DD) */
    private String dataViagem;

    /** Quantidade de pessoas no pacote (1 a 20) */
    private Integer quantidadePessoas;

    /** Valor total calculado: preço do destino × quantidade de pessoas */
    private Double valorTotal;

    /** Status da reserva — sempre "CONFIRMADA" ao criar */
    private String status;

    /** Construtor padrão — define status inicial como CONFIRMADA */
    public Reserva() {
        this.status = "CONFIRMADA";
    }

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDestinoId() { return destinoId; }
    public void setDestinoId(Long destinoId) { this.destinoId = destinoId; }

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

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}