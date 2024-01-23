package com.petros.bibernate;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.petros.bibernate.config.AppConfig.initializeDataSource;

public class Bibernate {
    public static void main(String[] args) throws LiquibaseException, SQLException {

        fillTestData();
    }

    public static void fillTestData() throws SQLException, LiquibaseException {
        DataSource dataSource = initializeDataSource();
        java.sql.Connection connection = dataSource.getConnection();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
