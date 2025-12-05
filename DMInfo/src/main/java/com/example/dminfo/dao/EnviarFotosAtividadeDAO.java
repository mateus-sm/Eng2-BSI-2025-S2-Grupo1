package com.example.dminfo.dao;

import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EnviarFotosAtividadeDAO {

    public List<EnviarFotosAtividade> getPorAtividade(int idAtividade, Conexao conexao) {
        List<EnviarFotosAtividade> fotos = new ArrayList<>();
        String sql = String.format("""
                SELECT ef.*, m.id_membro, u.id_usuario, u.nome AS usuario_nome, 
                       a.id_atividade, a.descricao AS atividade_descricao 
                FROM enviar_fotos_atividade ef 
                JOIN membro m ON ef.id_membro = m.id_membro 
                JOIN usuario u ON m.id_usuario = u.id_usuario 
                JOIN atividade a ON ef.id_atividade = a.id_atividade 
                WHERE ef.id_atividade = %d ORDER BY ef.data DESC
                """, idAtividade);

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null)
                while (rs.next())
                    fotos.add(new EnviarFotosAtividade(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Fotos de Atividade: " + e.getMessage());
        }
        return fotos;
    }

    public EnviarFotosAtividade get(int idFoto, Conexao conexao) {
        String sql = String.format("""
                SELECT ef.*, m.id_membro, u.id_usuario, u.nome AS usuario_nome, 
                       a.id_atividade, a.descricao AS atividade_descricao 
                FROM enviar_fotos_atividade ef 
                JOIN membro m ON ef.id_membro = m.id_membro 
                JOIN usuario u ON m.id_usuario = u.id_usuario 
                JOIN atividade a ON ef.id_atividade = a.id_atividade 
                WHERE ef.id_foto = %d
                """, idFoto);

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next())
                return new EnviarFotosAtividade(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Foto: " + e.getMessage());
        }
        return null;
    }

    public EnviarFotosAtividade gravar(EnviarFotosAtividade foto, Conexao conexao) {
        String sql = String.format(
                "INSERT INTO enviar_fotos_atividade (id_membro, id_atividade, foto, data) VALUES (%d, %d, '%s', '%s') RETURNING id_foto",
                foto.getMembro().getId(),
                foto.getAtividade().getId(),
                foto.getFoto(),
                foto.getData().toString()
        );

        try {
            ResultSet rs = conexao.consultar(sql);
            if (rs != null && rs.next()) {
                foto.setId(rs.getInt("id_foto"));
                return foto;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gravar Foto: " + e.getMessage());
        }
        return null;
    }

    public boolean excluir(int idFoto, Conexao conexao) {
        String sql = String.format("DELETE FROM enviar_fotos_atividade WHERE id_foto=%d", idFoto);
        return conexao.manipular(sql);
    }
}