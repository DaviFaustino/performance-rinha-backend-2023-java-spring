package com.davifaustino.performancerinhabackend.infrastructure;

import java.util.Arrays;
import java.util.UUID;

import com.davifaustino.performancerinhabackend.domain.Pessoa;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.NoArgsConstructor;

@Table(name = "tb_pessoas")
@NoArgsConstructor
public class PessoaEntity {
    
    @Id
    private UUID id;
    private String apelido;
    private String nome;
    private String nascimento;
    private String[] stack;
    private String busca;

    public PessoaEntity(Pessoa pessoa) {
        this.id = pessoa.getId();
        this.apelido = pessoa.getApelido();
        this.nome = pessoa.getNome();
        this.nascimento = pessoa.getNascimento();
        Object[] stackObject = pessoa.getStack().toArray();
        this.stack = Arrays.copyOf(stackObject, stackObject.length, String[].class);
        this.busca = pessoa.getBusca();
    }

    public Pessoa toPessoa() {
        return new Pessoa(id, apelido, nome, nascimento, Arrays.asList(stack));
    }

    public UUID getId() {
        return this.id;
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
