package com.thm.fblz.main;

import java.sql.*;

/**
 * Created by fabian on 28.04.17.
 */
public class DatabaseModel {

    private Connection con;


    public DatabaseModel(String connectionString, String username, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(connectionString, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Result executeQuery(String query) throws SQLException {
        Statement statement = con.createStatement();
        boolean wasQuery = statement.execute(query);
        if (wasQuery) {
            return new Result(statement.getResultSet());
        } else {
            return new Result(statement.getUpdateCount());
        }
    }
}
