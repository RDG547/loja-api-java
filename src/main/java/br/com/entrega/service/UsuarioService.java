package br.com.entrega.service;

import br.com.entrega.domain.PerfilUsuario;
import br.com.entrega.domain.Usuario;
import br.com.entrega.exception.ConflictException;
import br.com.entrega.exception.NotFoundException;
import br.com.entrega.exception.ValidationException;
import br.com.entrega.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UsuarioService {
    private final CrudRepository<Usuario> repository;

    public UsuarioService(CrudRepository<Usuario> repository) {
        this.repository = repository;
    }

    public Usuario cadastrar(Map<String, Object> data) {
        String nome = Input.requiredString(data, "nome");
        String email = Input.requiredString(data, "email").toLowerCase(Locale.ROOT);
        PerfilUsuario perfil = PerfilUsuario.from(Input.optionalString(data, "perfil", "CLIENTE"));
        boolean ativo = Input.optionalBoolean(data, "ativo", true);

        validarUsuario(nome, email);
        garantirEmailDisponivel(email, 0);

        Usuario usuario = new Usuario(nome, email, perfil, ativo);
        return repository.save(usuario);
    }

    public Usuario buscarPorId(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
    }

    public List<Map<String, Object>> listar(Map<String, String> filtros) {
        String nome = filtros.getOrDefault("nome", "").trim().toLowerCase(Locale.ROOT);
        String email = filtros.getOrDefault("email", "").trim().toLowerCase(Locale.ROOT);

        ArrayList<Map<String, Object>> result = new ArrayList<>();
        for (Usuario usuario : repository.findAll()) {
            boolean matchesNome = nome.isBlank() || usuario.getNome().toLowerCase(Locale.ROOT).contains(nome);
            boolean matchesEmail = email.isBlank() || usuario.getEmail().toLowerCase(Locale.ROOT).contains(email);
            if (matchesNome && matchesEmail) {
                result.add(usuario.toMap());
            }
        }
        return result;
    }

    public Usuario atualizar(long id, Map<String, Object> data) {
        Usuario usuario = buscarPorId(id);

        String nome = Input.optionalString(data, "nome", usuario.getNome());
        String email = Input.optionalString(data, "email", usuario.getEmail()).toLowerCase(Locale.ROOT);
        PerfilUsuario perfil = PerfilUsuario.from(Input.optionalString(data, "perfil", usuario.getPerfil().name()));
        boolean ativo = Input.optionalBoolean(data, "ativo", usuario.isAtivo());

        validarUsuario(nome, email);
        garantirEmailDisponivel(email, id);

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setPerfil(perfil);
        usuario.setAtivo(ativo);
        return repository.save(usuario);
    }

    public void remover(long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Usuario nao encontrado.");
        }
        repository.deleteById(id);
    }

    private void validarUsuario(String nome, String email) {
        if (nome.length() < 2) {
            throw new ValidationException("Nome deve possuir pelo menos 2 caracteres.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new ValidationException("E-mail invalido.");
        }
    }

    private void garantirEmailDisponivel(String email, long ignoredId) {
        for (Usuario usuario : repository.findAll()) {
            if (usuario.getId() != ignoredId && usuario.getEmail().equalsIgnoreCase(email)) {
                throw new ConflictException("Ja existe usuario cadastrado com este e-mail.");
            }
        }
    }
}
