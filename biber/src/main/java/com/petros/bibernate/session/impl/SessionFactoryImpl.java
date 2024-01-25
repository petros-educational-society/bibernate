package com.petros.bibernate.session.impl;

import com.petros.bibernate.session.Session;
import com.petros.bibernate.session.SessionFactory;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;
    @Override
    public Session openSession() {
        return new SessionImpl(dataSource);
    }
}
