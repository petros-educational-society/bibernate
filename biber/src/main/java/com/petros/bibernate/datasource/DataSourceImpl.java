package com.petros.bibernate.datasource;

import com.petros.bibernate.connection.ConnectionPool;
import com.petros.bibernate.datasource.exception.DataSourceConnectionException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class DataSourceImpl implements DataSource, AutoCloseable {

    private static final int DEFAULT_POOL_SIZE = 10;

    private final Queue<Connection> connectionPool = new LinkedBlockingQueue<>(DEFAULT_POOL_SIZE);

    public DataSourceImpl(String url, String user, String password) {
        try {
            for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
                Connection connection = DriverManager.getConnection(url, user, password);
                ConnectionPool pooledConnection = new ConnectionPool(connectionPool, connection);
                connectionPool.add(pooledConnection);
            }
        } catch (SQLException e) {
            throw new DataSourceConnectionException("Can't create connection ", e);
        }
    }

    @Override
    public Connection getConnection() {
        return connectionPool.poll();
    }

    @Override
    public Connection getConnection(String username, String password) {
        return getConnection();
    }

    @Override
    public void close() {
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        throw new UnsupportedOperationException();
    }
}
