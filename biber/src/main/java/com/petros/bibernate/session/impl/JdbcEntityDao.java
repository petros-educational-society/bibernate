package com.petros.bibernate.session.impl;

import com.petros.bibernate.annotation.JoinTable;
import com.petros.bibernate.annotation.ManyToMany;
import com.petros.bibernate.collection.LazyList;
import com.petros.bibernate.collection.LazySet;
import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.entity.metamodel.EntityMetamodel;
import com.petros.bibernate.session.PersistenceContext;
import com.petros.bibernate.session.util.EntityKey;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.petros.bibernate.session.util.EntityUtil.getId;
import static com.petros.bibernate.session.util.EntityUtil.getMappedByRelatedEntityField;
import static com.petros.bibernate.session.util.EntityUtil.getRelatedEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isManyToManyEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isManyToOneEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isOneToManyEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isOneToOneEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isRegularField;
import static com.petros.bibernate.session.util.EntityUtil.resolveColumnName;
import static com.petros.bibernate.session.util.EntityUtil.resolveTableName;

@Log4j2
@RequiredArgsConstructor
public class JdbcEntityDao {

    private final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?;";

    private final DataSourceImpl dataSource;
    private final PersistenceContext persistenceContext;
    private final Map<Class<?>, EntityMetamodel> entityMetamodels;

    @SneakyThrows
    public <T> T findById(Class<T> entityType,
                          Object id) {
        final EntityMetamodel entityMetamodel = entityMetamodels.get(entityType);
        final String idColumn = entityMetamodel.identifierProperty()
                                               .columnName();

        var cachedEntity = persistenceContext.getEntity(new EntityKey<T>(entityType,
                                                                         id));
        if (cachedEntity != null) {
            log.trace("Returning cached entity from the context {}",
                      cachedEntity);
            return entityType.cast(cachedEntity);
        }
        return findOneBy(entityType,
                         idColumn,
                         id);
    }

    @SneakyThrows
    public <T> List<T> findAllBy(Class<T> entityType,
                                 Field field,
                                 Object columnValue) {
        List<T> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String columnName = resolveColumnName(field);
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN,
                                            tableName,
                                            columnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1,
                                          columnValue);
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    list.add(createEntityFromResultSet(entityType,
                                                       resultSet));
                }
            }
        }
        return list;
    }

    @SneakyThrows
    private <T> T findOneBy(Class<T> entityType,
                            String columnName,
                            Object columnValue) {
        try (Connection connection = dataSource.getConnection()) {
            final EntityMetamodel entityMetamodel = entityMetamodels.get(entityType);
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN,
                                            entityMetamodel.tableName(),
                                            columnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1,
                                          columnValue);
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                return createEntityFromResultSet(entityType,
                                                 resultSet);
            }
        }
    }

    @SneakyThrows
    private <T> T createEntityFromResultSet(Class<T> entityType,
                                            ResultSet resultSet) {

        final EntityMetamodel entityMetamodel = entityMetamodels.get(entityType);
        final Constructor<T> constructor = (Constructor<T>) entityMetamodel.noArgsConstructor();
        constructor.setAccessible(true);

        T entity = constructor.newInstance();
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (isRegularField(field)) {
                resolveRegularFieldValue(resultSet,
                                         entity,
                                         field);
            } else if (isOneToOneEntityField(field) || isManyToOneEntityField(field)) {
                resolveEntityFieldValue(resultSet,
                                        entity,
                                        field);
            } else if (isOneToManyEntityField(field)) {
                resolveEntityCollectionValue(entityType,
                                             entity,
                                             field);
            } else if (isManyToManyEntityField(field)) {
                resolveManyToManyEntityValue(entity,
                                             field);

            }
        }
        return persistenceContext.addEntity(entity);
    }

    private <T> void resolveEntityCollectionValue(Class<T> entityType,
                                                  T entity,
                                                  Field field)
            throws IllegalAccessException {
        Class<?> relatedEntityType = getEntityCollectionEntityType(field);
        var entityFieldInRelatedEntity = getRelatedEntityField(entityType,
                                                               relatedEntityType);
        var entityId = getId(entity);
        var list = new LazyList<>(() -> findAllBy(relatedEntityType,
                                                  entityFieldInRelatedEntity,
                                                  entityId));
        field.set(entity,
                  list);
    }

    @SneakyThrows
    private <T> void resolveManyToManyEntityValue(T entity,
                                                  Field field) {
        String mappedBy = field.getAnnotation(ManyToMany.class)
                               .mappedBy();
        if (Objects.nonNull(mappedBy) && !mappedBy.isEmpty()) {
            Class<?> relatedEntityType = getEntityCollectionEntityType(field);
            var entityFieldInRelatedEntity = getMappedByRelatedEntityField(relatedEntityType,
                                                                           field);
            String joinTableName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class)
                                                             .value();
            String joinColumnName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class)
                                                              .joinColumns()[0].value();
            String inverseJoinColumnName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class)
                                                                     .inverseJoinColumns()[0].value();
            var associatedEntities = new LazySet<>(() -> findAllManyToMany(entity,
                                                                           field,
                                                                           joinTableName,
                                                                           inverseJoinColumnName,
                                                                           joinColumnName));
            field.set(entity,
                      associatedEntities);
        } else {
            String joinTableName = field.getAnnotation(JoinTable.class)
                                        .value();
            String joinColumnName = field.getAnnotation(JoinTable.class)
                                         .joinColumns()[0].value();
            String inverseJoinColumnName = field.getAnnotation(JoinTable.class)
                                                .inverseJoinColumns()[0].value();
            var associatedEntities = new LazySet<>(() -> findAllManyToMany(entity,
                                                                           field,
                                                                           joinTableName,
                                                                           joinColumnName,
                                                                           inverseJoinColumnName));
            field.set(entity,
                      associatedEntities);
        }
    }

    @SneakyThrows
    public <T> Set<T> findAllManyToMany(T entity,
                                        Field field,
                                        String joinTableName,
                                        String joinColumnName,
                                        String inverseJoinColumnName) {
        Set<T> associatedEntities = new HashSet<>();
        var entityId = getId(entity);
        try (Connection connection = dataSource.getConnection()) {
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN,
                                            joinTableName,
                                            joinColumnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1,
                                          entityId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var associatedEntity = (T) findById(getEntityCollectionEntityType(field),
                                                            resultSet.getObject(inverseJoinColumnName));
                        associatedEntities.add(associatedEntity);
                    }
                }
            }
            return associatedEntities;
        }
    }

    private <T> void resolveEntityFieldValue(ResultSet resultSet,
                                             T entity,
                                             Field field)
            throws SQLException, IllegalAccessException {
        var relatedEntityType = field.getType();
        String joinColumnName = resolveColumnName(field);
        var joinColumnValue = resultSet.getObject(joinColumnName);
        var relatedEntity = findById(relatedEntityType,
                                     joinColumnValue);
        field.set(entity,
                  relatedEntity);
    }

    private <T> void resolveRegularFieldValue(ResultSet resultSet,
                                              T entity,
                                              Field field)
            throws SQLException, IllegalAccessException {
        field.setAccessible(true);
        String fieldName = resolveColumnName(field);
        Object fieldValue = resultSet.getObject(fieldName);
        field.set(entity,
                  fieldValue);
    }

    private Class<?> getEntityCollectionEntityType(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        return (Class<?>) typeArgument;
    }

}
