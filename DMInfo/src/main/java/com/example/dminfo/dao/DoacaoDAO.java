package com.example.dminfo.dao;

import com.example.dminfo.model.Administrador;
import com.example.dminfo.model.Doacao;
import com.example.dminfo.model.Doador;
import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class DoacaoDAO {

    private Doacao buildDoacao(ResultSet rs) throws SQLException {
        Doacao doacao = new Doacao();
        doacao.setId_doacao(rs.getInt("id_doacao"));
        doacao.setData(rs.getDate("data").toLocalDate());
        doacao.setValor(rs.getDouble("valor"));
        doacao.setObservacao(rs.getString("observacao"));

        //Monta Doador
        Doador doador = new Doador();
        doador.setId(rs.getInt("id_doador"));
        doador.setNome(rs.getString("doador_nome"));
        doacao.setId_doador(doador);

        //Monta Admin
        Administrador admin = new Administrador();
        admin.setId(rs.getInt("id_admin"));

        //Monta Usuário do Admin
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("usuario_nome"));
        admin.setUsuario(usuario);
        doacao.setId_admin(admin);

        return doacao;
    }

    public List<Doacao> get(String filtro) {
        List<Doacao> doacoes = new ArrayList<>();
        String sql = "SELECT d.*, " +
                "   don.nome AS doador_nome, don.id_doador, " +
                "   a.id_admin, u.id_usuario, u.nome AS usuario_nome " +
                "FROM doacao d " +
                "JOIN doador don ON d.id_doador = don.id_doador " +
                "JOIN administrador a ON d.id_admin = a.id_admin " +
                "JOIN usuario u ON a.id_usuario = u.id_usuario " +
                filtro + " ORDER BY d.data DESC";

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try {
            if(rs != null)
                while(rs.next())
                    doacoes.add(buildDoacao(rs));
        }catch(SQLException e){
            System.out.println("Erro ao listar Doações: " + e.getMessage());
        }
        return doacoes;
    }

    public Doacao get(int id) {
        String filtro = String.format(" WHERE d.id_doacao = %d", id);
        List<Doacao> lista = get(filtro);
        if (lista.isEmpty())
            return null;
        return lista.get(0);
    }

    public Doacao gravar(Doacao doacao) {
        String obsOriginal = doacao.getObservacao();
        String obsEscapada = obsOriginal.replace("'", "''");

        // Usamos Locale.US para garantir o ponto decimal no valor
        String sql = String.format(Locale.US,
                "INSERT INTO doacao (id_doador, id_admin, data, valor, observacao) " +
                        "VALUES (%d, %d, '%s', %f, '%s') RETURNING id_doacao",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getData().toString(),
                doacao.getValor(),
                obsEscapada
        );

        ResultSet rs = SingletonDB.getConexao().consultar(sql);
        try{
            if(rs != null && rs.next()){
                doacao.setId_doacao(rs.getInt("id_doacao"));
                return doacao;
            }
        }catch(SQLException e){
            System.out.println("Erro ao gravar Doação (SQL): " + e.getMessage());
            throw new RuntimeException("Falha no SQL ao gravar: " + e.getMessage());
        }

        return null;
    }

    public boolean atualizar(Doacao doacao) {
        String obsOriginal = doacao.getObservacao();
        String obsEscapada = obsOriginal.replace("'", "''");

        String sql = String.format(Locale.US,
                "UPDATE doacao SET " +
                        "id_doador = %d, " +
                        "id_admin = %d, " +
                        "valor = %f, " +
                        "observacao = '%s' " +
                        "WHERE id_doacao = %d",
                doacao.getId_doador().getId(),
                doacao.getId_admin().getId(),
                doacao.getValor(),
                obsEscapada,
                doacao.getId_doacao()
        );

        return SingletonDB.getConexao().manipular(sql);
    }

    public boolean excluir(int id) {
        String sql = String.format("DELETE FROM doacao WHERE id_doacao = %d", id);
        return SingletonDB.getConexao().manipular(sql);
    }
}