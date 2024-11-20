package com.davifaustino.performancerinhabackend.infrastructure;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@Profile("devtest")
public class InsertTestDataConfig {

    private final DatabaseClient databaseClient;

    public InsertTestDataConfig(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Bean
    public ApplicationRunner verifyData() {
        return args -> {

            databaseClient.sql(Files.readString((Paths.get("./src/main/resources/data.sql"))))
                        .fetch()
                        .rowsUpdated()
                        .doOnError(error -> System.err.println("Erro ao inserir dados no H2: " + error.getMessage()))
                        .subscribe();
        };
    }
}