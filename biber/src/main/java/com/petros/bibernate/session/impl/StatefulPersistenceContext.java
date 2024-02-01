package com.petros.bibernate.session.impl;

import com.petros.bibernate.session.PersistenceContext;
import com.petros.bibernate.session.util.EntityKey;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maksym Oliinyk
 */
@Log4j2
public class StatefulPersistenceContext
        implements PersistenceContext {

    private static final Map<EntityKey<?>, Object> entitiesByKey = new ConcurrentHashMap<>();

    @Override
    public <T> T addEntity(final T entity) {
        final EntityKey<T> entityKey = EntityKey.valueOf(entity);
        T persistedEntity = getEntity(entityKey);
        if (persistedEntity != null) {
            log.trace("Entity with key {} already exists in the context",
                      entityKey);
        } else {
            persistedEntity = entity;
            entitiesByKey.put(entityKey,
                              persistedEntity);
        }
        return persistedEntity;

    }

    @Override
    public <T> T getEntity(final EntityKey<T> entityKey) {
        log.trace("Getting entity from the context by key {}",
                  entityKey);
        return entityKey.entityClass()
                        .cast(entitiesByKey.get(entityKey));
    }

    @Override
    public <T> boolean containsEntity(final EntityKey<T> key) {
        return entitiesByKey.containsKey(key);
    }

    @Override
    public <T> T removeEntity(final EntityKey<T> key) {
        return key.entityClass()
                  .cast(entitiesByKey.remove(key));
    }

    public void clear() {
        entitiesByKey.clear();
    }

}
