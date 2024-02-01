package com.petros.bibernate.session.util;

/**
 * @author Maksym Oliinyk
 */
public record EntityKey<T>(Class<T> entityClass, Object identifier) {

    @SuppressWarnings("unchecked")
    public static <T> EntityKey<T> valueOf(T entity) {
        return new EntityKey<>((Class<T>) entity.getClass(),
                               EntityUtil.getId(entity));
    }

}
