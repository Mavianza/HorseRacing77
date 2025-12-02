package view;

import java.awt.*;
import javax.swing.*;
import utils.UserManager;

/**
 * RegisterPanel handles user account creation.
 * It extends BasePanel to share background and layout
 * with the other main panels.
 */
public class RegisterPanel extends BasePanel implements Displayable {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private final UserManager userManager;

    public RegisterPanel(GameFrame gameFrame, UserManager userManager) {
        super(gameFrame);
        this.userManager = userManager;
        initComponents();
    }

    @Override
    protected void initComponents() {
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.anchor = GridBagConstraints.CENTER;
        rootGbc.fill = GridBagConstraints.NONE;

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(new Color(139, 69, 19, 200));
                g2d.fillRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 20, 20);
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setPreferredSize(new Dimension(450, 480));

        add(contentPanel, rootGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("CREATE NEW ACCOUNT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                Font font = new Font(Font.SANS_SERIF, Font.BOLD, 26);
                g2d.setFont(font);

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 10;

                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawString(getText(), x + 3, y + 3);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 215, 0),
                        0, getHeight(), new Color(218, 165, 32)
                );
                g2d.setPaint(gradient);
                g2d.drawString(getText(), x, y);

                g2d.setColor(new Color(255, 255, 200, 150));
                g2d.drawString(getText(), x - 1, y - 1);
            }
        };
        titleLabel.setPreferredSize(new Dimension(400, 50));
        gbc.gridy = 0;
        contentPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(8, 20, 8, 20);

        // Username
        gbc.gridy = 1;
        contentPanel.add(createStyledLabel("Username:"), gbc);

        gbc.gridy = 2;
        usernameField = createStyledTextField();
        contentPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 3;
        contentPanel.add(createStyledLabel("Password:"), gbc);

        gbc.gridy = 4;
        passwordField = createStyledPasswordField();
        contentPanel.add(passwordField, gbc);

        // Confirm password
        gbc.gridy = 5;
        contentPanel.add(createStyledLabel("Confirm Password:"), gbc);

        gbc.gridy = 6;
        confirmPasswordField = createStyledPasswordField();
        contentPanel.add(confirmPasswordField, gbc);

        // Buttons
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 20, 20, 20);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        registerButton = createStyledButton("Register", new Color(76, 153, 51));
        backButton = createStyledButton("Back", new Color(178, 34, 34));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        contentPanel.add(buttonPanel, gbc);

        // Actions
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> gameFrame.showPanel("login"));

        confirmPasswordField.addActionListener(e -> handleRegister());
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(255, 248, 220));
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(new Color(139, 90, 43));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        field.setPreferredSize(new Dimension(380, 40));
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setColor(new Color(139, 90, 43));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        field.setPreferredSize(new Dimension(380, 40));
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return field;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

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
        button.setPreferredSize(new Dimension(160, 45));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        return button;
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(
                this,
                "Username must be at least 3 characters!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(
                this,
                "Password must be at least 4 characters!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (userManager.register(username, password)) {
            JOptionPane.showMessageDialog(
                this,
                "Registration successful! Please login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            gameFrame.showPanel("login");
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Username already exists!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // --- Displayable implementation ---

    @Override
    public void refreshDisplay() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    @Override
    public String getPanelName() {
        return "register";
    }
}
