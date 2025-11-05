package com.example.dminfo.util;

public class SingletonDB {
    private static com.example.dminfo.util.Conexao conexao = null;

    private SingletonDB() {
    }

    public static boolean conectarDB(){
        conexao = new com.example.dminfo.util.Conexao();
        return conexao.conectar("jdbc:postgresql://localhost:5432/",
                "dminfo_db","postgres","admin");
    }
    public static com.example.dminfo.util.Conexao getConexao(){
        return conexao;
    }
}
