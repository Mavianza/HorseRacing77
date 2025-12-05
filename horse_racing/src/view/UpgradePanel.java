package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.Horse;
import model.User;
import utils.UserManager;

public class UpgradePanel extends JPanel implements Displayable {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JLabel coinsLabel;
    private JLabel speedLabel;
    private JLabel staminaLabel;
    private JLabel accelerationLabel;
    private JLabel levelLabel;
    private JButton upgradeSpeedBtn;
    private JButton upgradeStaminaBtn;
    private JButton upgradeAccelerationBtn;
    private JProgressBar speedBar;
    private JProgressBar staminaBar;
    private JProgressBar accelerationBar;
    private BufferedImage backgroundImage;
    
    private static final int SPEED_BASE_COST = 70;
    private static final int SPEED_COST_INCREMENT = 15;
    private static final int STAMINA_BASE_COST = 60;
    private static final int STAMINA_COST_INCREMENT = 12;
    private static final int ACCELERATION_BASE_COST = 55;
    private static final int ACCELERATION_COST_INCREMENT = 10;
    private static final int UPGRADE_AMOUNT = 10;
    private static final int MAX_STAT = 200;
    private static final Font DIALOG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Color DIALOG_BG = new Color(36, 26, 18);
    private static final Color DISABLED_BUTTON = new Color(110, 110, 110);
    
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

        JLabel infoLabel = new JLabel("Costs differ per stat and scale up each upgrade (Max: " + MAX_STAT + ")");
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
        speedBar = new JProgressBar(0, 200);
        speedBar.setPreferredSize(new Dimension(200, 25));
        speedBar.setStringPainted(true);
        speedBar.setName("speedBar");
        styleProgressBar(speedBar);
        centerPanel.add(speedBar, gbc);
        
