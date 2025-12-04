package com.example.dminfo.controller;

import com.example.dminfo.model.Mensalidade;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MensalidadeController {

    @Autowired
    private Mensalidade mensModel;

    public Mensalidade salvar(Mensalidade m) {
        return m.salvar(SingletonDB.getConexao());
    }

    public boolean excluir(Integer id) {
        mensModel.setId_mensalidade(id);
        return mensModel.excluir(SingletonDB.getConexao());
    }

    public Mensalidade getById(Integer id) {
        return mensModel.buscarPorId(id, SingletonDB.getConexao());
    }

    public List<Mensalidade> listar(String filtroNome) {
        return mensModel.listarTodos(filtroNome,SingletonDB.getConexao());
    }

    public List<Mensalidade> listarMesAno(int mes, int ano) {
        return mensModel.listarPorMesAno(mes, ano, SingletonDB.getConexao());
    }

    public List<Mensalidade> listarMembro(Integer id) {
        return mensModel.listarPorMembro(id, SingletonDB.getConexao());
    }

    public List<Mensalidade> filtrar(String nome, String dataIni, String dataFim) {
        return mensModel.filtrarAvancado(nome, dataIni, dataFim, SingletonDB.getConexao());
    }
}