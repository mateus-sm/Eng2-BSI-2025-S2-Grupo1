package com.example.dminfo.controller;

import com.example.dminfo.model.Mensalidade;
import org.springframework.stereotype.Service; // ou @RestController dependendo do seu projeto
import java.util.List;

@Service
public class MensalidadeController {

    public Mensalidade salvar(Mensalidade m) {
        return m.salvar();
    }

    public boolean excluir(Integer id) {
        Mensalidade m = new Mensalidade();
        m.setId_mensalidade(id);
        return m.excluir();
    }

    public Mensalidade getById(Integer id) {
        return Mensalidade.buscarPorId(id);
    }

    public List<Mensalidade> listar(String filtroNome) {
        return Mensalidade.listarTodos(filtroNome);
    }

    public List<Mensalidade> listarMesAno(int mes, int ano) {
        return Mensalidade.listarPorMesAno(mes, ano);
    }

    public List<Mensalidade> listarMembro(Integer id) {
        return Mensalidade.listarPorMembro(id);
    }

    public List<Mensalidade> filtrar(String nome, String dataIni, String dataFim) {
        return Mensalidade.filtrarAvancado(nome, dataIni, dataFim);
    }
}