package utils;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "horse_racing_db";
    private static final String USER = "root";
    private static final String PASS = "";
    
    private static volatile boolean databaseReady = false;
    
    public static Connection getConnection() {
        try {
            ensureDatabaseReady();
            return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Gagal koneksi ke database!");
        }
    }
    
    private static synchronized void ensureDatabaseReady() throws SQLException {
        if (databaseReady) {
            return;
        }
        
        try (Connection initialConn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement s = initialConn.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            databaseReady = true;
        }
    }
    
    public static void initialize() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            
            s.executeUpdate(
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INT(11) NOT NULL AUTO_INCREMENT," +
                "username VARCHAR(50) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "salt VARCHAR(255) NOT NULL," +       
                "coins INT DEFAULT 500," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)" +
            ");"
        );
            
            s.executeUpdate("CREATE TABLE IF NOT EXISTS horses (" +
                    "id INT(11) NOT NULL AUTO_INCREMENT," +
                    "user_id INT(11) NOT NULL," +
                    "name VARCHAR(50) NOT NULL," +
                    "color VARCHAR(20) NOT NULL," +
                    "speed INT DEFAULT 50," +
                    "stamina INT DEFAULT 50," +
                    "acceleration INT DEFAULT 50," +
                    "level INT DEFAULT 1," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");");
            
            s.executeUpdate("CREATE TABLE IF NOT EXISTS race_history (" +
                    "id INT(11) NOT NULL AUTO_INCREMENT," +
                    "user_id INT(11) NOT NULL," +
                    "horse_name VARCHAR(50) NOT NULL," +
                    "position INT NOT NULL," +
                    "total_horses INT NOT NULL," +
                    "coins_earned INT NOT NULL," +
                    "race_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");");
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database tables!");
            e.printStackTrace();
        }
    }
    
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

