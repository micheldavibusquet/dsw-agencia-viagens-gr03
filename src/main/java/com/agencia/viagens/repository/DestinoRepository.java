package com.agencia.viagens.repository;

import com.agencia.viagens.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA para a entidade Destino.
 *
 * Ao estender JpaRepository<Destino, Long>, o Spring Data JPA
 * gera automaticamente a implementação completa em runtime.
 * Não precisamos escrever nenhuma linha de SQL para operações básicas.
 *
 * Métodos CRUD herdados automaticamente:
 * ┌─────────────────────────────────────────────────────────────────┐
 * │ save(destino)      → INSERT ou UPDATE (detecta automaticamente) │
 * │ findById(id)       → SELECT * FROM destinos WHERE id = ?        │
 * │ findAll()          → SELECT * FROM destinos                     │
 * │ deleteById(id)     → DELETE FROM destinos WHERE id = ?          │
 * │ existsById(id)     → SELECT COUNT(*) > 0 WHERE id = ?          │
 * │ count()            → SELECT COUNT(*) FROM destinos              │
 * └─────────────────────────────────────────────────────────────────┘
 *
 * Métodos derivados do nome — Spring gera a query automaticamente:
 * findByNomeContainingIgnoreCase("rio")
 * → SELECT * FROM destinos WHERE LOWER(nome) LIKE LOWER('%rio%')
 */
@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    /**
     * Pesquisa destinos cujo NOME contenha o termo (case-insensitive).
     * Exemplo: pesquisar("rio") encontra "Rio de Janeiro" e "Ribeirão Preto"
     *
     * @param nome Termo de busca parcial
     * @return Lista de destinos encontrados
     */
    List<Destino> findByNomeContainingIgnoreCase(String nome);

    /**
     * Pesquisa destinos por LOCALIZAÇÃO contendo o termo (case-insensitive).
     * Exemplo: pesquisar("janeiro") encontra "Rio de Janeiro"
     *
     * @param localizacao Termo de busca parcial
     * @return Lista de destinos encontrados
     */
    List<Destino> findByLocalizacaoContainingIgnoreCase(String localizacao);
}