package com.davifaustino.performancerinhabackend.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.davifaustino.performancerinhabackend.domain.PessoaDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PessoasTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .expectBody(UUID.class)  // Espera o corpo como um UUID (o id da pessoa)
                .consumeWith(result -> {
                    // Verifique se o ID foi gerado
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor de campo nulo", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Apelido existente", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor de campo nulo", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor de campo nulo", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Nascimento em formato errado", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor de campo ultrapassa tamanho", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor de campo ultrapassa tamanho", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor numerico em vez de string", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor numerico em vez de string", result.getResponseBody());
                });
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
                .expectBody(String.class)
                .consumeWith(result -> {
                    assertEquals("Error message: Valor numerico em vez de string", result.getResponseBody());
                });
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
}
