package com.agencia.viagens.dto;

import jakarta.validation.constraints.*;

/**
 * DTO para receber a nota de avaliação de um destino.
 *
 * Restringe a nota ao intervalo válido (1 a 10) antes
 * de chegar ao Service, evitando dados inconsistentes no banco.
 *
 * Usado em: PATCH /destinos/{id}/avaliar
 */
public class AvaliacaoRequestDTO {

    /**
     * Nota de avaliação — obrigatória, entre 1 e 10.
     * @Min e @Max validados automaticamente pelo Spring (@Valid no Controller).
     */
    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 10, message = "A nota máxima é 10")
    private Integer nota;

    // ─── Getters e Setters ───────────────────────────────────────────────────

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
}