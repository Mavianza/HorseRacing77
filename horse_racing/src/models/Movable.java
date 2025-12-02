package models;

public interface Movable {
    void move();
    
    int getPosition();
    
    void setPosition(int position);
    
    boolean isFinished();
    
    void setFinished(boolean finished);
}
