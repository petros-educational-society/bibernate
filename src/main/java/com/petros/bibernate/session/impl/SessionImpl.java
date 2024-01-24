package com.petros.bibernate.session.impl;

import com.petros.bibernate.session.Session;
import lombok.SneakyThrows;

import javax.sql.DataSource;

public class SessionImpl implements Session {

    private final EntityManager entityManager;

    public SessionImpl(DataSource dataSource) {
        this.entityManager = new EntityManager(dataSource);
    }

    @Override
    @SneakyThrows
    public <T> T find(Class<T> entityType, Object id) {
        return entityManager.findById(entityType, id);
    }
}
