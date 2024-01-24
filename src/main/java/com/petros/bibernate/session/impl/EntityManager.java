package com.petros.bibernate.session.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.petros.bibernate.session.util.EntityUtil.getIdField;
import static com.petros.bibernate.session.util.EntityUtil.resolveColumnName;
import static com.petros.bibernate.session.util.EntityUtil.resolveTableName;

@RequiredArgsConstructor
public class EntityManager {

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
            String fieldName = resolveColumnName(field);
            Object fieldValue = resultSet.getObject(fieldName);
            field.set(entity, fieldValue);
        }
        return entity;
    }
}
