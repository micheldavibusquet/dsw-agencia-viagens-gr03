package com.agencia.viagens.exception;

/**
 * Exceção lançada quando um destino não é encontrado no banco.
 *
 * Estende RuntimeException — não precisa ser declarada no método
 * com "throws", simplificando o código dos Services e Controllers.
 *
 * Quando lançada, o GlobalExceptionHandler a intercepta e
 * retorna automaticamente HTTP 404 Not Found com mensagem clara.
 *
 * Exemplo de uso no Service:
 * destinoRepository.findById(id)
 *     .orElseThrow(() -> new DestinoNotFoundException(id));
 *
 * Resultado para o cliente:
 * HTTP 404
 * {
 *   "status": 404,
 *   "erro": "Destino não encontrado com id: 99",
 *   "timestamp": "2025-12-20T10:30:00"
 * }
 */
public class DestinoNotFoundException extends RuntimeException {

    /**
     * @param id ID do destino que não foi encontrado
     */
    public DestinoNotFoundException(Long id) {
        super("Destino não encontrado com id: " + id);
    }
}