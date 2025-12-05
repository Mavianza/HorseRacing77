package model;

public class User extends BaseEntity {
    
    private int userId;
    private String username;
    private String password;
    private Horse horse;
    private int coins;
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.coins = 500;
        this.horse = null;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @Override
    public String getName() {
        return username;
    }
    
    @Override
    public String getInfo() {
        return String.format("User: %s - Coins: %d, Horse: %s", 
                           username, coins, horse != null ? horse.getName() : "None");
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public Horse getHorse() {
        return horse;
    }
    
    public void setHorse(Horse horse) {
        this.horse = horse;
    }
    
    public int getCoins() {
        return coins;
    }
    
    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    public void addCoins(int amount) {
        this.coins += amount;
    }
    
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
}
