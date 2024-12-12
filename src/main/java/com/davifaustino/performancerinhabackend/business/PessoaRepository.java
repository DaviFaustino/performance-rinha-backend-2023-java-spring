package com.davifaustino.performancerinhabackend.business;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;
import org.springframework.stereotype.Component;

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
}
