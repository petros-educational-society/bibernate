package com.petros.bibernate.session.impl;

import com.petros.bibernate.annotation.JoinTable;
import com.petros.bibernate.annotation.ManyToMany;
import com.petros.bibernate.collection.LazyList;
import com.petros.bibernate.collection.LazySet;
import com.petros.bibernate.datasource.DataSourceImpl;
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
import java.util.*;

import static com.petros.bibernate.session.util.EntityUtil.*;
@Log4j2
@RequiredArgsConstructor
public class JdbcEntityDao {

    private final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    public final String INSERT_INTO_TABLE_VALUES_TEMPLATE = "insert into %s(%s) values(%s)";
    public final String UPDATE_TABLE_SET_VALUES_BY_COLUMN_TEMPLATE = "update %s set %s where %s";
    public final String DELETE_FROM_TABLE_BY_COLUMN = "delete from %s where %s = ?";

    private final DataSourceImpl dataSource;
    private final PersistenceContext persistenceContext;

    @SneakyThrows
    public <T> T findById(Class<T> entityType, Object id) {
        Field idField = getIdField(entityType);
        var cachedEntity = persistenceContext.getEntity(new EntityKey<T>(entityType, id));
        if (cachedEntity != null) {
            log.trace("Returning cached entity from the context {}", cachedEntity);
            return entityType.cast(cachedEntity);
        }
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
        return persistenceContext.addEntity(entity);
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

    @SneakyThrows
    public <T> T insert(T entity) {
        log.trace("Inserting entity {}", entity);
        var entityType = entity.getClass();
        Map<Field, Object> idByField = getEntityIdsOfNoRegularFieldsIfExist(entity, entityType);
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String columns = commaSeparatedInsertableColumns(entityType);
            String params = commaSeparatedInsertableParams(entityType);
            String insertQuery = String.format(INSERT_INTO_TABLE_VALUES_TEMPLATE, tableName, columns, params);
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                fillInsertStatementParams(insertStatement, entity, idByField);
                log.debug("SQL: {}", insertStatement);
                insertStatement.executeUpdate();
            }
        }
        return entity;
    }

    private <T> Map<Field, Object> getEntityIdsOfNoRegularFieldsIfExist(T entity, Class<?> entityType) throws IllegalAccessException {
        List<Field> fieldsWithEntityType = Arrays.stream(getInsertableFields(entityType)).filter(f -> !isRegularField(f)).toList();
        Map<Field, Object> idByField = null;
        if (!fieldsWithEntityType.isEmpty()) {
            idByField = new HashMap<>();
            for (var field: fieldsWithEntityType) {
                field.setAccessible(true);
                Object columnValue = field.get(entity);
                if (Objects.isNull(columnValue)) {
                    throw new IllegalArgumentException("%s is mandatory field for entity %s".formatted(field.getType(), entityType));
                }
                Object result = insert(columnValue);
                Object id = getId(result);
                idByField.put(field, id);
            }
        }
        return idByField;
    }

    @SneakyThrows
    public <T> T update(T entity) {
        log.trace("Updating entity {}", entity);
        var entityType = entity.getClass();
        Map<Field, Object> idByField = getEntityIdsOfNoRegularFieldsIfExist(entity, entityType);
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String updatableColumns = commaSeparatedUpdatableColumnSetters(entityType);
            String idColumn = resolveIdColumnName(entityType) + " = ?";
            var updateQuery = String.format(UPDATE_TABLE_SET_VALUES_BY_COLUMN_TEMPLATE, tableName, updatableColumns, idColumn);
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                fillUpdateStatementParams(updateStatement, entity, idByField);
                int idParamIndex = getUpdatableFields(entityType).length + 1;
                updateStatement.setObject(idParamIndex, getId(entity));
                log.debug("SQL: " + updateStatement);
                updateStatement.executeUpdate();
            }
        }
        return entity;
    }

    @SneakyThrows
    public <T> T delete(T entity) {
        log.trace("Deleting entity {}", entity);
        var entityType = entity.getClass();
        try (Connection connection = dataSource.getConnection()) {
            String tableName = resolveTableName(entityType);
            String idColumnName = resolveIdColumnName(entityType);
            var deleteQuery = String.format(DELETE_FROM_TABLE_BY_COLUMN, tableName, idColumnName);
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                Object id = getId(entity);
                deleteStatement.setObject(1, id);
                log.debug("SQL: " + deleteStatement);
                deleteStatement.executeUpdate();
            }
        }
        return entity;
    }

    @SneakyThrows
    private <T> void fillInsertStatementParams(PreparedStatement insertStatement, T entity, Map<Field, Object> idByField) {
        Field[] insertableFields = getInsertableFields(entity.getClass());
        setParamsFromFields(insertStatement, entity, insertableFields, idByField);
    }

    @SneakyThrows
    private <T> void fillUpdateStatementParams(PreparedStatement updateStatement, T entity, Map<Field, Object> idByField) {
        Field[] updatableFields = getUpdatableFields(entity.getClass());
        setParamsFromFields(updateStatement, entity, updatableFields, idByField);
    }

    @SneakyThrows
    private void setParamsFromFields(PreparedStatement statement, Object entity, Field[] fields, Map<Field, Object> idByField) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            var columnValue = isRegularField(field) ? field.get(entity) : idByField.get(field);
            statement.setObject(i + 1, columnValue);
        }
    }
}
