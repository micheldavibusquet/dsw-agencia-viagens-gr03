package com.agencia.viagens.controller;

import com.agencia.viagens.dto.ReservaRequestDTO;
import com.agencia.viagens.model.Reserva;
import com.agencia.viagens.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para o recurso Reserva.
 *
 * Responsabilidade ÚNICA: receber requisições HTTP e
 * delegar ao ReservaService. Zero lógica de negócio aqui.
 *
 * Permissões controladas pelo SecurityConfig:
 * - POST /destinos/{id}/reservar  → qualquer usuário autenticado
 * - GET  /destinos/{id}/reservas  → qualquer usuário autenticado
 *
 * Endpoints:
 * POST /destinos/{id}/reservar → Reservar pacote de viagem
 * GET  /destinos/{id}/reservas → Listar reservas do destino
 */
@RestController
@RequestMapping("/destinos")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // ─── POST /destinos/{id}/reservar ────────────────────────────────────────

    /**
     * Reserva um pacote de viagem para o destino informado.
     *
     * Regras de negócio (aplicadas no ReservaService):
     * - O destino deve existir (404 se não existir)
     * - Valor total calculado automaticamente (preço × qtd pessoas)
     * - Status inicial: CONFIRMADA
     *
     * Retorna 201 Created com a reserva criada.
     *
     * Exemplo de requisição:
     * POST /destinos/1/reservar
     * Authorization: Bearer {token}
     * {
     *   "nomeCliente": "Michel Busquet",
     *   "emailCliente": "michel@email.com",
     *   "dataViagem": "2025-12-20",
     *   "quantidadePessoas": 2
     * }
     */
    @PostMapping("/{id}/reservar")
    public ResponseEntity<Reserva> reservar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO dto) {
        Reserva reserva = reservaService.reservar(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    // ─── GET /destinos/{id}/reservas ─────────────────────────────────────────

    /**
     * Lista todas as reservas de um destino específico.
     * Retorna 200 OK com array de reservas (pode ser vazio).
     */
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<Reserva>> listarReservas(
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.listarPorDestino(id));
    }
}