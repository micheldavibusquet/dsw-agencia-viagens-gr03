package com.agencia.viagens.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilitário central para operações com JWT (JSON Web Token).
 *
 * O JWT é composto por três partes separadas por ponto (.):
 *
 * 1. HEADER (Base64): tipo e algoritmo
 *    {"alg": "HS256", "typ": "JWT"}
 *
 * 2. PAYLOAD (Base64): dados do usuário (claims)
 *    {"sub": "michel", "role": "ROLE_ADMIN", "exp": 1234567890}
 *    ⚠️ Base64 NÃO é criptografia — qualquer um pode decodificar!
 *    Nunca coloque senhas ou dados sensíveis no payload.
 *
 * 3. SIGNATURE: garante integridade
 *    HMACSHA256(base64(header) + "." + base64(payload), chaveSecreta)
 *    Se alguém alterar o payload, a assinatura não bate → token inválido
 *
 * Fluxo completo:
 * Login → gerarToken() → cliente guarda o token
 * Próxima requisição → JwtAuthFilter extrai o token do header
 * → extrairUsername() → validarToken() → autoriza ou rejeita
 */
@Component
public class JwtUtil {

    /**
     * Chave secreta lida do application.properties (jwt.secret).
     * @Value injeta o valor da propriedade automaticamente pelo Spring.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tempo de expiração em ms lido do application.properties.
     * 86400000ms = 24 horas.
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Converte a chave secreta hexadecimal em objeto Key
     * compatível com o algoritmo HMAC-SHA256.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(hexStringToByteArray(secret));
    }

    /**
     * Gera um token JWT assinado para o usuário autenticado.
     *
     * O token contém:
     * - subject: username (identifica o usuário)
     * - claim "role": perfil de acesso (ROLE_ADMIN ou ROLE_USER)
     * - issuedAt: momento de criação
     * - expiration: momento de expiração (agora + 24h)
     *
     * @param username Nome do usuário autenticado
     * @param role     Perfil de acesso do usuário
     * @return Token JWT assinado
     */
    public String gerarToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o username (subject) do token JWT.
     *
     * @param token Token JWT
     * @return Username contido no token
     */
    public String extrairUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extrai o perfil de acesso (role) do token JWT.
     *
     * @param token Token JWT
     * @return Role do usuário (ex: ROLE_ADMIN)
     */
    public String extrairRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Valida o token JWT verificando:
     * 1. A assinatura está correta (não foi adulterado)
     * 2. O token não está expirado
     * 3. O username do token corresponde ao usuário informado
     *
     * @param token    Token JWT a validar
     * @param username Username esperado
     * @return true se o token é válido
     */
    public boolean validarToken(String token, String username) {
        try {
            String usernameToken = extrairUsername(token);
            return usernameToken.equals(username) && !isTokenExpirado(token);
        } catch (JwtException e) {
            // Token inválido, adulterado ou expirado
            return false;
        }
    }

    /**
     * Verifica se o token está expirado comparando
     * a data de expiração com o momento atual.
     */
    private boolean isTokenExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extrai todas as claims do token JWT.
     * O parser verifica automaticamente a assinatura —
     * lança JwtException se o token for inválido ou adulterado.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Converte string hexadecimal em array de bytes.
     * Necessário porque a chave é armazenada como hex
     * no application.properties.
     */
    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}