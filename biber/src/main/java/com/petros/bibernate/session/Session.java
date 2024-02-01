package com.petros.bibernate.session;

public interface Session {

    <T> T find(Class<T> entityType, Object id);
    <T> T insert(T entity);
    <T> void remove(T entity);
    void flush();
    void close();
}
