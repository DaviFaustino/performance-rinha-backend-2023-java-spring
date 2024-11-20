package com.davifaustino.performancerinhabackend.infrastructure;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface SpringPessoaRepository extends ReactiveCrudRepository<PessoaEntity, UUID> {

    @Modifying
    @Query("INSERT INTO tb_pessoas (id, nome, apelido, nascimento, stack, busca)" +
        " VALUES (:#{#pe.id}, :#{#pe.nome}, :#{#pe.apelido}, :#{#pe.nascimento}, :#{#pe.stack}, :#{#pe.busca})")
    Mono<Long> savePessoa(@Param("pe") PessoaEntity pe);
    
    Mono<Boolean> existsByApelido(String apelido);
}
