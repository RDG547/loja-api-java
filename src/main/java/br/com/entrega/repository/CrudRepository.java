package br.com.entrega.repository;

import br.com.entrega.domain.Entidade;

import java.util.ArrayList;
import java.util.Optional;

public interface CrudRepository<T extends Entidade> {
    T save(T entity);

    Optional<T> findById(long id);

    ArrayList<T> findAll();

    void deleteById(long id);

    boolean existsById(long id);
}
