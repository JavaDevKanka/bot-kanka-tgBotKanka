package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.Exceptions.ConstraintException;
import com.kankaBot.kankaBot.dao.abstracts.ReadWriteDao;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public abstract class ReadWriteServiceImpl<E, K> extends ReadOnlyServiceImpl<E, K> {
    private final ReadWriteDao<E, K> readWriteDao;
    private static final String ENTITIES_MUST_NOT_BE_NULL = "Entities cannot be null and empty";

    public ReadWriteServiceImpl(ReadWriteDao<E, K> readWriteDao) {
        super(readWriteDao);
        this.readWriteDao = readWriteDao;
    }

    @Transactional
    public void persist(E e) {
        readWriteDao.persist(e);
    }

    @Transactional
    public void update(E e) {
        readWriteDao.update(e);
    }

    @Transactional
    public void delete(E e) {
        readWriteDao.delete(e);
    }

    @Transactional
    public void deleteById(K id) {
        readWriteDao.deleteById(id);
    }

    @Transactional
    public void persistAll(E... entities) {
        if (entities == null || entities.length == 0) {
            throw new ConstraintException(ENTITIES_MUST_NOT_BE_NULL);
        }
        readWriteDao.persistAll(entities);
    }

    @Transactional
    public void persistAll(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new ConstraintException(ENTITIES_MUST_NOT_BE_NULL);
        }
        readWriteDao.persistAll(entities);
    }

    @Transactional
    public void deleteAll(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            throw new ConstraintException(ENTITIES_MUST_NOT_BE_NULL);
        }
        readWriteDao.deleteAll(entities);
    }

    @Transactional
    public void updateAll(Iterable<? extends E> entities) {
        if (entities == null || !entities.iterator().hasNext()) {
            throw new ConstraintException(ENTITIES_MUST_NOT_BE_NULL);
        }
        readWriteDao.updateAll(entities);
    }
}