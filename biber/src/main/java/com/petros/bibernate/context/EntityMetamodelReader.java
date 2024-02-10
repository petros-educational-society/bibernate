package com.petros.bibernate.context;

import com.petros.bibernate.annotation.Column;
import com.petros.bibernate.entity.metamodel.EntityMetamodel;
import com.petros.bibernate.entity.metamodel.IdentifierProperty;
import com.petros.bibernate.entity.metamodel.Property;
import com.petros.bibernate.session.util.EntityUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.petros.bibernate.session.util.EntityUtil.getIdField;
import static com.petros.bibernate.session.util.EntityUtil.isManyToManyEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isManyToOneEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isOneToManyEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isOneToOneEntityField;
import static com.petros.bibernate.session.util.EntityUtil.isRegularField;

/**
 * @author Maksym Oliinyk
 */
public class EntityMetamodelReader {

    public EntityMetamodel readEntityMetamodel(Class<?> entity) {

        final Constructor<?> noArgsConstructor = getNoArgsConstructor(entity);

        final IdentifierProperty identifierProperty
                = IdentifierProperty.builder()
                                    .columnName(EntityUtil.resolveColumnName(getIdField(entity)))
                                    .build();

        final Field[] declaredFields = entity.getDeclaredFields();
        final List<Property> properties = Arrays.stream(declaredFields)
                                                .filter(field -> field.isAnnotationPresent(Column.class))
                                                .map(field -> {
                                                    field.setAccessible(true);
                                                    final Property.PropertyBuilder builder = Property.builder();

                                                    final boolean oneToOneEntityField = isOneToOneEntityField(field);
                                                    final boolean manyToOneEntityField = isManyToOneEntityField(field);
                                                    final boolean oneToManyEntityField = isOneToManyEntityField(field);
                                                    final boolean manyToManyEntityField = isManyToManyEntityField(field);
                                                    final boolean regularField = isRegularField(field);

                                                    Optional.ofNullable(field.getAnnotation(Column.class))
                                                            .ifPresent(columnAnnotation -> builder.insertable(columnAnnotation.insertable())
                                                                                                  .updateable(columnAnnotation.updatable())
                                                                                                  .nullable(columnAnnotation.nullable())
                                                                                                  .unique(columnAnnotation.unique()));

                                                    return builder.isOneToOneEntity(oneToOneEntityField)
                                                                  .isManyToOneEntity(manyToOneEntityField)
                                                                  .isOneToManyEntity(oneToManyEntityField)
                                                                  .isManyToManyEntity(manyToManyEntityField)
                                                                  .isRegularField(regularField)
                                                                  .columnName(EntityUtil.resolveColumnName(field))
                                                                  .build();
                                                })
                                                .toList();

        return EntityMetamodel.builder()
                              .introspectedClass(entity)
                              .noArgsConstructor(noArgsConstructor)
                              .tableName(EntityUtil.resolveTableName(entity))
                              .identifierProperty(identifierProperty)
                              .properties(properties)
                              .build();
    }

    private Constructor<?> getNoArgsConstructor(final Class<?> entity) {
        return Arrays.stream(entity.getDeclaredConstructors())
                     .filter(s -> s.getParameterCount() == 0)
                     .findFirst()
                     .orElseThrow();
    }

}
