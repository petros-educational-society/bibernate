package com.petros.bibernate.action;

import com.petros.bibernate.session.impl.JdbcEntityDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityDeleteAction implements EntityAction {
    private final Object entity;
    private final JdbcEntityDao jdbcEntityDao;

    @Override
    public void execute() {
        jdbcEntityDao.delete(entity);
    }

    @Override
    public int priority() {
        return 3;
    }
}
