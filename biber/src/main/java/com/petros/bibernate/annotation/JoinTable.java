package com.petros.bibernate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {

    /**
     * (Optional) The name of the join table.
     *
     */
    String value();

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity owning the
     * association. (I.e. the owning side of
     * the association).
     *
     */
    JoinColumn[] joinColumns();

    /**
     * (Optional) The foreign key columns
     * of the join table which reference the
     * primary table of the entity that does
     * not own the association. (I.e. the
     * inverse side of the association).
     *
     */
    JoinColumn[] inverseJoinColumns();
}
