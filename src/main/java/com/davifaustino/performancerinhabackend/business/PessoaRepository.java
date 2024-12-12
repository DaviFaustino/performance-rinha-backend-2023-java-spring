package com.davifaustino.performancerinhabackend.business;

import java.util.UUID;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PessoaRepository {

    private final DatabaseClient databaseClient;

    public PessoaRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<Long> savePessoa(Pessoa pessoa) {
        GenericExecuteSpec genericExecuteSpec;
        genericExecuteSpec = databaseClient.sql("INSERT INTO tb_pessoas VALUES (:id, :apelido, :nome, :nascimento, :stack, :busca)")
                                            .bind("id", pessoa.getId())
                                            .bind("apelido", pessoa.getApelido())
                                            .bind("nome", pessoa.getNome())
                                            .bind("nascimento", pessoa.getNascimento())
                                            .bind("busca", pessoa.getBusca());

        if (pessoa.getStack() == null) {
            return genericExecuteSpec.bindNull("stack", String[].class)
                                    .fetch()
                                    .rowsUpdated();
        } else {
            return genericExecuteSpec.bind("stack", pessoa.getStack())
                                    .fetch()
                                    .rowsUpdated();
        }
    }

    public Mono<Pessoa> getOnePessoa(UUID id) {
        return databaseClient.sql("SELECT * FROM tb_pessoas WHERE id = :id")
                            .bind("id", id) // Bind do parâmetro :id
                            .map((row, metadata) -> new Pessoa(row.get("id", UUID.class),
                                                                row.get("apelido", String.class),
                                                                row.get("nome", String.class),
                                                                row.get("nascimento", String.class),
                                                                row.get("stack", String[].class)))
                            .one(); // Retorna Mono<Pessoa>
    }

    public Flux<Pessoa> getPessoas(String termo) {

        return databaseClient.sql("SELECT * FROM tb_pessoas WHERE busca ILIKE '%' || :termo || '%' LIMIT 50")
                            .bind("termo", termo)
                            .map((row, metadata) -> new Pessoa(row.get("id", UUID.class),
                                                                row.get("apelido", String.class),
                                                                row.get("nome", String.class),
                                                                row.get("nascimento", String.class),
                                                                row.get("stack", String[].class)))
                            .all(); // Retorna Flux<Pessoa>
    }

    public Mono<Integer> getPessoasCounting() {

        return databaseClient.sql("SELECT COUNT(*) FROM tb_pessoas")
                            .map((row, metadata) -> row.get(0, Integer.class))
                            .one();
    }
}
