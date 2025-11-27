//package com.example.dminfo.controller;
//
//import com.example.dminfo.model.CriarRealizacaoAtividades;
//import com.example.dminfo.model.FrequenciaAtividade;
//import com.example.dminfo.model.Membro;
//import com.example.dminfo.model.Usuario;
//import com.example.dminfo.util.SingletonDB; // Import necessário
//import com.example.dminfo.util.Conexao;     // Import necessário
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class FrequenciaAtividadeController {
//
//    // 1. Injete os MODELS, não os DAOs e nem outros Controllers
//    @Autowired
//    private FrequenciaAtividade frequenciaModel;
//
//    @Autowired
//    private CriarRealizacaoAtividades atividadeModel;
//
//    @Autowired
//    private Membro membroModel;
//
//    // Funcao de teste (Mantida)
//    public List<Membro> getMembrosTeste() {
//        List<Membro> membros = new ArrayList<>();
//        Usuario u1 = new Usuario(); u1.setId(101); u1.setNome("Ana Silva (Teste)");
//        Membro m1 = new Membro(); m1.setId(1); m1.setDtIni(LocalDate.of(2024, 1, 15)); m1.setUsuario(u1);
//        membros.add(m1);
//
//        Usuario u2 = new Usuario(); u2.setId(102); u2.setNome("Bruno Souza (Teste)");
//        Membro m2 = new Membro(); m2.setId(2); m2.setDtIni(LocalDate.of(2024, 2, 20)); m2.setUsuario(u2);
//        membros.add(m2);
//        return membros;
//    }
//
//    public List<FrequenciaAtividade> buscarFrequenciasPorAtividade(int idAtividade) {
//        // Passa a conexão para o Model
//        return frequenciaModel.buscarPorAtividade(idAtividade, SingletonDB.getConexao());
//    }
//
//    public List<Membro> listarMembros(String termoBusca) {
//        // Passa a conexão para o Model
//        return membroModel.buscar(termoBusca, SingletonDB.getConexao());
//    }
//
//    // 1. Busca Atividades (usando a data de início como filtro)
//    public List<CriarRealizacaoAtividades> buscarAtividadesPorData(LocalDate data) {
//        // Você precisará garantir que esse método exista no Model ou filtrar aqui
//        // Exemplo assumindo que o Model sabe filtrar ou listar tudo:
//        List<CriarRealizacaoAtividades> todas = atividadeModel.listarTodas(SingletonDB.getConexao());
//
//        // Filtro manual simples caso o SQL não esteja pronto
//        List<CriarRealizacaoAtividades> filtradas = new ArrayList<>();
//        for(CriarRealizacaoAtividades atv : todas) {
//            if(atv.getDtIni() != null && atv.getDtIni().equals(data)) {
//                filtradas.add(atv);
//            }
//        }
//        return filtradas;
//    }
//
//    // 2. Busca Membro pelo ID
//    public Membro buscarMembroPorId(int idMembro) {
//        return membroModel.buscarPorId(idMembro, SingletonDB.getConexao());
//    }
//
//    // 3. Lançar Frequência
//    public FrequenciaAtividade lancarFrequencia(int idCriacao, int idMembro) {
//        Conexao conexao = SingletonDB.getConexao(); // Pega conexão uma vez
//
//        // Valida Membro via Model
//        Membro membro = membroModel.buscarPorId(idMembro, conexao);
//        if (membro == null)
//            throw new RuntimeException("Membro com ID " + idMembro + " não encontrado.");
//
//        // Valida Atividade via Model
//        CriarRealizacaoAtividades atividade = atividadeModel.buscarPorId(idCriacao, conexao);
//        if (atividade == null)
//            throw new RuntimeException("Atividade de Realização com ID " + idCriacao + " não encontrada.");
//
//        // Verifica duplicidade via Model da Frequência
//        if (frequenciaModel.jaRegistrado(idCriacao, idMembro, conexao)) {
//            throw new RuntimeException("Membro já possui frequência registrada nesta atividade.");
//        }
//
//        // Cria e preenche o objeto
//        FrequenciaAtividade freq = new FrequenciaAtividade();
//        freq.setAtividadeRealizada(atividade);
//        freq.setMembro(membro);
//        // Status TRUE por padrão
//
//        // Salva via Model
//        return frequenciaModel.salvar(freq, conexao);
//    }
//}