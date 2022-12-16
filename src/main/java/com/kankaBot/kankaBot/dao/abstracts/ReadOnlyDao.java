package com.kankaBot.kankaBot.dao.abstracts;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReadOnlyDao <E, K> {
    List<E> getAll();
    boolean existsById(K id);
    Optional<E> getById(K id);
    List<E> getAllByIds(Iterable<K> ids);
    boolean existsByAllIds(Collection<K> ids);

}
