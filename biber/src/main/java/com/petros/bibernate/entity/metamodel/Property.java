package com.petros.bibernate.entity.metamodel;

import lombok.Builder;

/**
 * @author Maksym Oliinyk
 */
@Builder
public record Property(String columnName,
                       boolean isOneToOneEntity,
                       boolean isManyToOneEntity,
                       boolean isOneToManyEntity,
                       boolean isManyToManyEntity,
                       boolean isRegularField,

                       Value value,
                       String[] cascade,
                       boolean updateable,
                       boolean insertable,
                       boolean nullable,
                       boolean unique,
                       boolean optimisticLocked,
                       String propertyAccessorName,
                       boolean lazy,
                       boolean optional,
                       java.util.Map metaAttributes,
                       Class<?> persistentClass,
                       boolean naturalIdentifier) {

}
