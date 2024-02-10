package com.petros.bibernate.entity.metamodel;

import com.petros.bibernate.enums.FetchType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Maksym Oliinyk
 */
@Getter
@EqualsAndHashCode
public class OneToManyMetamodel
        implements Value {

    private final String mappedBy;
    private final String referencingTable;
    private final String referencedEntityName;
    private final Class<?> associatedClass;
    private final FetchType fetchType;

    @Builder(toBuilder = true)
    public OneToManyMetamodel(String mappedBy,
                              String referencingTable,
                              String referencedEntityName,
                              Class<?> associatedClass,
                              FetchType fetchType) {
        this.mappedBy = mappedBy;
        this.referencingTable = referencingTable;
        this.referencedEntityName = referencedEntityName;
        this.associatedClass = associatedClass;
        this.fetchType = fetchType;
    }

}
