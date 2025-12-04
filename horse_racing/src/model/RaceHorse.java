package model;

public class RaceHorse implements Movable {
    private String name;
    private String color;
    private int speed;
    private int position;
    private boolean isPlayer;
    private boolean finished;
    
    public RaceHorse(String name, String color, int speed, boolean isPlayer) {
        this.name = name;
        this.color = color;
        this.speed = speed;
        this.position = 0;
        this.isPlayer = isPlayer;
        this.finished = false;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    @Override
    public int getPosition() {
        return position;
    }
    
    @Override
    public void setPosition(int position) {
        this.position = position;
    }
    
    @Override
    public void move() {
        int movement = (int) (Math.random() * (speed / 10 + 1)) + 1;
        position += movement;
    }
    
    public boolean isPlayer() {
        return isPlayer;
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }
    
    @Override
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
