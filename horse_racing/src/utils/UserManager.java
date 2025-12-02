package utils;

import model.User;
import model.Horse;
import model.RaceHistory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class UserManager {
    
    public UserManager() {
        DatabaseConnection.initialize();
    }
    
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    public boolean register(String username, String password) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = "INSERT INTO users (username, password, salt, coins) VALUES (?, ?, ?, 500)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                return false;
            }
            
            // Generate salt sama hash password
            String salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, salt);
                insertStmt.executeUpdate();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public User login(String username, String password) {
        String userSql = "SELECT id, username, password, salt, coins FROM users WHERE username = ?";
        String horseSql = "SELECT name, speed, stamina, acceleration, level FROM horses WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql)) {
            
            userStmt.setString(1, username);
            ResultSet rs = userStmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String uname = rs.getString("username");
                String storedHash = rs.getString("password");
                String salt = rs.getString("salt");
                int coins = rs.getInt("coins");
                

                String inputHash = hashPassword(password, salt);
                if (!inputHash.equals(storedHash)) {
                    return null;
                }
                
                User user = new User(uname, storedHash);
                user.setCoins(coins);
                user.setUserId(userId);
                
                try (PreparedStatement horseStmt = conn.prepareStatement(horseSql)) {
                    horseStmt.setInt(1, userId);
                    ResultSet horseRs = horseStmt.executeQuery();
                    
                    if (horseRs.next()) {
                        String horseName = horseRs.getString("name");
                        int speed = horseRs.getInt("speed");
                        int stamina = horseRs.getInt("stamina");
                        int acceleration = horseRs.getInt("acceleration");
                        int level = horseRs.getInt("level");
                        
                        Horse horse = new Horse(horseName);
                        horse.setSpeed(speed);
                        horse.setStamina(stamina);
                        horse.setAcceleration(acceleration);
                        horse.setLevel(level);
                        
                        user.setHorse(horse);
                    }
                }
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void updateUser(User user) {
        String updateUserSql = "UPDATE users SET coins = ? WHERE id = ?";
        String updateHorseSql = "UPDATE horses SET speed = ?, stamina = ?, acceleration = ?, level = ? WHERE user_id = ?";
        String insertHorseSql = "INSERT INTO horses (user_id, name, speed, stamina, acceleration, level) VALUES (?, ?, ?, ?, ?, ?)";
        String checkHorseSql = "SELECT id FROM horses WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateUserStmt = conn.prepareStatement(updateUserSql)) {
            
            updateUserStmt.setInt(1, user.getCoins());
            updateUserStmt.setInt(2, user.getUserId());
            updateUserStmt.executeUpdate();
            
            if (user.getHorse() != null) {
                Horse horse = user.getHorse();
                
                try (PreparedStatement checkStmt = conn.prepareStatement(checkHorseSql)) {
                    checkStmt.setInt(1, user.getUserId());
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (rs.next()) {
                        try (PreparedStatement updateHorseStmt = conn.prepareStatement(updateHorseSql)) {
                            updateHorseStmt.setInt(1, horse.getSpeed());
                            updateHorseStmt.setInt(2, horse.getStamina());
                            updateHorseStmt.setInt(3, horse.getAcceleration());
                            updateHorseStmt.setInt(4, horse.getLevel());
                            updateHorseStmt.setInt(5, user.getUserId());
                            updateHorseStmt.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement insertHorseStmt = conn.prepareStatement(insertHorseSql)) {
                            insertHorseStmt.setInt(1, user.getUserId());
                            insertHorseStmt.setString(2, horse.getName());
                            insertHorseStmt.setInt(3, horse.getSpeed());
                            insertHorseStmt.setInt(4, horse.getStamina());
                            insertHorseStmt.setInt(5, horse.getAcceleration());
                            insertHorseStmt.setInt(6, horse.getLevel());
                            insertHorseStmt.executeUpdate();
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void addRaceHistory(RaceHistory history) {
        String sql = "INSERT INTO race_history (user_id, horse_name, position, total_horses, coins_earned) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, history.getUserId());
            stmt.setString(2, history.getHorseName());
            stmt.setInt(3, history.getPosition());
            stmt.setInt(4, history.getTotalHorses());
            stmt.setInt(5, history.getCoinsEarned());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error adding race history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public List<RaceHistory> getUserHistory(int userId) {
        List<RaceHistory> historyList = new ArrayList<>();
        String sql = "SELECT user_id, horse_name, position, total_horses, coins_earned, race_date FROM race_history WHERE user_id = ? ORDER BY race_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String horseName = rs.getString("horse_name");
                int position = rs.getInt("position");
                int totalHorses = rs.getInt("total_horses");
                int coinsEarned = rs.getInt("coins_earned");
                Timestamp raceDate = rs.getTimestamp("race_date");
                
                RaceHistory history = new RaceHistory(userId, horseName, position, totalHorses, coinsEarned);
                history.setTimestamp(raceDate);
                historyList.add(history);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading race history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historyList;
    }
}
