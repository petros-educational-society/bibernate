package com.petros.bibernate.config;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

public class H2DbConfig {

    public static DataSource initializeDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }
}
