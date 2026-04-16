package com.example.dminfo.controller.strategy;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ProcessadorDeArquivoLocal implements ProcessadorDeArquivo {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/DMInfo/src/main/resources/static/uploads/";

    @Override
    public void salvar(MultipartFile arquivo, String nomeArquivo) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            Path caminho = Paths.get(UPLOAD_DIRECTORY + nomeArquivo);
            arquivo.transferTo(caminho.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Falha no upload do arquivo físico: " + e.getMessage());
        }
    }

    @Override
    public void excluir(String nomeArquivo) {
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY + nomeArquivo));
        } catch (IOException e) {
            System.err.println("Aviso: Falha ao apagar arquivo físico: " + e.getMessage());
        }
    }
}