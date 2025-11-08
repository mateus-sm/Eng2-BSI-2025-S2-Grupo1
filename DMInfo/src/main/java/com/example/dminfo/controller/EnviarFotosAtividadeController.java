package com.example.dminfo.controller;

import com.example.dminfo.dao.EnviarFotosAtividadeDAO;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Membro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EnviarFotosAtividadeController {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private EnviarFotosAtividadeDAO dao;

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return dao.getPorAtividade(idAtividade);
    }

    public EnviarFotosAtividade salvar(MultipartFile arquivo, int idMembro, int idAtividade) throws IOException {
        Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));

        String extension = arquivo.getOriginalFilename().substring(arquivo.getOriginalFilename().lastIndexOf("."));
        String nomeArquivo = UUID.randomUUID().toString() + extension;
        Path caminho = Paths.get(UPLOAD_DIRECTORY + nomeArquivo);
        arquivo.transferTo(caminho.toFile());

        EnviarFotosAtividade foto = new EnviarFotosAtividade();
        foto.setFoto(nomeArquivo);
        foto.setData(LocalDate.now());

        Membro membro = new Membro();
        membro.setId(idMembro);
        foto.setMembro(membro);

        Atividade atividade = new Atividade();
        atividade.setId(idAtividade);
        foto.setAtividade(atividade);

        return dao.gravar(foto);
    }
}
