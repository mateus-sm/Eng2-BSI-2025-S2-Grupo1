package com.example.dminfo.controller;

import com.example.dminfo.model.CriarRealizacaoAtividades;
import com.example.dminfo.model.FrequenciaAtividade;
import com.example.dminfo.model.Membro;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.dao.MembroDAO;
import com.example.dminfo.dao.FrequenciaAtividadeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrequenciaAtividadeController {

    @Autowired
    private FrequenciaAtividadeDAO freqDAO;
    @Autowired
    private CriarRealizacaoAtividadesController craController;
    @Autowired
    private MembroDAO membroDAO;

    // funcao de teste
    public List<Membro> getMembrosTeste() {
        List<Membro> membros = new ArrayList<>();

        // Membro 1
        Usuario u1 = new Usuario();
        u1.setId(101);
        u1.setNome("Ana Silva (Teste)");

        Membro m1 = new Membro();
        m1.setId(1);
        m1.setDtIni(LocalDate.of(2024, 1, 15));
        m1.setUsuario(u1);
        membros.add(m1);

        // Membro 2
        Usuario u2 = new Usuario();
        u2.setId(102);
        u2.setNome("Bruno Souza (Teste)");

        Membro m2 = new Membro();
        m2.setId(2);
        m2.setDtIni(LocalDate.of(2024, 2, 20));
        m2.setUsuario(u2);
        membros.add(m2);

        return membros;
    }

    public List<FrequenciaAtividade> buscarFrequenciasPorAtividade(int idAtividade) {
        // Chama o método no DAO
        return freqDAO.buscarPorAtividade(idAtividade);
    }

    public List<Membro> listarMembros(String termoBusca) {
        // Se a sua correção no Membro.java já funcionou, você pode retornar o DAO original aqui.
        // Se ainda não limpou o Membro.java, use o DAO original:
        return membroDAO.get(termoBusca);
    }

    /* antigo
    public List<Membro> listarMembros(String termoBusca) {
        // Chama o método existente no DAO
        return membroDAO.get(termoBusca);
    }

    */

    // 1. Busca Atividades (usando a data de início como filtro)
    public List<CriarRealizacaoAtividades> buscarAtividadesPorData(LocalDate data) {
        // Requer que CriarRealizacaoAtividadesController tenha listarPorData (veja ajuste anterior)
        return craController.listarPorData(data);
    }

    // 2. Busca Membro pelo ID
    public Membro buscarMembroPorId(int idMembro) {
        // Usa o get(int id) do seu MembroDAO
        return membroDAO.get(idMembro);
    }

    // 3. Lançar Frequência
    public FrequenciaAtividade lancarFrequencia(int idCriacao, int idMembro) {

        Membro membro = membroDAO.get(idMembro);
        if (membro == null)
            throw new RuntimeException("Membro com ID " + idMembro + " não encontrado.");

        CriarRealizacaoAtividades atividade = craController.getById(idCriacao);
        if (atividade == null)
            throw new RuntimeException("Atividade de Realização com ID " + idCriacao + " não encontrada.");

        if (freqDAO.jaRegistrado(idCriacao, idMembro)) {
            throw new RuntimeException("Membro já possui frequência registrada nesta atividade.");
        }

        // Cria e preenche o objeto FrequenciaAtividade
        FrequenciaAtividade freq = new FrequenciaAtividade();
        freq.setAtividadeRealizada(atividade);
        freq.setMembro(membro);
        // Status é TRUE por padrão no construtor, confirmando a presença.

        return freqDAO.salvarFrequencia(freq);
    }
}