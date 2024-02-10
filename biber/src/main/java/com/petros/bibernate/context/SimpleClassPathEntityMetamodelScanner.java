package com.petros.bibernate.context;

import com.petros.bibernate.annotation.Entity;
import com.petros.bibernate.entity.metamodel.EntityMetamodel;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maksym Oliinyk
 */
public class SimpleClassPathEntityMetamodelScanner {

    public Map<Class<?>, EntityMetamodel> findCandidateComponents(String... basePackages) {
        final Map<Class<?>, EntityMetamodel> entityMetamodels = new ConcurrentHashMap<>();
        for (String basePackage : basePackages) {
            Reflections scanner = new Reflections(basePackage);
            final Set<Class<?>> entities = scanner.getTypesAnnotatedWith(Entity.class);
            entities.forEach(entity -> entityMetamodels.put(entity,
                                                            new EntityMetamodelReader().readEntityMetamodel(entity)));
        }
        return entityMetamodels;
    }

}
