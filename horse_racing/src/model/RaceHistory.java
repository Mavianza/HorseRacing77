package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RaceHistory extends BaseEntity {
    
    private int userId;
    private String horseName;
    private int position;
    private int totalHorses;
    private int coinsEarned;
    private String timestamp;
    
    public RaceHistory(int userId, String horseName, int position, int totalHorses, int coinsEarned) {
        this.userId = userId;
        this.horseName = horseName;
        this.position = position;
        this.totalHorses = totalHorses;
        this.coinsEarned = coinsEarned;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    
    @Override
    public String getName() {
        return horseName;
    }
    
    @Override
    public String getInfo() {
        return toString();
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getHorseName() {
        return horseName;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int getTotalHorses() {
        return totalHorses;
    }
    
    public int getCoinsEarned() {
        return coinsEarned;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp ts) {
        this.timestamp = ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("[%s] Position %d/%d - Earned: %d coins", 
                           timestamp, position, totalHorses, coinsEarned);
    }
}
