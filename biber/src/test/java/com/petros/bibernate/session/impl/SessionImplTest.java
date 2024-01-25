package com.petros.bibernate.session.impl;

import com.petros.bibernate.config.H2DbConfig;
import com.petros.bibernate.entity.Address;
import com.petros.bibernate.entity.User;
import com.petros.bibernate.session.Session;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionImplTest {

    private static final String DB_URL = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:data/BB-02_Create_test_tables.sql';";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";

    private Connection connection;

    private DataSource dataSource;
    private Session session;

    @BeforeAll
    public void init() throws SQLException {
        dataSource = H2DbConfig.initializeDataSource();
        session = new SessionImpl(dataSource);
        //here the data is added to H2
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Test
    void findExistingUser() {
        User user = session.find(User.class, 1);
        assertEquals("Ken", user.getName());
        assertEquals("ken@gmail.com", user.getEmail());
    }

    @Test
    void findExistingAddress() {
        Address address = session.find(Address.class, 11);
        assertEquals("Odessa", address.getCity());
        assertEquals("R.Luksemburg", address.getStreet());
    }

    @Test
    void findNonExistingUser() {
        Exception exception = assertThrows(JdbcSQLNonTransientException.class,
                () -> session.find(User.class, 1000));

        assertEquals("No data is available [2000-224]", exception.getMessage());
    }

    @Test
    void findNonExistingAddress() {
        Exception exception = assertThrows(JdbcSQLNonTransientException.class,
                () -> session.find(Address.class, 1000));

        assertEquals("No data is available [2000-224]", exception.getMessage());
    }

}