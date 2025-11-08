package com.example.dminfo.dao;

import com.example.dminfo.model.Atividade;
import com.example.dminfo.model.EnviarFotosAtividade;
import com.example.dminfo.model.Membro;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EnviarFotosAtividadeDAO {

    private EnviarFotosAtividade buildFoto(ResultSet rs) throws SQLException {
        EnviarFotosAtividade foto = new EnviarFotosAtividade();
        foto.setId(rs.getInt("id_foto"));
        foto.setFoto(rs.getString("foto"));
        foto.setData(rs.getDate("data").toLocalDate());

        Membro membro = new Membro();
        membro.setId(rs.getInt("id_membro"));
        membro.setUsuario(new com.example.dminfo.model.Usuario() {{
            setId(rs.getInt("id_usuario"));
            setNome(rs.getString("usuario_nome"));
        }});
        foto.setMembro(membro);

        Atividade atv = new Atividade();
        atv.setId(rs.getInt("id_atividade"));
        atv.setDescricao(rs.getString("atividade_descricao"));
        foto.setAtividade(atv);

        return foto;
    }

    public List<EnviarFotosAtividade> getPorAtividade(int idAtividade) {
        List<EnviarFotosAtividade> fotos = new ArrayList<>();
        String sql = String.format(
                "SELECT ef.*, m.id_membro, u.id_usuario, u.nome AS usuario_nome, a.id_atividade, a.descricao AS atividade_descricao " +
                        "FROM enviar_fotos_atividade ef " +
                        "JOIN membro m ON ef.id_membro = m.id_membro " +
                        "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                        "JOIN atividade a ON ef.id_atividade = a.id_atividade " +
                        "WHERE ef.id_atividade = %d ORDER BY ef.data DESC",
                idAtividade
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null)
                while (rs.next())
                    fotos.add(buildFoto(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar Fotos de Atividade: " + e.getMessage());
        }
        return fotos;
    }

    public EnviarFotosAtividade get(int idFoto) {
        String sql = String.format(
                "SELECT ef.*, m.id_membro, u.id_usuario, u.nome AS usuario_nome, a.id_atividade, a.descricao AS atividade_descricao " +
                        "FROM enviar_fotos_atividade ef " +
                        "JOIN membro m ON ef.id_membro = m.id_membro " +
                        "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                        "JOIN atividade a ON ef.id_atividade = a.id_atividade " +
                        "WHERE ef.id_foto = %d", idFoto
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if (rs != null && rs.next())
                return buildFoto(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Foto: " + e.getMessage());
        }
        return null;
    }

    public EnviarFotosAtividade gravar(EnviarFotosAtividade foto) {
        String sql = String.format(
                "INSERT INTO enviar_fotos_atividade (id_membro, id_atividade, foto, data) VALUES (%d, %d, '%s', '%s')",
                foto.getMembro().getId(),
                foto.getAtividade().getId(),
                foto.getFoto(),
                foto.getData().toString()
        );

        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou) {
            System.out.println("Erro: " + SingletonDB.getConexao().getMensagemErro());
            throw new RuntimeException("Falha ao inserir a foto.");
        }

        int idGerado = SingletonDB.getConexao().getMaxPK("enviar_fotos_atividade", "id_foto");
        foto.setId(idGerado);
        return foto;
    }

    public boolean alterar(EnviarFotosAtividade foto) {
        String sql = String.format(
                "UPDATE enviar_fotos_atividade SET id_membro=%d, id_atividade=%d, foto='%s', data='%s' WHERE id_foto=%d",
                foto.getMembro().getId(),
                foto.getAtividade().getId(),
                foto.getFoto(),
                foto.getData().toString(),
                foto.getId()
        );
        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou)
            System.out.println("Erro ao alterar foto: " + SingletonDB.getConexao().getMensagemErro());
        return executou;
    }

    public boolean excluir(int idFoto) {
        String sql = String.format("DELETE FROM enviar_fotos_atividade WHERE id_foto=%d", idFoto);
        boolean executou = SingletonDB.getConexao().manipular(sql);

        if (!executou)
            System.out.println("Erro ao excluir foto: " + SingletonDB.getConexao().getMensagemErro());
        return executou;
    }
}