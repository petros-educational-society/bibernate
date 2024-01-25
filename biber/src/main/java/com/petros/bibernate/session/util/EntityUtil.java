package com.petros.bibernate.session.util;

import com.petros.bibernate.annotation.Column;
import com.petros.bibernate.annotation.Id;
import com.petros.bibernate.annotation.Table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EntityUtil {

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElse(field.getName());
    }

    public static  <T> String resolveTableName(Class<T> entityType) {
        return Optional.ofNullable(entityType.getAnnotation(Table.class))
                .map(Table::value)
                .orElse(entityType.getSimpleName());
    }

    public static <T> Field getIdField(Class<T> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow();
    }
}
