package com.example.dminfo.controller;

import com.example.dminfo.dao.EventoDAO;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.AtividadeDAO;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EnviarFotosAtividadeController {

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    @Autowired
    private EnviarFotosAtividade fotoModel;
    @Autowired
    private MembroDAO membroDAO;
    @Autowired
    private AtividadeDAO atividadeDAO;
    @Autowired
    private EventoDAO eventoDAO;

    public List<Map<String, Object>> listarTodosEventos() {
        List<Evento> eventos = eventoDAO.getTodos();

        return eventos.stream().map(e -> {
            Map<String, Object> json = new HashMap<>();
            json.put("id", e.getId());
            json.put("titulo", e.getTitulo());
            json.put("descricao", e.getDescricao());
            return json;
        }).collect(java.util.stream.Collectors.toList());
    }

    public List<Map<String, Object>> listarAtividadesPorEvento(int idEvento) {
        List<Atividade> atividades = atividadeDAO.getPorEvento(idEvento);

        return atividades.stream().map(a -> {
                    Map<String, Object> json = new HashMap<>();
                    json.put("id", a.getId());
                    json.put("descricao", a.getDescricao());
                    return json;
                }).collect(java.util.stream.Collectors.toList());
    }

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return fotoModel.listarPorAtividade(idAtividade);
    }

    public Map<String, Object> salvar(MultipartFile arquivo, int idMembro, int idAtividade) {

        Membro m = membroDAO.get(idMembro);
        if (m == null)
            return Map.of("erro", "Membro não encontrado com ID: " + idMembro);

        Atividade a = atividadeDAO.getById(idAtividade);
        if (a == null)
            return Map.of("erro", "Atividade não encontrada com ID: " + idAtividade);

        if (arquivo.isEmpty())
            return Map.of("erro", "Nenhum arquivo de foto enviado.");

        String nomeArquivo = "";
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            String extension = arquivo.getOriginalFilename().substring(arquivo.getOriginalFilename().lastIndexOf("."));
            nomeArquivo = UUID.randomUUID().toString() + extension;
            Path caminho = Paths.get(UPLOAD_DIRECTORY + nomeArquivo);
            arquivo.transferTo(caminho.toFile());

            EnviarFotosAtividade foto = new EnviarFotosAtividade();
            foto.setFoto(nomeArquivo);
            foto.setData(LocalDate.now());
            foto.setMembro(m);
            foto.setAtividade(a);

            EnviarFotosAtividade fotoSalva = fotoModel.gravar(foto);

            return Map.of("id", fotoSalva.getId(), "foto", fotoSalva.getFoto());

        } catch (IOException e) {
            return Map.of("erro", "Falha no upload do arquivo físico: " + e.getMessage());
        } catch (RuntimeException e) {
            if (!nomeArquivo.isEmpty())
                try { Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY + nomeArquivo)); } catch (IOException ignore) {}
            return Map.of("erro", "Falha ao inserir a foto no banco: " + e.getMessage());
        }
    }

    public Map<String, Object> getById(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto);

        if (foto == null)
            return Map.of("erro", "Foto não encontrada com ID: " + idFoto);

        Map<String, Object> json = new HashMap<>();
        json.put("id", foto.getId());
        json.put("id_membro", foto.getMembro().getId());
        json.put("id_atividade", foto.getAtividade().getId());
        json.put("nome_membro", foto.getMembro().getUsuario().getNome());
        json.put("caminho_foto", foto.getFoto());
        json.put("data", foto.getData());

        return json;
    }

    public Map<String, Object> excluirFoto(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto);

        if (foto == null)
            return Map.of("erro", "Foto não encontrada para exclusão.");

        // Excluir o registro no banco
        boolean excluiuBD = fotoModel.excluir(idFoto);

        if (excluiuBD) {
            // Excluir o arquivo físico
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY + foto.getFoto()));
                return Map.of("mensagem", "Foto excluída com sucesso!");
            } catch (IOException e) {
                return Map.of("alerta", "Foto excluída do banco, mas falha ao remover arquivo físico: " + e.getMessage());
            }
        } else {
            return Map.of("erro", "Falha ao excluir o registro da foto no banco.");
        }
    }

    public Map<String, Object> alterar(int idFoto, int novoIdMembro, int novoIdAtividade) {
        EnviarFotosAtividade fotoExistente = fotoModel.getById(idFoto);

        if (fotoExistente == null)
            return Map.of("erro", "Foto não encontrada para alteração.");

        Membro m = membroDAO.get(novoIdMembro);
        if (m == null)
            return Map.of("erro", "Novo Membro (ID: " + novoIdMembro + ") não encontrado.");

        Atividade a = atividadeDAO.getById(novoIdAtividade);
        if (a == null)
            return Map.of("erro", "Nova Atividade (ID: " + novoIdAtividade + ") não encontrada.");

        fotoExistente.setMembro(m);
        fotoExistente.setAtividade(a);

        boolean alterou = fotoModel.alterar(fotoExistente);

        if (alterou)
            return Map.of("mensagem", "Foto atualizada com sucesso.");
        else
            return Map.of("erro", "Falha ao alterar a foto no banco de dados.");
    }
}