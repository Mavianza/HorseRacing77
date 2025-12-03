package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public abstract class BasePanel extends JPanel {

    protected final GameFrame gameFrame;
    private BufferedImage backgroundImage;

    public BasePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        // Layout dasar supaya konten bisa di-center di panel turunan
        setLayout(new GridBagLayout());
        loadBackgroundImage();
    }

    /**
     * Loads the shared background image for all panels.
     */
    protected void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("assets/background.jpg"));
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
            backgroundImage = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback background color if image is missing
            g.setColor(new Color(139, 69, 19));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Child panels must implement this to build their UI. Panggil ini dari
     * constructor subclass setelah field-nya siap.
     */
    protected abstract void initComponents();

    public GameFrame getGameFrame() {
        return gameFrame;
    }
}
