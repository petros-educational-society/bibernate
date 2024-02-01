package com.petros.bibernate.session;

import com.petros.bibernate.session.util.EntityKey;

/**
 * @author Maksym Oliinyk
 */
public interface PersistenceContext {

    /**
     * Add a canonical mapping from entity key to entity instance
     *
     * @param entity The entity instance to add
     */
    <T> T addEntity(T entity);

    /**
     * Get the entity instance associated with the given key
     * Params:
     * key – The key under which to look for an entity
     *
     * @param <T>
     */
    <T> T getEntity(EntityKey<T> key);

    /**
     * Is there an entity with the given key in the persistence context
     *
     * @param key The key under which to look for an entity
     *
     * @return {@code true} indicates an entity was found; otherwise {@code false}
     */
    <T> boolean containsEntity(EntityKey<T> key);

    /**
     * Remove an entity.
     *
     * @param key The key whose matching entity should be removed
     *
     * @return The matching entity
     */
    <T> T removeEntity(EntityKey<T> key);

}
