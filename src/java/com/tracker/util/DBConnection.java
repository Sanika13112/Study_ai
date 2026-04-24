package com.tracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Standard port is 3306. Update to 33006 if your installation requires it.
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:33006/study_ai", "root", "Sanika@123");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("JDBC Connection Failed: " + e.getMessage());
        }
        return con;
    }
}