package com.petros.bibernate.session.impl;

import com.petros.bibernate.annotation.JoinTable;
import com.petros.bibernate.annotation.ManyToMany;
import com.petros.bibernate.collection.LazyList;
import com.petros.bibernate.collection.LazySet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.petros.bibernate.session.util.EntityUtil.*;

@RequiredArgsConstructor
public class JdbcEntityDao {

    private final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?;";

    private final DataSource dataSource;

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        Field idField = getIdField(entityType);
        return findOneBy(entityType, idField, id);
    }

    @SneakyThrows
    public <T> List<T> findAllBy(Class<T> entityType, Field field, Object columnValue) {
        List<T> list = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String columnName = resolveColumnName(field);
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN, tableName, columnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1, columnValue);
                ResultSet resultSet = selectStatement.executeQuery();
                while (resultSet.next()) {
                    list.add(createEntityFromResultSet(entityType, resultSet));
                }
            }
        }
        return list;
    }

    @SneakyThrows
    private  <T> T findOneBy(Class<T> entityType, Field field, Object columnValue) {
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String columnName = resolveColumnName(field);
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN, tableName, columnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1, columnValue);
                ResultSet resultSet = selectStatement.executeQuery();
                resultSet.next();
                return createEntityFromResultSet(entityType, resultSet);
            }
        }
    }

    @SneakyThrows
    private <T> T createEntityFromResultSet(Class<T> entityType, ResultSet resultSet) {
        Constructor<T> constructor = entityType.getConstructor();
        T entity = constructor.newInstance();
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (isRegularField(field)) {
                resolveRegularFieldValue(resultSet, entity, field);
            } else if (isOneToOneEntityField(field) || isManyToOneEntityField(field)) {
                resolveEntityFieldValue(resultSet, entity, field);
            } else if (isOneToManyEntityField(field)) {
                resolveEntityCollectionValue(entityType, entity, field);
            } else if (isManyToManyEntityField(field)) {
                resolveManyToManyEntityValue(entity, field);

            }
        }
        return entity;
    }

    private <T> void resolveEntityCollectionValue(Class<T> entityType, T entity, Field field) throws IllegalAccessException {
        Class<?> relatedEntityType = getEntityCollectionEntityType(field);
        var entityFieldInRelatedEntity = getRelatedEntityField(entityType, relatedEntityType);
        var entityId = getId(entity);
        var list = new LazyList<>(() -> findAllBy(relatedEntityType, entityFieldInRelatedEntity, entityId));
        field.set(entity, list);
    }

    @SneakyThrows
    private <T> void resolveManyToManyEntityValue(T entity, Field field) {
        String mappedBy = field.getAnnotation(ManyToMany.class).mappedBy();
        if (Objects.nonNull(mappedBy) && !mappedBy.isEmpty()) {
            Class<?> relatedEntityType = getEntityCollectionEntityType(field);
            var entityFieldInRelatedEntity = getMappedByRelatedEntityField(relatedEntityType, field);
            String joinTableName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class).value();
            String joinColumnName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class).joinColumns()[0].value();
            String inverseJoinColumnName = entityFieldInRelatedEntity.getAnnotation(JoinTable.class).inverseJoinColumns()[0].value();
            var associatedEntities = new LazySet<>(() -> findAllManyToMany(entity, field, joinTableName, inverseJoinColumnName, joinColumnName));
            field.set(entity, associatedEntities);
        } else {
            String joinTableName = field.getAnnotation(JoinTable.class).value();
            String joinColumnName = field.getAnnotation(JoinTable.class).joinColumns()[0].value();
            String inverseJoinColumnName = field.getAnnotation(JoinTable.class).inverseJoinColumns()[0].value();
            var associatedEntities = new LazySet<>(() -> findAllManyToMany(entity, field, joinTableName, joinColumnName, inverseJoinColumnName));
            field.set(entity, associatedEntities);
        }
    }

    @SneakyThrows
    public <T> Set<T> findAllManyToMany(T entity, Field field, String joinTableName, String joinColumnName, String inverseJoinColumnName) {
        Set<T>  associatedEntities = new HashSet<>();
        var entityId = getId(entity);
        try (Connection connection = dataSource.getConnection()) {
            String sqlQuery = String.format(SELECT_FROM_TABLE_BY_COLUMN, joinTableName, joinColumnName);
            try (PreparedStatement selectStatement = connection.prepareStatement(sqlQuery)) {
                selectStatement.setObject(1, entityId);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var associatedEntity = (T) findById(getEntityCollectionEntityType(field), resultSet.getObject(inverseJoinColumnName));
                        associatedEntities.add(associatedEntity);
                    }
                }
            }
            return associatedEntities;
        }
    }

    private <T> void resolveEntityFieldValue(ResultSet resultSet, T entity, Field field) throws SQLException, IllegalAccessException {
        var relatedEntityType = field.getType();
        String joinColumnName = resolveColumnName(field);
        var joinColumnValue = resultSet.getObject(joinColumnName);
        var relatedEntity = findById(relatedEntityType, joinColumnValue);
        field.set(entity, relatedEntity);
    }

    private <T> void resolveRegularFieldValue(ResultSet resultSet, T entity, Field field) throws SQLException, IllegalAccessException {
        field.setAccessible(true);
        String fieldName = resolveColumnName(field);
        Object fieldValue = resultSet.getObject(fieldName);
        field.set(entity, fieldValue);
    }

    private Class<?> getEntityCollectionEntityType(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        return (Class<?>) typeArgument;
    }
}
