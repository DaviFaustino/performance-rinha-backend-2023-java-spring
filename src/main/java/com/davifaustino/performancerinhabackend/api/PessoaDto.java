package com.davifaustino.performancerinhabackend.api;

import java.util.List;
import java.util.UUID;

public record PessoaDto(
    UUID id,
    String apelido,
    String nome,
    String nascimento,
    List<String> stack) {
}
