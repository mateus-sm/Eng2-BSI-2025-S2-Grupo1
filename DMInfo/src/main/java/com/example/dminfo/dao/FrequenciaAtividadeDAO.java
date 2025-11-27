//package com.example.dminfo.dao;
//
//import com.example.dminfo.model.CriarRealizacaoAtividades;
//import com.example.dminfo.model.FrequenciaAtividade;
//import com.example.dminfo.model.Membro;
//import com.example.dminfo.util.SingletonDB;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Repository
//public class FrequenciaAtividadeDAO {
//
//    @Autowired
//    private MembroDAO membroDAO;
//    @Autowired
//    private CriarRealizacaoAtividadesDAO craDAO;
//
//    private String formatDateTime(LocalDateTime dateTime) {
//        // Formato necessário para o SQL
//        return "'" + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
//    }
//
//    public List<FrequenciaAtividade> buscarPorAtividade(int idAtividade) {
//        List<FrequenciaAtividade> frequencias = new ArrayList<>();
//
//        String sql = String.format(
//                "SELECT * FROM frequencia_atividade WHERE id_realizacao_atividades = %d",
//                idAtividade
//        );
//
//        ResultSet rs = SingletonDB.getConexao().consultar(sql);
//        try {
//            if (rs != null) {
//                while (rs.next()) {
//                    // CÓDIGO CRÍTICO: Você precisa de um método 'buildFrequencia' que mapeie o ResultSet para o objeto FrequenciaAtividade
//                    // e que preencha os objetos Membro e AtividadeRealizada aninhados.
//                    // Assumindo um método de mapeamento (buildFrequencia) e que o DAO tenha acesso aos outros DAOs
//
//                    // Exemplo simplificado (você deve implementar o mapeamento completo):
//                    FrequenciaAtividade freq = new FrequenciaAtividade();
//
//                    // Mapeamento de campos básicos
//                    freq.setId(rs.getInt("id_frequencia"));
//                    freq.setStatusParticipacao(rs.getBoolean("status_participacao"));
//
//                    // Busca e anexa o Membro
//                    int idMembro = rs.getInt("id_membro");
//                    Membro membro = membroDAO.get(idMembro); // Usa o MembroDAO injetado
//
//                    // Busca e anexa a Atividade
//                    CriarRealizacaoAtividades atividade = craDAO.getById(rs.getInt("id_realizacao_atividades")); // Assumindo método get(int id) no craDAO
//
//                    freq.setMembro(membro);
//                    freq.setAtividadeRealizada(atividade);
//
//                    frequencias.add(freq);
//                }
//            }
//        } catch (SQLException e) {
//            System.err.println("Erro ao buscar frequências por atividade: " + e.getMessage());
//            // Lança a exceção para ser tratada em camadas superiores
//            throw new RuntimeException("Erro de SQL ao buscar frequências.", e);
//        } finally {
//            // Lembre-se de fechar o ResultSet e o Statement aqui.
//            // ...
//        }
//        return frequencias;
//    }
//
//    /**
//     * Registra a frequência de um membro em uma atividade.
//     */
//    public FrequenciaAtividade salvarFrequencia(FrequenciaAtividade freq) {
//        String sql = String.format(
//                "INSERT INTO frequencia_atividade (id_realizacao_atividades, id_membro, status_participacao, data_registro) " +
//                        "VALUES (%d, %d, %s, %s)",
//                freq.getAtividadeRealizada().getId(),
//                freq.getMembro().getId(),
//                freq.getStatusParticipacao() ? "'TRUE'" : "'FALSE'",
//                formatDateTime(LocalDateTime.now())
//        );
//        /*
//        String sql = String.format(
//                "INSERT INTO frequencia_atividade (id_criacao, id_membro, status_participacao, data_registro) " +
//                        "VALUES (%d, %d, %s, %s)",
//                freq.getAtividadeRealizada().getId(),
//                freq.getMembro().getId(),
//                freq.getStatusParticipacao() ? "'TRUE'" : "'FALSE'",
//                formatDateTime(LocalDateTime.now()) // Usa o horário do servidor
//        );
//        */
//        if (SingletonDB.getConexao().manipular(sql)) {
//            int idGerado = SingletonDB.getConexao().getMaxPK("frequencia_atividade", "id_frequencia");
//            freq.setId(idGerado);
//            return freq;
//        }
//        throw new RuntimeException("Falha ao registrar a frequência no banco de dados.");
//    }
//
//    /**
//     * Verifica se o membro já tem frequência registrada naquela atividade.
//     */
//    public boolean jaRegistrado(int idCriacao, int idMembro) {
//        String sql = String.format(
//                "SELECT 1 FROM frequencia_atividade WHERE id_realizacao_atividades = %d AND id_membro = %d",
//                idCriacao, idMembro
//        );
//        /*String sql = String.format(
//                "SELECT 1 FROM frequencia_atividade WHERE id_criacao = %d AND id_membro = %d",
//                idCriacao, idMembro
//        );
//
//         */
//        ResultSet rs = SingletonDB.getConexao().consultar(sql);
//        try {
//            if (rs != null && rs.next()) {
//                return true;
//            }
//        } catch (SQLException e) {
//            System.out.println("Erro ao verificar frequência: " + e.getMessage());
//        }
//        return false;
//    }
//}