package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public abstract class BasePanel extends JPanel {
    protected GameFrame gameFrame;
    protected BufferedImage backgroundImage;

    public BasePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new GridBagLayout());
        loadBackgroundImage();
    }
    
    protected void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("assets/background.jpg"));
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(139, 69, 19));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    protected abstract void initComponents();
    
    public GameFrame getGameFrame() {
        return gameFrame;
    }
}
