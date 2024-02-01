package com.petros.demo;

import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.entity.Address;
import com.petros.bibernate.entity.Order;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;


public class DemoApp {
    public static void main(String[] args) throws SQLException, LiquibaseException {
        try (DataSourceImpl customDataSource = new DataSourceImpl("jdbc:postgresql://localhost:5432/bibernate", "postgres", "root")) {
            try (Connection connection = customDataSource.getConnection()) {
                fillTestData(connection);
                testing(customDataSource);
            }
       }
    }

    private static void testing(DataSourceImpl dataSource) {
        SessionFactory sessionFactory = new SessionFactoryImpl(dataSource);
        Session session = sessionFactory.openSession();
        User user = session.find(User.class, 2);
        Address address = session.find(Address.class, 12);
        System.out.println(user);
        System.out.println(address);
        Address address1 = new Address(13L, "Kiev", "Shevchenka");
        Order order = new Order(4L, "Paper", BigDecimal.valueOf(1.04), address1);
        address1.addOrder(order);
        session.insert(order);
        session.close();
        Order result = session.find(Order.class, 4L);
        System.out.println(result);
    }

    public static void fillTestData(Connection connection) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        Liquibase liquibase = new liquibase.Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
    }
}
