package org.hsbc.db;
// link: jdbc:mysql://localhost:3306/bug_tracker
// my username & pass: root

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnector {
    private static JdbcConnector instance;
    private static final String url;
    private static final String username, password;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Properties props = new Properties();
        Path envFile = Paths.get(".env");

        try (InputStream inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String dbUrl = (String) props.get("DATABASE_URL");
        String dbUsername = (String) props.get("DB_USERNAME"), dbPassword = (String) props.get("DB_PASSWORD");
        url = dbUrl;
        username = dbUsername;
        password = dbPassword;
    }

    public static JdbcConnector getInstance() {
        if (instance == null) {
            return new JdbcConnector();
        }
        return instance;
    }

    public Connection getConnectionObject() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}
