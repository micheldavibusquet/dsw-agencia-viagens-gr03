package com.agencia.viagens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação — ponto de entrada do Spring Boot.
 *
 * @SpringBootApplication ativa três recursos simultaneamente:
 *
 * 1. @Configuration — permite definir beans de configuração
 * 2. @EnableAutoConfiguration — configura automaticamente Tomcat,
 *    JPA, Security e Jackson com base nas dependências do pom.xml
 * 3. @ComponentScan — varre os pacotes buscando @Controller,
 *    @Service, @Repository e os registra no contexto Spring
 *
 * Sequência de inicialização ao executar main():
 * 1. Spring lê as dependências e configura automaticamente
 * 2. Tomcat sobe na porta 8080
 * 3. Hibernate conecta ao PostgreSQL e cria as tabelas @Entity
 * 4. Spring Security ativa os filtros JWT
 * 5. Controllers registram os endpoints REST
 * 6. API pronta para receber requisições
 */
@SpringBootApplication
public class ViagensApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViagensApplication.class, args);
    }
}