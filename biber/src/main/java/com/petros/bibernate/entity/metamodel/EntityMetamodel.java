package com.petros.bibernate.entity.metamodel;

import lombok.Builder;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.List;

/**
 * @author Maksym Oliinyk
 */
@Builder
public record EntityMetamodel(Class<?> introspectedClass,
                              @NonNull Constructor<?> noArgsConstructor,
                              String tableName,
                              IdentifierProperty identifierProperty,
                              List<Property> properties) {
}
