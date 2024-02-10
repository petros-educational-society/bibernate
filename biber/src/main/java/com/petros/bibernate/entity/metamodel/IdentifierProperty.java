package com.petros.bibernate.entity.metamodel;

import lombok.Builder;

/**
 * @author Maksym Oliinyk
 */
@Builder
public record IdentifierProperty(String columnName) {
}
