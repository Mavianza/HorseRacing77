package models;

public interface Upgradable {

    void upgradeSpeed(int amount);
    
    void upgradeStamina(int amount);
    
    void upgradeAcceleration(int amount);

    void levelUp();

    int getLevel();
}
