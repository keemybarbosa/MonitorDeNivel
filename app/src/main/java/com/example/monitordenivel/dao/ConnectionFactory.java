package com.example.monitordenivel.dao;

import com.example.monitordenivel.utils.WebServiceConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://" + WebServiceConstants.BASE_DOMAIN + ":5432/measuremonitor?connectTimeout=30000";
    private static final String USER = "postgres";
    private static final String PASSWORD = "m3asur3jk";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection( URL, USER, PASSWORD);
        return conn;
    }
}