package com.example.dminfo.controller;

import com.example.dminfo.dao.AdministradorDAO;
import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import com.example.dminfo.util.Conexao;
import com.example.dminfo.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoacaoController {

    // Injeta os Models (que agora agem como repositório/serviço)
    @Autowired
    private Doacao doacaoModel;

    @Autowired
    private Doador doadorModel; // Usamos o Model agora, não o DAO direto

    // Mantemos o DAO de Admin se ele ainda não foi refatorado para o novo padrão
    @Autowired
    private AdministradorDAO adminDAO;

    public List<Doacao> listar() {
        return doacaoModel.listar("", SingletonDB.getConexao());
    }

    public Doacao buscar(int id) {
        Doacao d = doacaoModel.getById(id, SingletonDB.getConexao());
        if (d == null) {
            throw new RuntimeException("Doação não encontrada.");
        }
        return d;
    }

    public Doacao salvar(Doacao doacao) {
        Conexao conexao = SingletonDB.getConexao();

        if (doacao == null || doacao.getId_doacao() != 0)
            throw new RuntimeException("Doação inválida para criação.");

        validarDependencias(doacao, conexao);

        // Define data atual
        doacao.setData(LocalDate.now());

        // Validação de negócio
        if (doacao.getValor() <= 0)
            throw new RuntimeException("O valor da doação deve ser positivo.");

        return doacaoModel.salvar(doacao, conexao);
    }

    public Doacao atualizar(Doacao doacao) {
        Conexao conexao = SingletonDB.getConexao();

        if (doacao == null || doacao.getId_doacao() == 0)
            throw new RuntimeException("Doação inválida para atualização.");

        if (doacaoModel.getById(doacao.getId_doacao(), conexao) == null)
            throw new RuntimeException("Doação não encontrada para atualização.");

        validarDependencias(doacao, conexao);

        if (doacao.getValor() <= 0)
            throw new RuntimeException("O valor da doação deve ser positivo.");

        doacaoModel.alterar(doacao, conexao);
        return doacao;
    }

    public boolean excluir(int id) {
        if (id == 0) {
            throw new RuntimeException("ID inválido para exclusão.");
        }
        return doacaoModel.excluir(id, SingletonDB.getConexao());
    }

    // Passamos a conexão para poder reutilizá-la nas consultas
    private void validarDependencias(Doacao doacao, Conexao conexao) {
        if (doacao.getId_doador() == null || doacao.getId_doador().getId() == 0)
            throw new RuntimeException("O ID do Doador é obrigatório.");

        if (doacao.getId_admin() == null || doacao.getId_admin().getId() == 0)
            throw new RuntimeException("O ID do Administrador é obrigatório.");

        // --- CORREÇÃO AQUI ---
        // Usamos doadorModel.getById e passamos a conexão
        Doador doador = doadorModel.getById(doacao.getId_doador().getId(), conexao);
        if (doador == null)
            throw new RuntimeException("Doador não encontrado.");

        // Como AdminDAO ainda é o antigo (provavelmente), usamos o método antigo dele
        // Se AdminDAO já foi refatorado, mude para adminModel.getById(id, conexao)
        Administrador admin = adminDAO.get(doacao.getId_admin().getId());
        if (admin == null)
            throw new RuntimeException("Administrador não encontrado.");

        // Atualiza objetos completos para garantir consistência
        doacao.setId_doador(doador);
        doacao.setId_admin(admin);
    }
}