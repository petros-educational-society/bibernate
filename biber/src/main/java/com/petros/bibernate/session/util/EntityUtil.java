package com.petros.bibernate.session.util;

import com.petros.bibernate.annotation.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EntityUtil {

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .filter(StringUtils::isNotBlank)
                .orElseGet(
                        () -> Optional.ofNullable(field.getAnnotation(JoinColumn.class))
                        .map(JoinColumn::value)
                                .orElse(field.getName()));
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

    public static Object getId(Object entity) {
        var entityType = entity.getClass();
        var idField = getIdField(entityType);
        idField.setAccessible(true);
        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing ID field for entity: " + entityType.getSimpleName(), e);
        }
    }

    public static <T> Field getRelatedEntityField(Class<T> fromEntity, Class<?> toEntity) {
        return Arrays.stream(toEntity.getDeclaredFields())
                .filter(f -> f.getType().equals(fromEntity))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find related field in %s for %s".formatted(toEntity, fromEntity)));
    }

    public static <T> Field getMappedByRelatedEntityField(Class<T> relatedEntityType, Field mappedField) {
        return Arrays.stream(relatedEntityType.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(JoinTable.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find mappedBy field in %s for %s".formatted(relatedEntityType, mappedField)));
    }

    public static boolean isRegularField(Field field) {
        return !isManyToOneEntityField(field) && !isOneToManyEntityField(field)
                && !isOneToOneEntityField(field) && !isManyToManyEntityField(field);
    }

    public static boolean isOneToOneEntityField(Field field) {
        return field.isAnnotationPresent(OneToOne.class);
    }

    public static boolean isManyToOneEntityField(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isOneToManyEntityField(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public static boolean isManyToManyEntityField(Field field) {
        return field.isAnnotationPresent(ManyToMany.class);
    }
}
