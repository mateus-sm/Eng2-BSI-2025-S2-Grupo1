package com.example.dminfo.controller.bridge;

import org.springframework.web.multipart.MultipartFile;

//Implementor
public interface ProcessadorDeArquivo {
    void salvar(MultipartFile arquivo, String nomeArquivo);
    void excluir(String nomeArquivo);
}