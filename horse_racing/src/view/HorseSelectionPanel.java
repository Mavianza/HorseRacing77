package view;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import model.Horse;
import model.User;
import utils.UserManager;

public class HorseSelectionPanel extends JPanel {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JTextField horseNameField;
    private BufferedImage backgroundImage;
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 32);
    private static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font DIALOG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Color CARD_TOP = new Color(101, 67, 33, 220);
    private static final Color CARD_INNER = new Color(120, 81, 45, 220);
    private static final Color ACCENT_COLOR = new Color(255, 215, 0);
    private static final Color DIALOG_BG = new Color(36, 26, 18);
    
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
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(CARD_TOP);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 26, 26);

                g2d.setColor(CARD_INNER);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 20, 20);
            }
        };
        mainContainer.setOpaque(false);
        mainContainer.setPreferredSize(new Dimension(540, 440));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 28, 12, 28);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("SELECT YOUR HORSE");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainContainer.add(titleLabel, gbc);

        JTextPane infoLabel = createCenteredTextPane("Name your champion horse to start racing.", BODY_FONT, new Color(255, 255, 210));
        gbc.gridy = 1;
        gbc.insets = new Insets(6, 28, 6, 28);
        mainContainer.add(infoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(16, 36, 8, 12);
        JLabel nameLabel = new JLabel("Horse Name:");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(Color.WHITE);
        mainContainer.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(16, 12, 8, 36);
        horseNameField = createStyledTextField();
        horseNameField.setPreferredSize(new Dimension(260, 42));
        mainContainer.add(horseNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 28, 12, 28);
        JTextPane statsLabel = createCenteredTextPane("Starting Stats\n\nSpeed: 50 | Stamina: 50 | Acceleration: 50",
            new Font(Font.SANS_SERIF, Font.BOLD, 14), ACCENT_COLOR);
        mainContainer.add(statsLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(24, 28, 20, 28);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        buttonRow.setOpaque(false);

        JButton backButton = createStyledButton("BACK", new Color(178, 34, 34));
        backButton.setPreferredSize(new Dimension(150, 46));
        backButton.addActionListener(e -> gameFrame.showPanel("login"));

        JButton confirmButton = createStyledButton("CREATE HORSE", new Color(34, 139, 34));
        confirmButton.setPreferredSize(new Dimension(180, 46));
        confirmButton.addActionListener(e -> handleConfirm());

        buttonRow.add(backButton);
        buttonRow.add(confirmButton);
        mainContainer.add(buttonRow, gbc);

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
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = baseColor;
                if (!isEnabled()) {
                    g2d.setColor(bgColor.darker().darker());
                } else if (getModel().isPressed()) {
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
    
    // Consistent success dialog after creating a new horse
    private void showSuccessDialog(String horseName) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Horse Created", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);

        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 225, 170),
                    0, getHeight(), new Color(120, 70, 20)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2d.setColor(new Color(40, 20, 10, 160));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            }
        };
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel title = new JLabel("HORSE CREATED", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(new Color(70, 40, 10));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextPane message = createCenteredTextPane(
            "Horse \"" + horseName + "\" is ready to race. Good luck out there!",
            DIALOG_FONT,
            new Color(60, 35, 10)
        );
        message.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(message);

        JButton okButton = createDialogButton("CONTINUE", new Color(34, 139, 34));
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(new Dimension(430, 220));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTextPane createCenteredTextPane(String text, Font font, Color color) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setFont(font);
        pane.setForeground(color);
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
        pane.setCaretPosition(0);
        return pane;
    }

    private JButton createDialogButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = isEnabled() ? baseColor : baseColor.darker();
                if (getModel().isPressed()) {
                    c = c.darker();
                } else if (getModel().isRollover()) {
                    c = c.brighter();
                }
                g2d.setColor(c);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
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
        
        showSuccessDialog(horseName);
        
        gameFrame.showPanel("mainMenu");
    }
}
