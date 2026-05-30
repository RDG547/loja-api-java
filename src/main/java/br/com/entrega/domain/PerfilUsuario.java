package br.com.entrega.domain;

import br.com.entrega.exception.ValidationException;

public enum PerfilUsuario {
    CLIENTE,
    ADMIN;

    public static PerfilUsuario from(String value) {
        if (value == null || value.isBlank()) {
            return CLIENTE;
        }

        try {
            return PerfilUsuario.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Perfil de usuario invalido. Use CLIENTE ou ADMIN.");
        }
    }
}
