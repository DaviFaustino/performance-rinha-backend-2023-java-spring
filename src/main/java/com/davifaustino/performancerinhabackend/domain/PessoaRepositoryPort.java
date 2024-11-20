package com.davifaustino.performancerinhabackend.domain;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface PessoaRepositoryPort {

    public Mono<Boolean> existsByApelido(String apelido);
    public Mono<UUID> savePessoa(Pessoa pessoa);
}
