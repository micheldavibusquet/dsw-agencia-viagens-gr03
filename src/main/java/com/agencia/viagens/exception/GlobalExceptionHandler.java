package com.agencia.viagens.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento centralizado de exceções da API.
 *
 * @RestControllerAdvice intercepta exceções lançadas em qualquer
 * Controller e retorna respostas JSON padronizadas.
 *
 * Vantagens desta abordagem:
 * 1. Consistência: todos os erros têm o mesmo formato JSON
 * 2. Separação: Controllers não precisam tratar erros
 * 3. Manutenção: um único lugar para alterar o formato de erro
 *
 * Formato padrão de erro retornado:
 * {
 *   "timestamp": "2025-12-20T10:30:00",
 *   "status": 404,
 *   "erro": "Destino não encontrado com id: 99"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata destino não encontrado → HTTP 404 Not Found.
     * Lançada pelo DestinoService quando findById() não retorna resultado.
     */
    @ExceptionHandler(DestinoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            DestinoNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Trata falhas de validação dos DTOs → HTTP 400 Bad Request.
     * Lançada quando @Valid falha em um @RequestBody.
     * Exemplos: campo obrigatório vazio, nota fora do intervalo 1-10.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        // Concatena todas as mensagens de erro de validação
        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "; " + b);

        return buildResponse(HttpStatus.BAD_REQUEST, mensagem);
    }

    /**
     * Trata acesso negado → HTTP 403 Forbidden.
     * Lançada quando um ROLE_USER tenta acessar endpoint de ROLE_ADMIN.
     * Exemplo: ROLE_USER tentando DELETE /destinos/{id}
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN,
                "Acesso negado: você não tem permissão para esta operação");
    }

    /**
     * Trata falha de autenticação → HTTP 401 Unauthorized.
     * Lançada quando o token JWT é inválido ou ausente.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED,
                "Autenticação necessária: " + ex.getMessage());
    }

    /**
     * Trata qualquer outra exceção não mapeada → HTTP 500.
     * Captura erros inesperados sem expor detalhes internos ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor: " + ex.getMessage());
    }

    /**
     * Monta o corpo padrão de resposta de erro.
     *
     * @param status  Código HTTP do erro
     * @param mensagem Descrição do erro
     * @return ResponseEntity com corpo JSON padronizado
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String mensagem) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("erro", mensagem);

        return ResponseEntity.status(status).body(body);
    }
}