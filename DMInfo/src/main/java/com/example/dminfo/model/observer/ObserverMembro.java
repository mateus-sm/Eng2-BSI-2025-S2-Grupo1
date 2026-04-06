package com.example.dminfo.model.observer;

import com.example.dminfo.model.Usuario;
import com.example.dminfo.util.Conexao;
import org.springframework.mail.javamail.JavaMailSender;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ObserverMembro {
    public void notificarMembros(String motivo, JavaMailSender mailSender);
}
