package com.davifaustino.performancerinhabackend.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.davifaustino.performancerinhabackend.domain.Pessoa;
import com.davifaustino.performancerinhabackend.domain.PessoaRepositoryPort;
import com.davifaustino.performancerinhabackend.domain.UnprocessableException;

import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class PessoaRepository implements PessoaRepositoryPort {

    @Autowired
    private SpringPessoaRepository springPessoaRepository;

    @Override
    public Mono<Boolean> existsByApelido(String apelido) {
        return springPessoaRepository.existsByApelido(apelido);
    }

    @Override
    public Mono<UUID> savePessoa(Pessoa pessoa) {
        return springPessoaRepository.savePessoa(new PessoaEntity(pessoa))
                                .flatMap(linhasAlt -> {
                                    if (linhasAlt == 0) {
                                        return Mono.error(new UnprocessableException("Pessoa nao salva"));
                                    } else {
                                        return Mono.just(pessoa.getId());
                                    }
                                });
    }
}
