package com.example.dminfo.util;

import java.util.Base64;
import java.util.UUID;

public class Token {

    /**
     * Gera um token simples baseado em um UUID e no login.
     * Em um projeto real, isso seria um JWT (Json Web Token) mais complexo.
     */
    public static String gerarToken(String login) {
        // Cria um token simples: "login_base64(uuid_aleatorio)"
        String uuid = UUID.randomUUID().toString();
        String tokenPayload = login + ":" + uuid;

        // Codifica para Base64 para garantir que é uma string segura
        return Base64.getEncoder().encodeToString(tokenPayload.getBytes());
    }

    /**
     * Valida um token decodificando-o.
     * Isso será usado para proteger seus endpoints.
     */
    public static boolean validarToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            // Tenta decodificar o token
            byte[] decodedBytes = Base64.getDecoder().decode(token);
            String decodedString = new String(decodedBytes);

            // Verifica se o formato está correto (ex: "login:uuid")
            return decodedString.contains(":") && decodedString.split(":").length >= 2;
        } catch (IllegalArgumentException e) {
            // Se não for um Base64 válido
            return false;
        }
    }
}