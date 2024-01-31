package com.petros.bibernate.datasource.exception;

public class DataSourceConnectionException extends RuntimeException {

    public DataSourceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
