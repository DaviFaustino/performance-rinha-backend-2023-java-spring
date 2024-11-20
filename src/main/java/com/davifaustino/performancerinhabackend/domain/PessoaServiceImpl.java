package com.davifaustino.performancerinhabackend.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import reactor.core.publisher.Mono;

public class PessoaServiceImpl implements PessoaServicePort {
    
    private PessoaRepositoryPort pessoaRepository;

    public PessoaServiceImpl(PessoaRepositoryPort pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public Mono<UUID> savePessoa(PessoaDto pessoaDto) {
        
        return Mono.defer(() -> 
                
                validar(pessoaDto)
                    .then(pessoaRepository.existsByApelido(pessoaDto.apelido())
                        .flatMap(exists -> {
                            if (!exists) {
                                return pessoaRepository.savePessoa(new Pessoa(pessoaDto));
                            } else {
                                return Mono.error(new UnprocessableException("Apelido existente"));
                            }
                        })
                    )
                );
    }

    @Override
    public Mono<Void> validar(PessoaDto pessoaDto) {

        if (pessoaDto.apelido() == null || pessoaDto.nome() == null || pessoaDto.nascimento() == null) {
            return Mono.error(new UnprocessableException("Valor de campo nulo"));
        }
        if (pessoaDto.apelido().length() > 32 || pessoaDto.nome().length() > 100) {
            return Mono.error(new UnprocessableException("Valor de campo ultrapassa tamanho"));
        }
        if (pessoaDto.apelido().matches("[0-9.]+") || pessoaDto.nome().matches("[0-9.]+") || pessoaDto.nascimento().matches("[0-9.]+")) {
            return Mono.error(new BadRequestException("Valor numerico em vez de string"));
        }
        try {
            LocalDate.parse(pessoaDto.nascimento(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return Mono.error(new UnprocessableException("Nascimento em formato errado"));
        }
        if (pessoaDto.stack() != null && pessoaDto.stack().isEmpty()) {
            return Mono.error(new BadRequestException("Stack vazia"));
        }

        return Mono.empty();
    }
}
