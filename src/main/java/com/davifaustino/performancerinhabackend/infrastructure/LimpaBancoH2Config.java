package com.davifaustino.performancerinhabackend.infrastructure;

import java.io.File;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PreDestroy;

@Configuration
@Profile("devtest")
public class LimpaBancoH2Config {

    private static final String DATABASE_PATH = "./data/testdb.mv.db";
    
    @PreDestroy
    public void limpar() {
        
        File dbFile = new File(DATABASE_PATH);
        if (dbFile.exists()) {
            boolean deleted = dbFile.delete();
            if (deleted) {
                System.out.println("Dados do H2 deletados com sucesso.");
            } else {
                System.err.println("Falha ao deletar dados do banco H2.");
            }
        } else {
            System.out.println("Arquivo do banco de dados H2 não encontrado para exclusão.");
        }
    }
}
