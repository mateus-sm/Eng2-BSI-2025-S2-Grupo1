//package com.example.dminfo.controller;
//
//import com.example.dminfo.dao.CriarRealizacaoAtividadesDAO;
//import com.example.dminfo.model.CriarRealizacaoAtividades;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//public class CriarRealizacaoAtividadesController {
//
//    @Autowired
//    private CriarRealizacaoAtividades model;
//    @Autowired
//    private CriarRealizacaoAtividadesDAO dao;
//
//    public List<CriarRealizacaoAtividades> listarPorData(LocalDate data) {
//        // Chama o novo método do DAO
//        return dao.getPorData(data);
//    }
//
//    public CriarRealizacaoAtividades getById(Integer id) {
//        return model.getById(id);
//    }
//
//    // Listar com Filtros/Ordenação
//    public List<CriarRealizacaoAtividades> listar(String termoDescricao, String ordenarPor) {
//        return model.listar(termoDescricao, ordenarPor);
//    }
//
//    public CriarRealizacaoAtividades salvar(CriarRealizacaoAtividades atividade) {
//        return model.salvar(atividade);
//    }
//
//    public CriarRealizacaoAtividades atualizar(Integer id, CriarRealizacaoAtividades atividade) {
//        return model.atualizar(id, atividade);
//    }
//
//    public boolean excluir(Integer id) {
//        return model.excluir(id);
//    }
//
//    public boolean finalizar(CriarRealizacaoAtividades atividade) {
//        return model.finalizarAtividade(atividade);
//    }
//}