package de.neariyeveryone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class SQLite {
    private Connection connection;
    private Statement statement;
    private final String url;

    protected SQLite(String url) {
        this.url = url;
    }

    public boolean connect() {
        if (connection != null) return false;
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connecting to database");
            statement = connection.createStatement();
            return true;
        } catch (Exception exception) {
            System.err.println("Unable to connect to database");
            System.err.println(exception.getMessage());
            return false;
        }
    }

    public boolean disconnect() {
        if (connection == null) return false;
        try {
            connection.close();
            System.out.println("Disconnecting from database");
            return true;
        } catch (Exception exception) {
            System.err.println("Unable to disconnect from database");
            System.err.println(exception.getMessage());
            return false;
        }
    }

    public void update(String sql) {
        try {
            statement.execute(sql);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public ResultSet query(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            return null;
        }
    }
}
