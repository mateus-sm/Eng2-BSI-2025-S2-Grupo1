package com.example.dminfo.util;

public class SingletonDB {
    private static Conexao conexao;

    private SingletonDB() {} // Construtor privado

    public static Conexao getConexao() {
        if (conexao == null) {
            conexao = new Conexao();
            if (!conexao.conectar()) {
                // Não conseguiu conectar, retorna null para que os DAOs falhem
                return null;
            }
        }
        return conexao;
    }
}