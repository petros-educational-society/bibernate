package com.petros.bibernate;

import javax.sql.DataSource;

import static com.petros.bibernate.config.AppConfig.initializeDataSource;

public class Bibernate {
    public static void main(String[] args) {
        DataSource dataSource = initializeDataSource();
    }
}
