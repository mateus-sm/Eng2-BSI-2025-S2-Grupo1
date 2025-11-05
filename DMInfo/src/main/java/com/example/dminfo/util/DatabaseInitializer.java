package com.example.dminfo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> INICIALIZANDO CONEXAO COM O BANCO DE DADOS... <<<");
        if (SingletonDB.conectarDB()) {
            System.out.println(">>> CONEXAO COM O BANCO ESTABELECIDA COM SUCESSO! <<<");
        } else {
            throw new RuntimeException("ERRO GRAVE: Nao foi possivel conectar ao banco: " + SingletonDB.getConexao().getMensagemErro());
        }
    }
}