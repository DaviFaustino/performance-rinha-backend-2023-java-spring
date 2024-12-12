package com.davifaustino.performancerinhabackend.business;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.davifaustino.performancerinhabackend.api.PessoaDto;

@Table(name = "tb_pessoas")
public class Pessoa {
    
    @Id
    private UUID id;
    private String apelido;
    private String nome;
    private String nascimento;
    private String[] stack;
    private String busca;

    public Pessoa() {
    }

    public Pessoa(UUID id, String apelido, String nome, String nascimento, String[] stack) {
        this.id = id;
        this.apelido = apelido;
        this.nome = nome;
        this.nascimento = nascimento;
        this.stack = stack;
    }

    public Pessoa(PessoaDto pessoaDto) {
        this.id = UUID.randomUUID();
        this.apelido = pessoaDto.apelido();
        this.nome = pessoaDto.nome();
        this.nascimento = pessoaDto.nascimento();
        if (pessoaDto.stack() == null) {
            this.stack = null;
        } else {
            Object[] stackObject = pessoaDto.stack().toArray();
            this.stack = Arrays.copyOf(stackObject, stackObject.length, String[].class);
        }
        this.busca = pessoaDto.apelido() + "," + pessoaDto.nome() + (pessoaDto.stack() == null ? "": "," + String.join(",", pessoaDto.stack()));
    }

    public PessoaDto toPessoaDto() {
        return new PessoaDto(id, apelido, nome, nascimento, stack == null ? null: Arrays.asList(stack));
    }

    public UUID getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }

    public String getNome() {
        return nome;
    }

    public String getNascimento() {
        return nascimento;
    }

    public String[] getStack() {
        return stack;
    }

    public String getBusca() {
        return busca;
    }
}
