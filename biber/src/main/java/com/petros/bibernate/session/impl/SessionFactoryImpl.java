package com.petros.bibernate.session.impl;

import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.session.Session;
import com.petros.bibernate.session.SessionFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {

    private final DataSourceImpl dataSource;
    @Override
    public Session openSession() {
        return new SessionImpl(dataSource);
    }
}
