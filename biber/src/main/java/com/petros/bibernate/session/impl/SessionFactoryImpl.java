package com.petros.bibernate.session.impl;

import com.petros.bibernate.context.SimpleClassPathEntityMetamodelScanner;
import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.entity.metamodel.EntityMetamodel;
import com.petros.bibernate.session.Session;
import com.petros.bibernate.session.SessionFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionFactoryImpl
        implements SessionFactory {

    private final Map<Class<?>, EntityMetamodel> entityMetamodels = new ConcurrentHashMap<>();


    private final DataSourceImpl dataSource;
    private final SimpleClassPathEntityMetamodelScanner scanner;

    public SessionFactoryImpl(DataSourceImpl dataSource,
                              SimpleClassPathEntityMetamodelScanner scanner) {
        this.dataSource = dataSource;
        this.scanner = scanner;
        final Map<Class<?>, EntityMetamodel> candidateComponents = this.scanner.findCandidateComponents("com.petros");
        entityMetamodels.putAll(candidateComponents);
    }

    @Override
    public Session openSession() {
        return new SessionImpl(dataSource,
                               entityMetamodels);
    }

}
