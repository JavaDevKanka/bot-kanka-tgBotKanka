package com.kankaBot.kankaBot.service.abstracts;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReadOnlyService<E, K> {
    List<E> getAll();
    boolean existsById(K id);
    Optional<E> getById(K id);
    List<E> getAllByIds(Iterable<K> ids);
    boolean existsByAllIds(Collection<K> ids);
}