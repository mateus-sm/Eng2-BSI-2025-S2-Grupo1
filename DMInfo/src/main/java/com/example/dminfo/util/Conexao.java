package com.example.dminfo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {
    private Connection connection;

    private String USUARIO = "Admin";
    private String SENHA = "admin@123";
    private String URL = "jdbc:postgresql://localhost:5432/dminfo_db";

    public Conexao() {
        this.connection = null;
    }

    public boolean conectar() {
        try {
            // Carrega o driver do PostgreSQL
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(URL, USUARIO, SENHA);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco: " + e.getMessage());
            return false;
        }
    }

    public void desconectar() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    // Metodo para INSERT e SELECT
    public ResultSet consultar(String sql) {
        try {
            Statement statement = this.connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta SQL: " + e.getMessage());
            return null;
        }
    }

    // Metodo para UPDATE, DELETE, INSERT
    public boolean executar(String sql) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao executar SQL: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() {
        return this.connection;
    }
}