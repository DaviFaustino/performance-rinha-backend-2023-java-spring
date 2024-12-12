package com.davifaustino.performancerinhabackend.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.davifaustino.performancerinhabackend.business.PessoaService;

import reactor.core.publisher.Mono;

@RestController
public class PessoaController {

    private PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }
    
    @PostMapping(value = "/pessoas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UUID>> savePessoa(@RequestBody PessoaDto pessoaDto) {
        return pessoaService.savePessoa(pessoaDto)
                            .map(uuid -> ResponseEntity.created(URI.create("/pessoas/" + uuid.toString())).body(uuid));
    }

    @GetMapping(value = "/pessoas/{id}")
    public Mono<ResponseEntity<PessoaDto>> getOnePessoa(@PathVariable(value = "id") String id) {
        return pessoaService.getOnePessoa(id)
                            .map(pessoaDto -> ResponseEntity.status(HttpStatus.OK).body(pessoaDto));
    }

    // O parâmetro t não é requerido no endpoint para que o domínio lide com a validação de não nulo
    @GetMapping(value = "/pessoas")
    public Mono<ResponseEntity<List<PessoaDto>>> getPessoas(@RequestParam(required = false) String t) {
        return pessoaService.getPessoas(t)
                            .collectList()
                            .map(pessoaDtos -> ResponseEntity.status(HttpStatus.OK).body(pessoaDtos));
    }
}
