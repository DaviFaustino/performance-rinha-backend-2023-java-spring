package com.davifaustino.performancerinhabackend.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.davifaustino.performancerinhabackend.api.PessoaDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@SuppressWarnings("null")
public class PessoasTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.4");

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void beforeAll(@Autowired DatabaseClient databaseClient) throws IOException {
        databaseClient.sql(Files.readString((Paths.get("./src/main/resources/data.sql")))).fetch().rowsUpdated().subscribe();
        postgres.start();
    }

    @AfterAll
    static void afterAll(@Autowired DatabaseClient databaseClient) throws InterruptedException {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configura as propriedades para R2DBC
        registry.add("spring.r2dbc.url", () -> 
            "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/" + postgres.getDatabaseName()
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        // Configura o Flyway para conectar no Testcontainers (JDBC)
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Deve salvar uma pessoa com sucesso")
    void postPessoas1() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null, "Juca", "José", "2000-07-08", Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)  // Utiliza o bodyValue para passar o objeto
                    .exchange()  // Realiza a requisição e executa a troca de dados
                    .expectStatus().isCreated()  // Espera o status HTTP 201
                    .expectBody(UUID.class)  // Espera o corpo como um UUID
                    .consumeWith(result -> {
                        // Verifica se o ID foi gerado
                        assertNotNull(result.getResponseBody());
                    });
    }

    @Test
    @DisplayName("Deve retornar erro 422 por apelido ser nulo")
    void postPessoas2() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null, null, "José", "2000-07-08", Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por apelido já existir")
    void postPessoas3() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null, "Juca", "José", "2000-07-08", Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por nome ser nulo")
    void postPessoas4() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null, "Leão", null, "2000-07-08", Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por nascimento ser nulo")
    void postPessoas5() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null, "Urso", "José", null, Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por nascimento ter formato errado")
    void postPessoas6() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":\"Lopes\",\"nome\":\"José\",\"nascimento\":\"20-12-2000\",\"stack\":null}";
        PessoaDto pessoaDto = objectMapper.readValue(pessoaDtoString, PessoaDto.class);

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por apelido ter tamanho maior que 32")
    void postPessoas7() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null,
                                        "Urso Branco Barrigudo de Orelhas Bem Grandes",
                                        "José",
                                        "2000-07-08",
                                        Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 422 por nome ter tamanho maior que 32")
    void postPessoas8() throws Exception {
        PessoaDto pessoaDto = new PessoaDto(null,
                                        "Urso",
                                        "José Branco Barrigudo de Orelhas Bem Grandes Pereira Góes Machado Vásques de Vás e Melo Brito Cantanhede",
                                        "2000-07-08",
                                        Arrays.asList("Java"));

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(422)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 400 por apelido ser numérico")
    void postPessoas9() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":1,\"nome\":\"José\",\"nascimento\":\"2000-07-08\",\"stack\":[\"Java\"]}";
        PessoaDto pessoaDto = objectMapper.readValue(pessoaDtoString, PessoaDto.class);

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 400 por nome ser numérico")
    void postPessoas10() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":\"Barão\",\"nome\":1.1,\"nascimento\":\"2000-07-08\",\"stack\":[\"Java\"]}";
        PessoaDto pessoaDto = objectMapper.readValue(pessoaDtoString, PessoaDto.class);

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 400 por nascimento ser numérico")
    void postPessoas11() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":\"Lopes\",\"nome\":\"José\",\"nascimento\":2000,\"stack\":[\"Java\"]}";
        PessoaDto pessoaDto = objectMapper.readValue(pessoaDtoString, PessoaDto.class);

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDto)
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 400 por nascimento ser um array")
    void postPessoas12() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":\"Lopes\",\"nome\":\"José\",\"nascimento\":[\"outro\"],\"stack\":null}";

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDtoString)
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar erro 400 por stack possuir valor numérico")
    void postPessoas13() throws Exception {
        String pessoaDtoString = "{\"id\":null,\"apelido\":\"Lopes\",\"nome\":\"José\",\"nascimento\":[\"outro\"],\"stack\":[\"Spring\", 1]}";

        webTestClient.post()
                    .uri("/pessoas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(pessoaDtoString)
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar com sucesso os detalhes de uma pessoa com status 200")
    void getDetalhesPessoa1() throws Exception {

        webTestClient.get()
                    .uri("/pessoas/3f306ba4-bcb1-4c7c-bfcb-78a747225eda")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(PessoaDto.class);
    }

    @Test
    @DisplayName("Deve retornar um erro 404 pela não existência da pessoa")
    void getDetalhesPessoa2() throws Exception {

        webTestClient.get()
                    .uri("/pessoas/a804947b-36a4-4625-9cf9-3ef51f2a8170")
                    .exchange()
                    .expectStatus().isEqualTo(404)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar as pessoas com sucesso com status 200")
    void getPessoas1() throws Exception {

        webTestClient.get()
                    .uri("/pessoas?t=java")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBodyList(PessoaDto.class)
                    .consumeWith(result -> {
                        assertEquals(3, result.getResponseBody().size());
                    });
    }

    @Test
    @DisplayName("Deve retornar as pessoas com sucesso com status 200")
    void getPessoas2() throws Exception {

        webTestClient.get()
                    .uri("/pessoas?t=dino")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBodyList(PessoaDto.class)
                    .consumeWith(result -> {
                        assertEquals(1, result.getResponseBody().size());
                    });
    }

    @Test
    @DisplayName("Deve retornar as pessoas com sucesso com status 200")
    void getPessoas3() throws Exception {

        webTestClient.get()
                    .uri("/pessoas?t=mat")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBodyList(PessoaDto.class)
                    .consumeWith(result -> {
                        assertEquals(1, result.getResponseBody().size());
                    });
    }

    @Test
    @DisplayName("Deve retornar json vazio com status 200")
    void getPessoas4() throws Exception {

        webTestClient.get()
                    .uri("/pessoas?t=dilma")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBodyList(PessoaDto.class)
                    .consumeWith(result -> {
                        assertEquals(0, result.getResponseBody().size());
                    });
    }

    @Test
    @DisplayName("Deve retornar um erro com status 400 por t está em branco")
    void getPessoas5() throws Exception {

        webTestClient.get()
                    .uri("/pessoas?t=")
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar um erro com status 400 por t não ter sido informado")
    void getPessoas6() throws Exception {

        webTestClient.get()
                    .uri("/pessoas")
                    .exchange()
                    .expectStatus().isEqualTo(400)
                    .expectBody(String.class);
    }

    @Test
    @DisplayName("Deve retornar a quantidade de pessoas no banco com status 200")
    void getPessoasCounting() throws Exception {

        webTestClient.get()
                    .uri("/contagem-pessoas")
                    .exchange()
                    .expectStatus().isEqualTo(200)
                    .expectBody(String.class)
                    .consumeWith(result -> {
                        assertEquals("5", result.getResponseBody());
                    });
    }
}
