package model;

public class Horse extends BaseEntity implements Upgradable {
    
    private String name;
    private int speed;
    private int stamina;
    private int acceleration;
    private int level;
    
    public Horse(String name) {
        this.name = name;
        this.speed = 50;
        this.stamina = 50;
        this.acceleration = 50;
        this.level = 1;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getInfo() {
        return String.format("Horse: %s (Level %d) - Speed: %d, Stamina: %d, Acceleration: %d", 
                           name, level, speed, stamina, acceleration);
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public int getStamina() {
        return stamina;
    }
    
    public int getAcceleration() {
        return acceleration;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }
    
    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    @Override
    public void upgradeSpeed(int amount) {
        this.speed += amount;
    }
    
    @Override
    public void upgradeStamina(int amount) {
        this.stamina += amount;
    }
    
    @Override
    public void upgradeAcceleration(int amount) {
        this.acceleration += amount;
    }
    
    @Override
    public void levelUp() {
        this.level++;
    }
    
    public int calculateRaceSpeed() {
        return (speed + acceleration + stamina) / 3;
    }
}
