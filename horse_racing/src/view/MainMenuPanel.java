package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.Horse;
import model.User;

public class MainMenuPanel extends JPanel implements Displayable {
    private GameFrame gameFrame;
    private JLabel userInfoLabel;
    private JLabel horseInfoLabel;
    private JLabel coinsLabel;
    private BufferedImage backgroundImage;
    private static final Font DIALOG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Color DIALOG_BG = new Color(36, 26, 18);
    private static final Color ACCENT_COLOR = new Color(255, 215, 0);
    
    public MainMenuPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        
        setLayout(new GridBagLayout());

        try {
            backgroundImage = ImageIO.read(new File("assets/background.jpg"));
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }
        
        initComponents();
    }
    
    @Override
    public void refreshDisplay() {
        updateUserInfo();
    }
    
    @Override
    public String getPanelName() {
        return "Main Menu";
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
    
    private void initComponents() {
        
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(101, 67, 33));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(120, 81, 45));
                g2d.fillRoundRect(15, 15, getWidth()-30, getHeight()-30, 20, 20);
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setPreferredSize(new Dimension(500, 600));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("HORSE RACING GAME") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Font font = new Font(Font.SANS_SERIF, Font.BOLD, 32);
                g2d.setFont(font);
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 10;

                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), x + 3, y + 3);

                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 215, 0),
                                                          0, getHeight(), new Color(218, 165, 32));
                g2d.setPaint(gradient);
                g2d.drawString(getText(), x, y);

                g2d.setColor(new Color(255, 255, 200, 150));
                g2d.drawString(getText(), x - 1, y - 1);
            }
        };
        titleLabel.setPreferredSize(new Dimension(450, 50));
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 15, 20);
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        userInfoLabel.setForeground(new Color(255, 235, 205));
        userInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        horseInfoLabel = new JLabel();
        horseInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        horseInfoLabel.setForeground(new Color(255, 215, 0));
        horseInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        coinsLabel = new JLabel();
        coinsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        coinsLabel.setForeground(new Color(255, 215, 0));
        coinsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        userInfoPanel.add(userInfoLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(horseInfoLabel);
        userInfoPanel.add(Box.createVerticalStrut(5));
        userInfoPanel.add(coinsLabel);
        contentPanel.add(userInfoPanel, gbc);
        
        gbc.insets = new Insets(8, 20, 8, 20);

        gbc.gridy = 2;
        JButton raceButton = createStyledButton("START RACE", new Color(220, 20, 60));
        raceButton.addActionListener(e -> gameFrame.showPanel("race"));
        contentPanel.add(raceButton, gbc);

        gbc.gridy = 3;
        JButton upgradeButton = createStyledButton("UPGRADE HORSE", new Color(255, 140, 0));
        upgradeButton.addActionListener(e -> gameFrame.showPanel("upgrade"));
        contentPanel.add(upgradeButton, gbc);

        gbc.gridy = 4;
        JButton historyButton = createStyledButton("RACE HISTORY", new Color(30, 144, 255));
        historyButton.addActionListener(e -> gameFrame.showPanel("history"));
        contentPanel.add(historyButton, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(8, 20, 20, 20);
        JButton logoutButton = createStyledButton("LOGOUT", new Color(178, 34, 34));
        logoutButton.addActionListener(e -> {
            int result = showStyledConfirmDialog(
                "Confirm Logout",
                "Are you sure you want to logout?",
                "Logout",
                "Cancel"
            );
            
            if (result == JOptionPane.YES_OPTION) {
                gameFrame.setCurrentUser(null);
                gameFrame.showPanel("login");
            }
        });
        contentPanel.add(logoutButton, gbc);

        GridBagConstraints mainGbc = new GridBagConstraints();
        add(contentPanel, mainGbc);
    }
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(baseColor.brighter());
                } else {
                    g2d.setColor(baseColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                super.paintComponent(g);
            }
        };
        button.setPreferredSize(new Dimension(380, 50));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        
        return button;
    }
    
    public void updateUserInfo() {
        User user = gameFrame.getCurrentUser();
        if (user != null) {
            userInfoLabel.setText("Player: " + user.getUsername());
            coinsLabel.setText("💰 Coins: " + user.getCoins());
            
            Horse horse = user.getHorse();
            if (horse != null) {
                horseInfoLabel.setText("🐎 " + horse.getName() + " (Lvl " + horse.getLevel() + ")");
            }
        }
    }

    private int showStyledConfirmDialog(String title, String message, String okLabel, String cancelLabel) {
        final int[] result = {JOptionPane.CLOSED_OPTION};
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // === background cokelat gelap (#5C391C) ===
                Color brown = new Color(92, 57, 28);
                g2d.setColor(brown);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // border sedikit lebih gelap
                g2d.setColor(brown.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        // === judul dialog ===
        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        // krem, sama vibe dengan "Player: oming"
        titleLabel.setForeground(new Color(255, 235, 205));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === isi pesan ===
        JTextPane textPane = createDialogTextPane(
            message,
            DIALOG_FONT,
            new Color(255, 235, 205),  // teks krem juga
            true
        );
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(textPane);

        JButton okButton = createDialogButton(okLabel, new Color(178, 34, 34));
        okButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });
        JButton cancelButton = createDialogButton(cancelLabel, new Color(128, 128, 128));
        cancelButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);

        // ⬇️ ukuran dialog tetap seperti versi lama
        dialog.pack();
        dialog.setSize(new Dimension(420, 210));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }


    private JButton createDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled() ? bgColor : bgColor.darker();
                if (getModel().isPressed()) {
                    base = base.darker();
                } else if (getModel().isRollover()) {
                    base = base.brighter();
                }
                g2d.setColor(base);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JTextPane createDialogTextPane(String text, Font font, Color color, boolean center) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setFont(font);
        pane.setForeground(color);
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, center ? StyleConstants.ALIGN_CENTER : StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
        return pane;
    }
}
