package com.petros.bibernate.config;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class AppConfig {

    public static DataSource initializeDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/bibernate");
        dataSource.setUser("postgres");
        dataSource.setPassword("root");
        return dataSource;
    }
}
