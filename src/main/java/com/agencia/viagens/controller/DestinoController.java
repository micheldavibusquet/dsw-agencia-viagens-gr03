package com.agencia.viagens.controller;

import com.agencia.viagens.dto.AvaliacaoRequestDTO;
import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para o recurso Destino.
 *
 * Responsabilidade ÚNICA: receber requisições HTTP e
 * delegar ao DestinoService. Zero lógica de negócio aqui.
 *
 * As permissões de acesso são controladas pelo SecurityConfig:
 * - GET  /destinos/**              → autenticado (ROLE_USER ou ROLE_ADMIN)
 * - POST /destinos                 → apenas ROLE_ADMIN
 * - PATCH /destinos/{id}/avaliar   → autenticado
 * - DELETE /destinos/{id}          → apenas ROLE_ADMIN
 *
 * Endpoints:
 * POST   /destinos                  → Cadastrar destino
 * GET    /destinos                  → Listar todos
 * GET    /destinos/pesquisar?termo= → Pesquisar
 * GET    /destinos/{id}             → Buscar por ID
 * PATCH  /destinos/{id}/avaliar     → Avaliar destino
 * DELETE /destinos/{id}             → Excluir destino
 */
@RestController
@RequestMapping("/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    // ─── POST /destinos ──────────────────────────────────────────────────────

    /**
     * Cadastra novo destino — apenas ROLE_ADMIN.
     * @Valid aciona as validações do DestinoRequestDTO antes do método.
     * Retorna 201 Created com o destino criado (incluindo ID do banco).
     */
    @PostMapping
    public ResponseEntity<Destino> cadastrar(
            @Valid @RequestBody DestinoRequestDTO dto) {
        Destino destino = destinoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(destino);
    }

    // ─── GET /destinos ───────────────────────────────────────────────────────

    /**
     * Lista todos os destinos — qualquer usuário autenticado.
     * Retorna 200 OK com array JSON de destinos.
     */
    @GetMapping
    public ResponseEntity<List<Destino>> listarTodos() {
        return ResponseEntity.ok(destinoService.listarTodos());
    }

    // ─── GET /destinos/pesquisar?termo= ──────────────────────────────────────

    /**
     * Pesquisa destinos por nome ou localização.
     * Exemplo: GET /destinos/pesquisar?termo=rio
     * Retorna todos os destinos cujo nome ou localização
     * contenha "rio" (case-insensitive).
     */
    @GetMapping("/pesquisar")
    public ResponseEntity<List<Destino>> pesquisar(
            @RequestParam(required = false) String termo) {
        return ResponseEntity.ok(destinoService.pesquisar(termo));
    }

    // ─── GET /destinos/{id} ──────────────────────────────────────────────────

    /**
     * Retorna informações detalhadas de um destino específico.
     * Lança DestinoNotFoundException (→ 404) se não existir.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Destino> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarPorId(id));
    }

    // ─── PATCH /destinos/{id}/avaliar ────────────────────────────────────────

    /**
     * Recebe nota (1-10) e recalcula a média do destino.
     * Qualquer usuário autenticado pode avaliar.
     */
    @PatchMapping("/{id}/avaliar")
    public ResponseEntity<Destino> avaliar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.ok(destinoService.avaliar(id, dto));
    }

    // ─── DELETE /destinos/{id} ───────────────────────────────────────────────

    /**
     * Exclui um destino — apenas ROLE_ADMIN.
     * Retorna 204 No Content (sem corpo na resposta).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}