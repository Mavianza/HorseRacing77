package view;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import model.User;
import model.Horse;
import utils.UserManager;

public class UpgradePanel extends JPanel implements Displayable {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JLabel coinsLabel;
    private JLabel speedLabel;
    private JLabel staminaLabel;
    private JLabel accelerationLabel;
    private JLabel levelLabel;
    private BufferedImage backgroundImage;
    
    private static final int UPGRADE_COST = 50;
    private static final int UPGRADE_AMOUNT = 10;
    private static final int MAX_STAT = 200;
    
    public UpgradePanel(GameFrame gameFrame, UserManager userManager) {
        this.gameFrame = gameFrame;
        this.userManager = userManager;

        try {
            backgroundImage = ImageIO.read(new File("assets/background.jpg"));
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
        
        setLayout(new BorderLayout());
        
        initComponents();
    }
    
    @Override
    public void refreshDisplay() {
        updateStats();
    }
    
    @Override
    public String getPanelName() {
        return "Upgrade Panel";
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
        
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("UPGRADE YOUR HORSE");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 0));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(50, 20, getWidth() - 100, getHeight() - 40, 20, 20);
                
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(255, 215, 0, 150));
                g2d.drawRoundRect(50, 20, getWidth() - 100, getHeight() - 40, 20, 20);
            }
        };
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        coinsLabel = new JLabel();
        coinsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        coinsLabel.setForeground(new Color(255, 215, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        centerPanel.add(coinsLabel, gbc);

        levelLabel = new JLabel();
        levelLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        levelLabel.setForeground(Color.CYAN);
        gbc.gridy = 1;
        centerPanel.add(levelLabel, gbc);

        JLabel infoLabel = new JLabel("💎 Upgrade Cost: " + UPGRADE_COST + " coins | Max: " + MAX_STAT);
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        infoLabel.setForeground(new Color(255, 255, 150));
        gbc.gridy = 2;
        centerPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3;
        speedLabel = new JLabel();
        speedLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        speedLabel.setForeground(Color.WHITE);
        centerPanel.add(speedLabel, gbc);
        
        gbc.gridx = 1;
        JProgressBar speedBar = new JProgressBar(0, 200);
        speedBar.setPreferredSize(new Dimension(200, 25));
        speedBar.setStringPainted(true);
        speedBar.setName("speedBar");
        centerPanel.add(speedBar, gbc);
        
        gbc.gridx = 2;
        JButton upgradeSpeedBtn = createUpgradeButton("Upgrade Speed");
        upgradeSpeedBtn.addActionListener(e -> upgradeSpeed());
        centerPanel.add(upgradeSpeedBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        staminaLabel = new JLabel();
        staminaLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        staminaLabel.setForeground(Color.WHITE);
        centerPanel.add(staminaLabel, gbc);
        
        gbc.gridx = 1;
        JProgressBar staminaBar = new JProgressBar(0, 200);
        staminaBar.setPreferredSize(new Dimension(200, 25));
        staminaBar.setStringPainted(true);
        staminaBar.setName("staminaBar");
        centerPanel.add(staminaBar, gbc);
        
        gbc.gridx = 2;
        JButton upgradeStaminaBtn = createUpgradeButton("Upgrade Stamina");
        upgradeStaminaBtn.addActionListener(e -> upgradeStamina());
        centerPanel.add(upgradeStaminaBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        accelerationLabel = new JLabel();
        accelerationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        accelerationLabel.setForeground(Color.WHITE);
        centerPanel.add(accelerationLabel, gbc);
        
        gbc.gridx = 1;
        JProgressBar accelerationBar = new JProgressBar(0, 200);
        accelerationBar.setPreferredSize(new Dimension(200, 25));
        accelerationBar.setStringPainted(true);
        accelerationBar.setName("accelerationBar");
        centerPanel.add(accelerationBar, gbc);
        
        gbc.gridx = 2;
        JButton upgradeAccelerationBtn = createUpgradeButton("Upgrade Acceleration");
        upgradeAccelerationBtn.addActionListener(e -> upgradeAcceleration());
        centerPanel.add(upgradeAccelerationBtn, gbc);
        
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomPanel.setOpaque(false);
        
        JButton backButton = createStyledButton("BACK TO MENU", new Color(178, 34, 34));
        backButton.setPreferredSize(new Dimension(200, 45));
        backButton.addActionListener(e -> gameFrame.showPanel("mainMenu"));
        
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JButton createUpgradeButton(String text) {
        return createStyledButton(text, new Color(34, 139, 34));
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public void updateStats() {
        User currentUser = gameFrame.getCurrentUser();
        if (currentUser == null || currentUser.getHorse() == null) return;
        
        Horse horse = currentUser.getHorse();
        
        coinsLabel.setText("💰 Available Coins: " + currentUser.getCoins());
        levelLabel.setText("🐎 " + horse.getName() + " - Level " + horse.getLevel());
        speedLabel.setText("Speed: " + horse.getSpeed());
        staminaLabel.setText("Stamina: " + horse.getStamina());
        accelerationLabel.setText("Acceleration: " + horse.getAcceleration());

        Component[] components = ((JPanel)getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JProgressBar) {
                JProgressBar bar = (JProgressBar) comp;
                if (bar.getName() != null) {
                    if (bar.getName().equals("speedBar")) {
                        bar.setValue(horse.getSpeed());
                    } else if (bar.getName().equals("staminaBar")) {
                        bar.setValue(horse.getStamina());
                    } else if (bar.getName().equals("accelerationBar")) {
                        bar.setValue(horse.getAcceleration());
                    }
                }
            }
        }
    }
    
    private void upgradeSpeed() {
        User currentUser = gameFrame.getCurrentUser();
        Horse horse = currentUser.getHorse();

        if (horse.getSpeed() >= MAX_STAT) {
            JOptionPane.showMessageDialog(this, 
                "Speed level is already at MAX!", 
                "Maximum Level", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentUser.getCoins() >= UPGRADE_COST) {
            if (currentUser.spendCoins(UPGRADE_COST)) {
                int oldLevel = horse.getLevel();

                int newSpeed = Math.min(horse.getSpeed() + UPGRADE_AMOUNT, MAX_STAT);
                horse.setSpeed(newSpeed);
                
                checkLevelUp(horse, oldLevel);
                userManager.updateUser(currentUser);
                updateStats();
                gameFrame.updateMainMenu();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Not enough coins!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void upgradeStamina() {
        User currentUser = gameFrame.getCurrentUser();
        Horse horse = currentUser.getHorse();

        if (horse.getStamina() >= MAX_STAT) {
            JOptionPane.showMessageDialog(this, 
                "Stamina level is already at MAX!", 
                "Maximum Level", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentUser.getCoins() >= UPGRADE_COST) {
            if (currentUser.spendCoins(UPGRADE_COST)) {
                int oldLevel = horse.getLevel();

                int newStamina = Math.min(horse.getStamina() + UPGRADE_AMOUNT, MAX_STAT);
                horse.setStamina(newStamina);
                
                checkLevelUp(horse, oldLevel);
                userManager.updateUser(currentUser);
                updateStats();
                gameFrame.updateMainMenu();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Not enough coins!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void upgradeAcceleration() {
        User currentUser = gameFrame.getCurrentUser();
        Horse horse = currentUser.getHorse();

        if (horse.getAcceleration() >= MAX_STAT) {
            JOptionPane.showMessageDialog(this, 
                "Acceleration level is already at MAX!", 
                "Maximum Level", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentUser.getCoins() >= UPGRADE_COST) {
            if (currentUser.spendCoins(UPGRADE_COST)) {
                int oldLevel = horse.getLevel();

                int newAcceleration = Math.min(horse.getAcceleration() + UPGRADE_AMOUNT, MAX_STAT);
                horse.setAcceleration(newAcceleration);
                
                checkLevelUp(horse, oldLevel);
                userManager.updateUser(currentUser);
                updateStats();
                gameFrame.updateMainMenu();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Not enough coins!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkLevelUp(Horse horse, int oldLevel) {
        int totalStats = horse.getSpeed() + horse.getStamina() + horse.getAcceleration();
        int expectedLevel = (totalStats - 150) / 30 + 1;
        
        if (expectedLevel > horse.getLevel()) {
            horse.levelUp();
            
            JOptionPane.showMessageDialog(this, 
                "🎉 Congratulations! Your horse reached Level " + horse.getLevel() + "!\n" +
                "Keep upgrading to unlock your horse's full potential!", 
                "Level Up!", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
