package com.davifaustino.performancerinhabackend.domain;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface PessoaServicePort {
    
    public Mono<UUID> savePessoa(PessoaDto pessoaDto);
    public Mono<Void> validar(PessoaDto pessoaDto);
}
