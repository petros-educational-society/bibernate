package com.petros.bibernate.session.impl;

import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.entity.metamodel.EntityMetamodel;
import com.petros.bibernate.session.Session;
import lombok.SneakyThrows;

import java.util.Map;

public class SessionImpl
        implements Session {
    private final JdbcEntityDao jdbcEntityDao;

    public SessionImpl(DataSourceImpl dataSource,
                       Map<Class<?>, EntityMetamodel> entityMetamodels) {
        this.jdbcEntityDao = new JdbcEntityDao(dataSource, new StatefulPersistenceContext(), entityMetamodels);
    }

    @Override
    @SneakyThrows
    public <T> T find(Class<T> entityType, Object id) {
        return jdbcEntityDao.findById(entityType, id);
    }

}
