package com.petros.bibernate.session.impl;

import com.petros.bibernate.action.EntityAction;
import com.petros.bibernate.action.EntityDeleteAction;
import com.petros.bibernate.action.EntityInsertAction;
import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.session.PersistenceContext;
import com.petros.bibernate.session.Session;
import com.petros.bibernate.session.util.EntityKey;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.PriorityQueue;
import java.util.Queue;

import static java.util.Comparator.comparing;

@Log4j2
public class SessionImpl implements Session {

    private final JdbcEntityDao jdbcEntityDao;
    private final PersistenceContext persistenceContext = new StatefulPersistenceContext();
    private final Queue<EntityAction> actionQueue = new PriorityQueue<>(comparing(EntityAction::priority));

    public SessionImpl(DataSourceImpl dataSource) {
        this.jdbcEntityDao = new JdbcEntityDao(dataSource, persistenceContext);
    }

    @Override
    @SneakyThrows
    public <T> T find(Class<T> entityType, Object id) {
        log.info("Finding entity {} by id = {}", entityType.getSimpleName(), id);
        return jdbcEntityDao.findById(entityType, id);
    }

    @Override
    @SneakyThrows
    public <T> T insert(T entity) {
        log.info("Persisting entity {}", entity);
        if (persistenceContext.containsEntity(EntityKey.valueOf(entity))) {
            throw new RuntimeException("Entity already exists");
        }
        persistenceContext.addEntity(entity);
        log.trace("Adding EntityInsertAction for entity {} to the ActionQueue", entity);
        actionQueue.add(new EntityInsertAction(entity, jdbcEntityDao));

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        log.info("Removing entity {}", entity);
        var managedEntity = persistenceContext.getEntity(EntityKey.valueOf(entity));
        if (managedEntity == null) {
            throw new RuntimeException("Cannot remove an entity that are not in the current session");
        }
        actionQueue.add(new EntityDeleteAction(entity, jdbcEntityDao));
    }

    @Override
    public void flush() {
        log.trace("Session flush");
//        dirtyChecking();
        processActionQueue();
    }

    private void processActionQueue() {
        log.trace("Flushing ActionQueue");
        while (!actionQueue.isEmpty()) {
            var entityAction = actionQueue.poll();
            entityAction.execute();
        }
    }

    @Override
    public void close() {
        log.info("Closing session");
        flush();
        persistenceContext.clear();
    }
}
