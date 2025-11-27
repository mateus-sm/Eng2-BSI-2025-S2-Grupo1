package com.example.dminfo.controller;

import com.example.dminfo.dao.AtividadeDAO;
import com.example.dminfo.dao.EventoDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Evento;
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

    // Caminho correto com o nome do projeto incluído
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/DMInfo/src/main/resources/static/uploads/";

    @Autowired
    private EnviarFotosAtividade fotoModel;
    @Autowired
    private MembroDAO membroDAO;
    @Autowired
    private AtividadeDAO atividadeDAO;
    @Autowired
    private EventoDAO eventoDAO;

    public List<Evento> listarTodosEventos() {
        return eventoDAO.getTodos();
    }

    public List<Atividade> listarAtividadesPorEvento(int idEvento) {
        return atividadeDAO.getPorEvento(idEvento);
    }

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return fotoModel.listarPorAtividade(idAtividade);
    }

    public EnviarFotosAtividade getById(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto);
        if (foto == null)
            throw new RuntimeException("Foto não encontrada com ID: " + idFoto);
        return foto;
    }

    public EnviarFotosAtividade salvar(MultipartFile arquivo, int idMembro, int idAtividade) {
        Membro m = membroDAO.get(idMembro);
        if (m == null)
            throw new RuntimeException("Membro não encontrado com ID: " + idMembro);

        Atividade a = atividadeDAO.getById(idAtividade);
        if (a == null)
            throw new RuntimeException("Atividade não encontrada com ID: " + idAtividade);

        if (arquivo.isEmpty())
            throw new RuntimeException("Nenhum arquivo de foto enviado.");

        String nomeArquivo = "";
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));

            String originalName = arquivo.getOriginalFilename();
            String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".png";

            nomeArquivo = UUID.randomUUID().toString() + extension;
            Path caminho = Paths.get(UPLOAD_DIRECTORY + nomeArquivo);

            arquivo.transferTo(caminho.toFile());

            EnviarFotosAtividade foto = new EnviarFotosAtividade();
            foto.setFoto(nomeArquivo);
            foto.setData(LocalDate.now());
            foto.setMembro(m);
            foto.setAtividade(a);

            return fotoModel.gravar(foto);

        } catch (IOException e) {
            throw new RuntimeException("Falha no upload do arquivo físico: " + e.getMessage());
        } catch (RuntimeException e) {
            if (!nomeArquivo.isEmpty()) {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY + nomeArquivo));
                } catch (IOException ignore) {}
            }
            throw new RuntimeException("Falha ao inserir a foto no banco: " + e.getMessage());
        }
    }

    public void excluirFoto(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto);
        if (foto == null) throw new RuntimeException("Foto não encontrada para exclusão.");

        boolean excluiuBD = fotoModel.excluir(idFoto);

        if (excluiuBD) {
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY + foto.getFoto()));
            } catch (IOException e) {
                System.err.println("Aviso: Registro excluído, mas falha ao apagar arquivo: " + e.getMessage());
            }
        }
        else
            throw new RuntimeException("Falha ao excluir o registro da foto no banco.");
    }
}