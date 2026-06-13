package com.agencia.viagens.service;

import com.agencia.viagens.dto.ReservaRequestDTO;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.Reserva;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Camada de serviço responsável pela lógica de negócio de Reservas.
 *
 * Nota sobre persistência:
 * O GR03 foca em Spring Data JPA para Destinos e Spring Security.
 * As reservas continuam em memória nesta versão — para persistência
 * completa, seria necessário criar a entidade Reserva com @Entity
 * e um ReservaRepository, seguindo o mesmo padrão do DestinoService.
 *
 * Responsabilidades:
 * - Validar se o destino existe antes de criar a reserva
 * - Calcular o valor total (preço do destino × quantidade de pessoas)
 * - Associar a reserva ao destino correto
 */
@Service
public class ReservaService {

    /**
     * DestinoService injetado para validar existência do destino
     * e buscar o preço para cálculo do valor total.
     */
    private final DestinoService destinoService;

    /** Armazenamento em memória — futuro: ReservaRepository com JPA */
    private final List<Reserva> reservas = new ArrayList<>();

    /** Contador de IDs thread-safe para ambiente concorrente */
    private final AtomicLong contadorId = new AtomicLong(1);

    public ReservaService(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    // ─── Criar reserva ───────────────────────────────────────────────────────

    /**
     * Cria uma nova reserva para o destino informado.
     *
     * Regras de negócio:
     * 1. O destino deve existir (DestinoNotFoundException se não existir)
     * 2. O valor total é calculado automaticamente
     *    valorTotal = preço do destino × quantidade de pessoas
     * 3. Status inicial sempre "CONFIRMADA"
     *
     * @param destinoId ID do destino a reservar
     * @param dto       Dados da reserva
     * @return Reserva criada com valor total calculado
     */
    public Reserva reservar(Long destinoId, ReservaRequestDTO dto) {
        // Valida existência do destino e obtém o preço
        Destino destino = destinoService.buscarPorId(destinoId);

        Reserva reserva = new Reserva();
        reserva.setId(contadorId.getAndIncrement());
        reserva.setDestinoId(destinoId);
        reserva.setNomeCliente(dto.getNomeCliente());
        reserva.setEmailCliente(dto.getEmailCliente());
        reserva.setDataViagem(dto.getDataViagem());
        reserva.setQuantidadePessoas(dto.getQuantidadePessoas());

        // Regra de negócio: valor total = preço por pessoa × quantidade
        double valorTotal = destino.getPreco() * dto.getQuantidadePessoas();
        reserva.setValorTotal(valorTotal);

        reservas.add(reserva);
        return reserva;
    }

    // ─── Listar reservas por destino ─────────────────────────────────────────

    /**
     * Retorna todas as reservas de um destino específico.
     *
     * @param destinoId ID do destino
     * @return Lista de reservas do destino
     */
    public List<Reserva> listarPorDestino(Long destinoId) {
        return reservas.stream()
                .filter(r -> r.getDestinoId().equals(destinoId))
                .collect(Collectors.toList());
    }
}