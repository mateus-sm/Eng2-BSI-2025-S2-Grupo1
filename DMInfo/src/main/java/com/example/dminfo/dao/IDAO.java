package com.example.dminfo.dao;

import com.example.dminfo.util.Conexao;

import java.util.List;

public interface IDAO<T>{
    public Object create(T entidade, Conexao conexao);
    public Object read(T entidade, Conexao conexao);
    public Object update(T entidade, Conexao conexao);
    public boolean delete(int id, Conexao conexao);
    public List<T> readAll(String filtro, Conexao conexao);
}
