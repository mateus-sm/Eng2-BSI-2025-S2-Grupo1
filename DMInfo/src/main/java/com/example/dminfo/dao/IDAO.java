package com.example.dminfo.dao;

import com.example.dminfo.util.Conexao;

import java.sql.ResultSet;

public interface IDAO<T>{
    public Object create(T entidade, Conexao conexao);
    public Object read(T entidade, Conexao conexao);
    public Object update(T entidade, Conexao conexao);
    public boolean delete(int id, Conexao conexao);
    public ResultSet readAll(String filtro, Conexao conexao);
    public ResultSet getById(int id, Conexao conexao);
}
