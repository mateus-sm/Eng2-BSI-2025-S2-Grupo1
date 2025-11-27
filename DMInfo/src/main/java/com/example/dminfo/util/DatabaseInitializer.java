package com.example.dminfo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        SingletonDB.conectarDB();
    }
}