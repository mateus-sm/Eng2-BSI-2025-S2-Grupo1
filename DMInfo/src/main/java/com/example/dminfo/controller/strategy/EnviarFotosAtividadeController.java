package com.example.dminfo.controller.strategy;

import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Evento;
import com.example.dminfo.model.Membro;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

//Abstração
@Service
public class EnviarFotosAtividadeController {

    @Autowired
    private EnviarFotosAtividade fotoModel;

    @Autowired
    private Membro membroModel;

    @Autowired
    private Atividade atividadeModel;

    @Autowired
    private Evento eventoModel;

    private ProcessadorDeArquivoLocal processadorDeArquivo;

    public List<Evento> listarTodosEventos() {
        return eventoModel.getTodos(SingletonDB.getConexao());
    }

    public List<Atividade> listarAtividadesPorEvento(int idEvento) {
        return atividadeModel.listarPorEvento(idEvento, SingletonDB.getConexao());
    }

    public List<EnviarFotosAtividade> listarPorAtividade(int idAtividade) {
        return fotoModel.listarPorAtividade(idAtividade, SingletonDB.getConexao());
    }

    public EnviarFotosAtividade getById(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto, SingletonDB.getConexao());
        if (foto == null)
            throw new RuntimeException("Foto não encontrada com ID: " + idFoto);
        return foto;
    }

    public EnviarFotosAtividade salvar(MultipartFile arquivo, int idUsuario, int idAtividade) {
        Membro m = membroModel.getByUsuario(idUsuario, SingletonDB.getConexao());
        if (m == null)
            throw new RuntimeException("Membro não encontrado para o usuário logado (ID Usuário: " + idUsuario + ")");

        Atividade a = atividadeModel.getById(idAtividade, SingletonDB.getConexao());
        if (a == null)
            throw new RuntimeException("Atividade não encontrada com ID: " + idAtividade);

        if (arquivo.isEmpty())
            throw new RuntimeException("Nenhum arquivo de foto enviado.");

        String originalName = arquivo.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".png";
        String nomeArquivo = UUID.randomUUID().toString() + extension;

        processadorDeArquivo = new  ProcessadorDeArquivoLocal();
        processadorDeArquivo.salvar(arquivo, nomeArquivo);

        try {

            EnviarFotosAtividade foto = new EnviarFotosAtividade();
            foto.setFoto(nomeArquivo);
            foto.setData(LocalDate.now());
            foto.setMembro(m);
            foto.setAtividade(a);

            return fotoModel.gravar(foto, SingletonDB.getConexao());
        } catch (RuntimeException e) {
            processadorDeArquivo.excluir(nomeArquivo);
            throw new RuntimeException("Falha ao inserir a foto no banco: " + e.getMessage());
        }
    }

    public void excluirFoto(int idFoto) {
        EnviarFotosAtividade foto = fotoModel.getById(idFoto, SingletonDB.getConexao());
        if (foto == null)
            throw new RuntimeException("Foto não encontrada para exclusão.");

        boolean excluiuBD = fotoModel.excluir(idFoto, SingletonDB.getConexao());

        if (!excluiuBD) {
            throw new RuntimeException("Falha ao excluir o registro da foto no banco.");
        }

        processadorDeArquivo = new  ProcessadorDeArquivoLocal();
        processadorDeArquivo.excluir(foto.getFoto());
    }
}