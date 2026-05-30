package br.com.entrega.domain;

import java.util.Map;

public class Usuario extends Pessoa {
    private PerfilUsuario perfil;

    public Usuario(String nome, String email, PerfilUsuario perfil, boolean ativo) {
        super(nome, email, ativo);
        this.perfil = perfil;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    @Override
    public String getPapelNoSistema() {
        return perfil == PerfilUsuario.ADMIN ? "Administracao" : "Cliente";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = baseMap();
        map.put("nome", getNome());
        map.put("email", getEmail());
        map.put("perfil", perfil.name());
        map.put("papelNoSistema", getPapelNoSistema());
        map.put("ativo", isAtivo());
        return map;
    }
}
