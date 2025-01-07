package com.OnlineConsultancyApp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "jdbc:mysql://localhost:3306/consultancy";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static PreparedStatement SQLConnection(String sqls) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        String sql = sqls;
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps;
    }

}
