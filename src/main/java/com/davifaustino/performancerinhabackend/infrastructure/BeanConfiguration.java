package com.davifaustino.performancerinhabackend.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.davifaustino.performancerinhabackend.domain.PessoaRepositoryPort;
import com.davifaustino.performancerinhabackend.domain.PessoaServiceImpl;
import com.davifaustino.performancerinhabackend.domain.PessoaServicePort;

@Configuration
public class BeanConfiguration {

    @Bean
    public PessoaServicePort pessoaServicePort(PessoaRepositoryPort pessoaRepositoryPort) {
        return new PessoaServiceImpl(pessoaRepositoryPort);
    }
}
