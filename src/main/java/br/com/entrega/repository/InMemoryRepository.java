package br.com.entrega.repository;

import br.com.entrega.domain.Entidade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryRepository<T extends Entidade> implements CrudRepository<T> {
    private final HashMap<Long, T> storage = new HashMap<>();
    private long nextId = 1;

    @Override
    public synchronized T save(T entity) {
        if (entity.getId() == 0) {
            entity.setId(nextId++);
        }
        entity.marcarAtualizacao();
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public synchronized Optional<T> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized ArrayList<T> findAll() {
        ArrayList<T> items = new ArrayList<>(storage.values());
        items.sort(Comparator.comparingLong(Entidade::getId));
        return items;
    }

    @Override
    public synchronized void deleteById(long id) {
        storage.remove(id);
    }

    @Override
    public synchronized boolean existsById(long id) {
        return storage.containsKey(id);
    }
}