        gbc.gridx = 2;
        upgradeSpeedBtn = createUpgradeButton("Upgrade Speed");
        upgradeSpeedBtn.addActionListener(e -> upgradeSpeed());
        centerPanel.add(wrapButton(upgradeSpeedBtn), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        staminaLabel = new JLabel();
        staminaLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        staminaLabel.setForeground(Color.WHITE);
        centerPanel.add(staminaLabel, gbc);
        
        gbc.gridx = 1;
        staminaBar = new JProgressBar(0, 200);
        staminaBar.setPreferredSize(new Dimension(200, 25));
        staminaBar.setStringPainted(true);
        staminaBar.setName("staminaBar");
        styleProgressBar(staminaBar);
        centerPanel.add(staminaBar, gbc);
        
        gbc.gridx = 2;
        upgradeStaminaBtn = createUpgradeButton("Upgrade Stamina");
        upgradeStaminaBtn.addActionListener(e -> upgradeStamina());
        centerPanel.add(wrapButton(upgradeStaminaBtn), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        accelerationLabel = new JLabel();
        accelerationLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        accelerationLabel.setForeground(Color.WHITE);
        centerPanel.add(accelerationLabel, gbc);
        
        gbc.gridx = 1;
        accelerationBar = new JProgressBar(0, 200);
        accelerationBar.setPreferredSize(new Dimension(200, 25));
        accelerationBar.setStringPainted(true);
        accelerationBar.setName("accelerationBar");
        styleProgressBar(accelerationBar);
        centerPanel.add(accelerationBar, gbc);
        
        gbc.gridx = 2;
        upgradeAccelerationBtn = createUpgradeButton("Upgrade Acceleration");
        upgradeAccelerationBtn.addActionListener(e -> upgradeAcceleration());
        centerPanel.add(wrapButton(upgradeAccelerationBtn), gbc);
        
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

    private JPanel wrapButton(JButton button) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        wrapper.setOpaque(false);
        wrapper.add(button);
        return wrapper;
    }

    private void styleProgressBar(JProgressBar bar) {
        bar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        bar.setForeground(new Color(116, 165, 220));
        bar.setBackground(new Color(32, 32, 32));
        bar.setOpaque(true);
        bar.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210), 1));
        bar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.WHITE;
            }

            @Override
            protected Color getSelectionBackground() {
                return Color.WHITE;
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2d.setColor(DISABLED_BUTTON);
                } else if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(isEnabled() ? getForeground() : new Color(230, 230, 230));
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
    
    private int calculateCost(int baseCost, int increment, int currentStat) {
        int upgradesDone = Math.max(0, (currentStat - 50) / UPGRADE_AMOUNT);
        return baseCost + (upgradesDone * increment);
    }
    
    private int calculateSpeedCost(int currentStat) {
        return calculateCost(SPEED_BASE_COST, SPEED_COST_INCREMENT, currentStat);
    }
    
    private int calculateStaminaCost(int currentStat) {
        return calculateCost(STAMINA_BASE_COST, STAMINA_COST_INCREMENT, currentStat);
    }
    
    private int calculateAccelerationCost(int currentStat) {
        return calculateCost(ACCELERATION_BASE_COST, ACCELERATION_COST_INCREMENT, currentStat);
    }

    private String toPercent(int value) {
        int percent = Math.round((value * 100f) / MAX_STAT);
        return percent + "%";
    }
    
    private void updateUpgradeButton(JButton button, String baseText, int currentStat, int cost) {
        if (currentStat >= MAX_STAT) {
            button.setText("MAXED");
            button.setToolTipText("This stat is already maxed");
            button.setEnabled(false);
        } else {
            button.setText(baseText + "  $" + cost + "");
            button.setToolTipText("Upgrade cost: " + cost + " coins");
            button.setEnabled(true);
        }
    }
    
    public void updateStats() {
        User currentUser = gameFrame.getCurrentUser();
        if (currentUser == null || currentUser.getHorse() == null) return;
        
        Horse horse = currentUser.getHorse();
        
        coinsLabel.setText("Coins: 💰 " + currentUser.getCoins());
        levelLabel.setText("Horse: 🐎 " + horse.getName() + " - Level " + horse.getLevel());
        speedLabel.setText("Speed: " + horse.getSpeed());
        staminaLabel.setText("Stamina: " + horse.getStamina());
        accelerationLabel.setText("Acceleration: " + horse.getAcceleration());

        speedBar.setValue(horse.getSpeed());
        staminaBar.setValue(horse.getStamina());
        accelerationBar.setValue(horse.getAcceleration());
        speedBar.setString(toPercent(horse.getSpeed()));
        staminaBar.setString(toPercent(horse.getStamina()));
        accelerationBar.setString(toPercent(horse.getAcceleration()));

        updateUpgradeButton(upgradeSpeedBtn, "Upgrade Cost :", horse.getSpeed(), calculateSpeedCost(horse.getSpeed()));
        updateUpgradeButton(upgradeStaminaBtn, "Upgrade Cost :", horse.getStamina(), calculateStaminaCost(horse.getStamina()));
        updateUpgradeButton(upgradeAccelerationBtn, "Upgrade Cost :", horse.getAcceleration(), calculateAccelerationCost(horse.getAcceleration()));
    }

    // Styled warning dialog when the player lacks coins for an upgrade (mirrors level-up look)
    // Styled warning dialog when the player lacks coins for an upgrade (mirrors level-up look)
    private void showNotEnoughCoins(String statName, int cost) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Upgrade Failed",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color brown = new Color(92, 57, 28);
                g2d.setColor(brown);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2d.setColor(brown.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        JLabel titleLabel = new JLabel("UPGRADE FAILED", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 235, 205));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ⬇️ pakai helper, font & warna seragam
        JTextPane textPane = createDialogTextPane(
                "You need " + cost + " coins to upgrade " + statName +
                ".\nRace more to earn extra coins.",
                DIALOG_FONT,                              // pakai konstanta di atas
                new Color(255, 235, 205),                // krem
                true                                     // center
        );

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(10));
        center.add(textPane);

        JButton okButton = createStyledButton("CONTINUE", new Color(34, 139, 34));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(okButton);

        content.add(center, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(new Dimension(440, 220));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }



    private JTextPane createDialogTextPane(String text, Font font, Color color, boolean center) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFocusable(false);
        pane.setOpaque(false);

        pane.setFont(font);
        pane.setText(text);

        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();

        // alignment tengah / kiri
        StyleConstants.setAlignment(attrs,
                center ? StyleConstants.ALIGN_CENTER : StyleConstants.ALIGN_LEFT);

        // ⬇️ INI YANG PENTING: warna + font di level dokumen
        StyleConstants.setForeground(attrs, color);
        StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, font.getSize());

        // apply ke seluruh teks
        doc.setParagraphAttributes(0, doc.getLength(), attrs, true);

        // biar benar2 di tengah kalau pakai BoxLayout
        pane.setAlignmentX(Component.CENTER_ALIGNMENT);

        return pane;
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
        
        int cost = calculateSpeedCost(horse.getSpeed());
        if (!currentUser.spendCoins(cost)) {
            showNotEnoughCoins("Speed", cost);
            return;
        }

        int oldLevel = horse.getLevel();
        int newSpeed = Math.min(horse.getSpeed() + UPGRADE_AMOUNT, MAX_STAT);
        horse.setSpeed(newSpeed);
        
        checkLevelUp(horse, oldLevel);
        userManager.updateUser(currentUser);
        updateStats();
        gameFrame.updateMainMenu();
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
        
        int cost = calculateStaminaCost(horse.getStamina());
        if (!currentUser.spendCoins(cost)) {
            showNotEnoughCoins("Stamina", cost);
            return;
        }

        int oldLevel = horse.getLevel();
        int newStamina = Math.min(horse.getStamina() + UPGRADE_AMOUNT, MAX_STAT);
        horse.setStamina(newStamina);
        
        checkLevelUp(horse, oldLevel);
        userManager.updateUser(currentUser);
        updateStats();
        gameFrame.updateMainMenu();
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
        
        int cost = calculateAccelerationCost(horse.getAcceleration());
        if (!currentUser.spendCoins(cost)) {
            showNotEnoughCoins("Acceleration", cost);
            return;
        }

        int oldLevel = horse.getLevel();
        int newAcceleration = Math.min(horse.getAcceleration() + UPGRADE_AMOUNT, MAX_STAT);
        horse.setAcceleration(newAcceleration);
        
        checkLevelUp(horse, oldLevel);
        userManager.updateUser(currentUser);
        updateStats();
        gameFrame.updateMainMenu();
    }
    
    private void checkLevelUp(Horse horse, int oldLevel) {
        int totalStats = horse.getSpeed() + horse.getStamina() + horse.getAcceleration();
        int expectedLevel = (totalStats - 150) / 30 + 1;
        
        if (expectedLevel > horse.getLevel()) {
            horse.levelUp();
            showLevelUpDialog(horse, oldLevel);
        }
    }

    private void showLevelUpDialog(Horse horse, int oldLevel) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Level Up",
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Color brown = new Color(92, 57, 28);
                g2d.setColor(brown);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                g2d.setColor(brown.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 16, 16);
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel title = new JLabel("LEVEL UP!", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(new Color(255, 235, 205));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);   // ⬅️ penting

        JLabel name = new JLabel(horse.getName(), SwingConstants.CENTER);
        name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        name.setForeground(new Color(255, 235, 205));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);    // ⬅️ penting

        JLabel level = new JLabel(
                "Level " + oldLevel + " → Level " + horse.getLevel(),
                SwingConstants.CENTER
        );
        level.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        level.setForeground(new Color(255, 235, 205));
        level.setAlignmentX(Component.CENTER_ALIGNMENT);   // ⬅️ penting

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(title);
        center.add(Box.createVerticalStrut(10));
        center.add(name);
        center.add(Box.createVerticalStrut(8));
        center.add(level);

        JButton ok = createStyledButton("CONTINUE", new Color(34, 139, 34));
        ok.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(ok);

        content.add(center, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(new Dimension(460, 230));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


}
