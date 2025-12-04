package view;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import model.Horse;
import model.User;
import utils.UserManager;

public class HorseSelectionPanel extends JPanel {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JTextField horseNameField;
    private BufferedImage backgroundImage;
    
    public HorseSelectionPanel(GameFrame gameFrame, UserManager userManager) {
        this.gameFrame = gameFrame;
        this.userManager = userManager;

        try {
            backgroundImage = ImageIO.read(new File("assets/background.jpg"));
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
        
        setLayout(new GridBagLayout());
        
        initComponents();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
    
    private void initComponents() {
        
        JPanel mainContainer = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setStroke(new BasicStroke(3));
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 215, 0, 200),
                    getWidth(), getHeight(), new Color(218, 165, 32, 200)
                );
                g2d.setPaint(gradient);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setPreferredSize(new Dimension(500, 400));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SELECT YOUR HORSE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.BLACK);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent();
                
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2d.drawString(getText(), x + dx, y + dy);
                        }
                    }
                }

                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 215, 0),
                    0, getHeight(), new Color(255, 185, 0)
                );
                g2d.setPaint(gradient);
                g2d.drawString(getText(), x, y);
            }
        };
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(400, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainContainer.add(titleLabel, gbc);

        JLabel infoLabel = new JLabel("<html><center>🐎 Name your champion horse!</center></html>");
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        infoLabel.setForeground(new Color(255, 255, 150));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 40, 20, 40);
        mainContainer.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 40, 10, 10);
        JLabel nameLabel = new JLabel("Horse Name:");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);
        mainContainer.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 10, 40);
        horseNameField = createStyledTextField();
        horseNameField.setPreferredSize(new Dimension(250, 40));
        mainContainer.add(horseNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 40, 20, 40);
        JLabel statsLabel = new JLabel("<html><center>⚡ Starting Stats ⚡<br><br>Speed: 50 | Stamina: 50 | Acceleration: 50</center></html>");
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        statsLabel.setForeground(new Color(255, 215, 0));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainContainer.add(statsLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 80, 30, 80);
        JButton confirmButton = createStyledButton("✓ CREATE HORSE");
        confirmButton.setPreferredSize(new Dimension(280, 50));
        confirmButton.addActionListener(e -> handleConfirm());
        mainContainer.add(confirmButton, gbc);

        add(mainContainer);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(101, 67, 33));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(new Color(139, 90, 43));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                super.paintComponent(g);
            }
        };
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return field;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = new Color(34, 139, 34);
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void handleConfirm() {
        String horseName = horseNameField.getText().trim();
        
        if (horseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a horse name!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (horseName.length() < 2) {
            JOptionPane.showMessageDialog(this, 
                "Horse name must be at least 2 characters!", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User currentUser = gameFrame.getCurrentUser();
        Horse horse = new Horse(horseName);
        currentUser.setHorse(horse);
        userManager.updateUser(currentUser);
        
        JOptionPane.showMessageDialog(this, 
            "🎉 Horse created successfully!\n\n" +
            "Name: " + horseName + "\n\n" +
            "Good luck in your races!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        gameFrame.showPanel("mainMenu");
    }
}
