package org.example.functions.logic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {

    private final static HikariConfig config = new HikariConfig();
    private final static HikariDataSource ds;

    static {
        //config.setDataSourceClassName("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
        String connectionUrl = "jdbc:sqlserver://sql-abhi.database.windows.net:1433;" +
                "database=sql-abhi;" +
                "user=abhishek@sql-abhi;" +
                "password=Abhi@1234;" +
                "encrypt=true;" +
                "trustServerCertificate=false;" +
                "hostNameInCertificate=*.database.windows.net;" +
                "loginTimeout=30;";
        config.setJdbcUrl(connectionUrl);
        ds = new HikariDataSource( config );
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
