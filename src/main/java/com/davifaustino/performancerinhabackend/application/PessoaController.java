package com.davifaustino.performancerinhabackend.application;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.davifaustino.performancerinhabackend.domain.PessoaDto;
import com.davifaustino.performancerinhabackend.domain.PessoaServicePort;

import reactor.core.publisher.Mono;

@RestController
public class PessoaController {

    private PessoaServicePort pessoaService;

    public PessoaController(PessoaServicePort pessoaService) {
        this.pessoaService = pessoaService;
    }
    
    @PostMapping(value = "/pessoas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UUID>> savePessoa(@RequestBody PessoaDto pessoaDto) {
        return pessoaService.savePessoa(pessoaDto)
                            .map(uuid -> ResponseEntity.created(URI.create("/pessoas/" + uuid.toString())).body(uuid));
    }
}
