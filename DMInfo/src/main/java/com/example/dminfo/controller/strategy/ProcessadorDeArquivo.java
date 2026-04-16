package com.example.dminfo.controller.strategy;

import org.springframework.web.multipart.MultipartFile;

public interface ProcessadorDeArquivo {
    void salvar(MultipartFile arquivo, String nomeArquivo);
    void excluir(String nomeArquivo);
}