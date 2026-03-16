package com.example.dminfo.model;

import com.example.dminfo.dao.AtribuirConquistaMembroDAO;
import com.example.dminfo.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class AtribuirConquistaMembro {
    private int id;
    private int id_admin;
    private int id_membro;
    private int id_conquista;
    private Date data;
    private String observacao;

    @Autowired
    private AtribuirConquistaMembroDAO dao;

    public AtribuirConquistaMembro() { }

    public AtribuirConquistaMembro(int id_admin, int id_membro, int id_conquista, Date data, String observacao) {
        this.id_admin = id_admin;
        this.id_membro = id_membro;
        this.id_conquista = id_conquista;
        this.data = data;
        this.observacao = observacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id_atribuir_conquista) {
        this.id = id_atribuir_conquista;
    }

    public int getId_admin() {
        return id_admin;
    }

    public void setId_admin(int id_admin) {
        this.id_admin = id_admin;
    }

    public int getId_membro() {
        return id_membro;
    }

    public void setId_membro(int id_membro) {
        this.id_membro = id_membro;
    }

    public int getId_conquista() {
        return id_conquista;
    }

    public void setId_conquista(int id_conquista) {
        this.id_conquista = id_conquista;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    //Métodos
    private AtribuirConquistaMembro buildACM(ResultSet rs) throws SQLException {
        AtribuirConquistaMembro acm  = new AtribuirConquistaMembro();
        acm.setId(rs.getInt("id_atribuir_conquista"));
        acm.setId_admin(rs.getInt("id_admin"));
        acm.setId_membro(rs.getInt("id_membro"));
        acm.setId_conquista(rs.getInt("id_conquista"));
        acm.setData(rs.getDate("data"));
        acm.setObservacao(rs.getString("observacao"));
        return acm;
    }

    public List<AtribuirConquistaMembro> listar(String filtro, Conexao conexao) {
        List<AtribuirConquistaMembro> acmList = new ArrayList<>();
        ResultSet rs = dao.readAll(filtro, conexao);

        try {
            while (rs != null && rs.next()) {
                acmList.add(buildACM(rs));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao construir Atribuições na Model: " + e.getMessage());
        }

        return acmList;
    }

    public AtribuirConquistaMembro getById(Integer id, Conexao conexao) {
        ResultSet rs = dao.getById(id, conexao);
        try {
            if (rs != null && rs.next()) {
                return buildACM(rs);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao construir objeto na Model: " + e.getMessage());
        }

        return null;
    }

    public AtribuirConquistaMembro getByDesc(AtribuirConquistaMembro acm, Conexao conexao) {
        return dao.read(acm, conexao);
    }

    public AtribuirConquistaMembro salvar(AtribuirConquistaMembro acm, Conexao conexao) {
        if (acm == null) {
            throw new RuntimeException("Atribuição nula");
        }

        AtribuirConquistaMembro existente = dao.read(acm, conexao);
        if (existente != null) {
            throw new RuntimeException("Já existe uma atribuição com essa descrição.");
        }

        return dao.create(acm, conexao);
    }

    public AtribuirConquistaMembro alterar(AtribuirConquistaMembro acm, Conexao conexao) {
        AtribuirConquistaMembro c = dao.update(acm, conexao);
        if (c != null) {
            return acm;
        }
        throw new RuntimeException("Erro ao atualizar conquista.");
    }

    public boolean excluir(Integer id, Conexao conexao) {
        AtribuirConquistaMembro conquista = getById(id, conexao);

        if (conquista == null) {
            throw new RuntimeException("Conquista não encontrada.");
        }

        return dao.delete(id, conexao);
    }
}
