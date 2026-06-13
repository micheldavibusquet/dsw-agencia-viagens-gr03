package com.agencia.viagens.service;

import com.agencia.viagens.dto.AvaliacaoRequestDTO;
import com.agencia.viagens.dto.DestinoRequestDTO;
import com.agencia.viagens.exception.DestinoNotFoundException;
import com.agencia.viagens.model.Destino;
import com.agencia.viagens.repository.DestinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Camada de serviço responsável pela lógica de negócio de Destinos.
 *
 * Evolução do GR02 para o GR03:
 * - GR02: dados em List<Destino> em memória (perdidos ao reiniciar)
 * - GR03: dados persistidos no PostgreSQL via DestinoRepository
 *
 * O Controller não sabe como os dados são armazenados —
 * ele apenas chama métodos do Service. Isso é o princípio
 * de Inversão de Dependência em ação: trocar o banco de dados
 * não afeta o Controller nem os DTOs.
 *
 * Responsabilidades desta camada:
 * - Converter DTO → Entidade (antes de salvar)
 * - Aplicar regras de negócio (cálculo de média de avaliações)
 * - Delegar persistência ao Repository
 * - Lançar exceções de domínio (DestinoNotFoundException)
 */
@Service
public class DestinoService {

    /**
     * Repository injetado via construtor.
     * final garante imutabilidade — boa prática para injeção via construtor.
     */
    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    // ─── 1. Cadastrar destino ────────────────────────────────────────────────

    /**
     * Cadastra um novo destino no banco de dados.
     *
     * Converte DestinoRequestDTO → Destino (entidade JPA)
     * e delega ao Repository para persistência.
     * O banco gera automaticamente o ID (@GeneratedValue).
     *
     * @param dto Dados do novo destino
     * @return Destino criado com ID gerado pelo banco
     */
    public Destino cadastrar(DestinoRequestDTO dto) {
        Destino destino = new Destino();
        destino.setNome(dto.getNome());
        destino.setLocalizacao(dto.getLocalizacao());
        destino.setDescricao(dto.getDescricao());
        destino.setPreco(dto.getPreco());

        // save() do JpaRepository: INSERT INTO destinos (...) VALUES (...)
        return destinoRepository.save(destino);
    }

    // ─── 2. Listar todos ─────────────────────────────────────────────────────

    /**
     * Retorna todos os destinos cadastrados no banco.
     * Query gerada: SELECT * FROM destinos
     *
     * @return Lista de todos os destinos
     */
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    // ─── 3. Pesquisar por nome ou localização ────────────────────────────────

    /**
     * Pesquisa destinos por nome ou localização contendo o termo.
     * Combina os resultados de ambas as buscas sem duplicatas.
     *
     * @param termo Termo de busca (case-insensitive)
     * @return Lista de destinos encontrados
     */
    public List<Destino> pesquisar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarTodos();
        }

        // Busca por nome
        List<Destino> porNome = destinoRepository
                .findByNomeContainingIgnoreCase(termo);

        // Busca por localização e adiciona apenas os não duplicados
        List<Destino> porLocalizacao = destinoRepository
                .findByLocalizacaoContainingIgnoreCase(termo);

        porLocalizacao.stream()
                .filter(d -> !porNome.contains(d))
                .forEach(porNome::add);

        return porNome;
    }

    // ─── 4. Buscar por ID ────────────────────────────────────────────────────

    /**
     * Busca um destino específico pelo ID.
     * Query: SELECT * FROM destinos WHERE id = ?
     *
     * @param id ID do destino
     * @return Destino encontrado
     * @throws DestinoNotFoundException se não existir
     */
    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new DestinoNotFoundException(id));
    }

    // ─── 5. Avaliar destino ──────────────────────────────────────────────────

    /**
     * Recebe uma nota e recalcula a média do destino.
     *
     * Fórmula da média ponderada:
     * nova_media = (media_atual * qtd_avaliacoes + nova_nota) / (qtd + 1)
     *
     * Exemplo: média 8.0 com 2 avaliações + nota 10:
     * nova_media = (8.0 * 2 + 10) / 3 = 26 / 3 = 8.7
     *
     * @param id  ID do destino a avaliar
     * @param dto Nota de avaliação (1 a 10)
     * @return Destino com nova média calculada
     */
    public Destino avaliar(Long id, AvaliacaoRequestDTO dto) {
        Destino destino = buscarPorId(id);

        int novaQuantidade = destino.getQuantidadeAvaliacoes() + 1;
        double novaMedia   = (destino.getNotaMedia()
                * destino.getQuantidadeAvaliacoes()
                + dto.getNota()) / novaQuantidade;

        // Arredonda para 1 casa decimal
        destino.setNotaMedia(Math.round(novaMedia * 10.0) / 10.0);
        destino.setQuantidadeAvaliacoes(novaQuantidade);

        // save() com ID existente → UPDATE no banco
        return destinoRepository.save(destino);
    }

    // ─── 6. Excluir destino ──────────────────────────────────────────────────

    /**
     * Exclui um destino do banco de dados.
     * Query: DELETE FROM destinos WHERE id = ?
     *
     * @param id ID do destino a excluir
     * @throws DestinoNotFoundException se o destino não existir
     */
    public void excluir(Long id) {
        // Verifica se existe antes de excluir
        buscarPorId(id);
        destinoRepository.deleteById(id);
    }
}