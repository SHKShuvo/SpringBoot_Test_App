package com.example.test.Common;

import oracle.jdbc.pool.OracleDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DBConnection {

    public static Properties config;

    String logStr = null;
    SimpleDateFormat ft4 = null;
    Date myDate = null;

    public DBConnection() {
        this.loadConfig();
        ft4 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        myDate = new Date();
    }

    public String loadConfig() {
        config = new Properties();
        loadPropertiesFile("application.properties", config);
        loadPropertiesFile("config.properties", config);
        return "";
    }

    private void loadPropertiesFile(String filename, Properties properties) {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/" + filename)) {
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load " + filename, e);
        }
    }

    public Connection getAuthDBConnection() {
        try {
            if (config == null || config.isEmpty()) {
                loadConfig();
            }

            String jdbcUrl = getValue("AUTH_DB_URL", "app.auth-db.url", null);
            String userid = getValue("AUTH_DB_USERNAME", "app.auth-db.username", null);
            String password = getValue("AUTH_DB_PASSWORD", "app.auth-db.password", null);

            if (isBlank(jdbcUrl)) {
                String host = getValue("CREDENTIAL_HOST", "credentialHost", "localhost");
                String port = getValue("CRED_DB_PORT", "credDbPort", "1521");
                String alias = getValue("CRED_DB_ALIAS", "credDbAlias", "orcl");
                jdbcUrl = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + alias;
            }

            if (isBlank(userid)) {
                userid = getValue("CRED_DB_USER", "credDbUser", null);
            }
            if (isBlank(password)) {
                password = getValue("CRED_DB_PASSWORD", "credDbPassword", null);
            }

            if (isBlank(userid) || isBlank(password)) {
                throw new IllegalStateException("Oracle DB username/password is missing. Set AUTH_DB_USERNAME and AUTH_DB_PASSWORD, or configure app.auth-db.username and app.auth-db.password.");
            }

            OracleDataSource dataSource = new OracleDataSource();
            dataSource.setURL(resolvePlaceholders(jdbcUrl));
            return dataSource.getConnection(resolvePlaceholders(userid), resolvePlaceholders(password));
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to Oracle database. Check URL, username, password, VPN/network, and Oracle listener/service name.", e);
        }
    }

    private String getValue(String envName, String propertyName, String defaultValue) {
        String envValue = System.getenv(envName);
        if (!isBlank(envValue)) {
            return envValue.trim();
        }

        String systemPropertyValue = System.getProperty(propertyName);
        if (!isBlank(systemPropertyValue)) {
            return systemPropertyValue.trim();
        }

        String propertyValue = config == null ? null : config.getProperty(propertyName);
        if (!isBlank(propertyValue)) {
            return propertyValue.trim();
        }

        return defaultValue;
    }

    private String resolvePlaceholders(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("${spring.datasource.url}", getValue("ORACLE_DB_URL", "spring.datasource.url", "jdbc:oracle:thin:@//localhost:1521/orcl"))
                .replace("${spring.datasource.username}", getValue("ORACLE_DB_USERNAME", "spring.datasource.username", "shuvo"))
                .replace("${spring.datasource.password}", getValue("ORACLE_DB_PASSWORD", "spring.datasource.password", "shuvo"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
