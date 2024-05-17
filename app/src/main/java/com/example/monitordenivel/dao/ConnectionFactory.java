package com.example.monitordenivel.dao;

import com.example.monitordenivel.utils.WebServiceConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://" + WebServiceConstants.BASE_DOMAIN + ":5432/measuremonitor";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection( URL, USER, PASSWORD);
    }
}