package com.ssg.jdbcex.todo.dao;



import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectTests {
    @Test
    public void test(){
        int v1 = 10;
        int v2 = 20;

        Assertions.assertNotEquals(v1, v2);
    }

    @Test
    public void test2() throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/ssgdb?serverTimezone=UTC";
        String user = "root";
        String password = "hd4628";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        Assertions.assertNotNull(connection);
        connection.close();
    }

    @Test
    public void test3() throws SQLException, ClassNotFoundException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/ssgdb?serverTimezone=UTC");
        hikariConfig.setUsername("root");
        hikariConfig.setPassword("hd4628");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        //커넥션 풀
        try(HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        Connection connection = hikariDataSource.getConnection()) {
            Assertions.assertNotNull(connection);
            System.out.println(connection);
        }
    }
}
