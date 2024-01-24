package com.petros.bibernate;

import com.petros.bibernate.entity.Address;
import com.petros.bibernate.entity.User;
import com.petros.bibernate.session.Session;
import com.petros.bibernate.session.SessionFactory;
import com.petros.bibernate.session.impl.SessionFactoryImpl;
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
        DataSource dataSource = initializeDataSource();

        fillTestData(dataSource);
        testing(dataSource);
    }

    private static void testing(DataSource dataSource) {
        SessionFactory sessionFactory = new SessionFactoryImpl(dataSource);
        Session session = sessionFactory.openSession();
        User user = session.find(User.class, 2);
        Address address = session.find(Address.class, 12);
        System.out.println(user);
        System.out.println(address);
    }

    public static void fillTestData(DataSource dataSource) throws SQLException, LiquibaseException {
        java.sql.Connection connection = dataSource.getConnection();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
