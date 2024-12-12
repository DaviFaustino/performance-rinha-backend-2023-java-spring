package com.davifaustino.performancerinhabackend.business;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davifaustino.performancerinhabackend.api.PessoaDto;

import reactor.core.publisher.Mono;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Mono<UUID> savePessoa(PessoaDto pessoaDto) {

        Pessoa pessoaEntity = new Pessoa(pessoaDto);
        try {
            validar(pessoaDto);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return pessoaRepository.savePessoa(pessoaEntity).flatMap(linhasAlt -> {
                                                    if (linhasAlt == 0) {
                                                        return Mono.error(new UnprocessableException());
                                                    } else {
                                                        return Mono.just(pessoaEntity.getId());
                                                    }
                                                });
    }

    public void validar(PessoaDto pessoaDto) {

        if (pessoaDto.apelido() == null || pessoaDto.nome() == null || pessoaDto.nascimento() == null) {
            throw new UnprocessableException();
        }
        if (pessoaDto.apelido().length() > 32 || pessoaDto.nome().length() > 100) {
            throw new UnprocessableException();
        }
        if (pessoaDto.apelido().matches("[0-9.]+") || pessoaDto.nome().matches("[0-9.]+") || pessoaDto.nascimento().matches("[0-9.]+")) {
            throw new BadRequestException();
        }
        try {
            LocalDate.parse(pessoaDto.nascimento(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            throw new UnprocessableException();
        }
        if (pessoaDto.stack() != null) {
            if (pessoaDto.stack().isEmpty()) {
                throw new BadRequestException();
            }
            if (!pessoaDto.stack().stream().allMatch(s -> s.length() <= 32)) {
                throw new UnprocessableException();
            }
        }
    }
}
