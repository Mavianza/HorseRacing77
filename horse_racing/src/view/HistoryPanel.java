package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.List;
import model.RaceHistory;
import utils.UserManager;


public class HistoryPanel extends JPanel implements Displayable {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private static final Font TABLE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private static final Font DIALOG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    private static final Color HEADER_BG = new Color(70, 45, 25);
    private static final Color STRIPE_ONE = new Color(249, 243, 232);
    private static final Color STRIPE_TWO = new Color(240, 233, 220);
    private static final Color TABLE_TEXT = new Color(38, 30, 22);
    private static final Color DIALOG_BG = new Color(36, 26, 18);
    
    public HistoryPanel(GameFrame gameFrame, UserManager userManager) {
        this.gameFrame = gameFrame;
        this.userManager = userManager;
        
        setLayout(new BorderLayout());
        setBackground(new Color(139, 69, 19));
        
        initComponents();
    }
    
    @Override
    public void refreshDisplay() {
        loadHistory();
    }
    
    @Override
    public String getPanelName() {
        return "Race History";
    }
    
    private void initComponents() {
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(101, 67, 33));
        JLabel titleLabel = new JLabel("RACE HISTORY");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Date & Time", "Horse", "Placement", "Total Racers", "Coins"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        styleHistoryTable();

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(247, 241, 231));

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(101, 67, 33));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton backButton = new JButton("BACK TO MENU");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        backButton.setBackground(new Color(178, 34, 34));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> gameFrame.showPanel("mainMenu"));
        
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Apply compact table styling, striping, and alignment for readability
    private void styleHistoryTable() {
        historyTable.setFont(TABLE_FONT);
        historyTable.setRowHeight(34);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setShowGrid(false);
        historyTable.setFillsViewportHeight(true);
        historyTable.setBackground(STRIPE_ONE);
        historyTable.setForeground(TABLE_TEXT);
        historyTable.setSelectionBackground(new Color(255, 215, 128));
        historyTable.setSelectionForeground(Color.BLACK);

        StripedTableCellRenderer centerRenderer = new StripedTableCellRenderer(SwingConstants.CENTER);
        historyTable.setDefaultRenderer(Object.class, centerRenderer);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JTableHeader header = historyTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(HEADER_FONT);
                label.setOpaque(true);
                label.setBackground(HEADER_BG);
                label.setForeground(Color.WHITE);
                label.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
                return label;
            }
        });
    }

    // Renderer that aligns cells and alternates row background colors
    private class StripedTableCellRenderer extends DefaultTableCellRenderer {
        private final int alignment;

        StripedTableCellRenderer(int alignment) {
            this.alignment = alignment;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(alignment);
            setFont(TABLE_FONT);
            if (isSelected) {
                setBackground(new Color(255, 215, 128));
                setForeground(Color.BLACK);
            } else {
                setBackground((row % 2 == 0) ? STRIPE_ONE : STRIPE_TWO);
                setForeground(TABLE_TEXT);
            }
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return this;
        }
    }

    // Friendly info dialog used for empty history and other soft notices
    private void showFriendlyInfoDialog(String title, String message) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, Dialog.ModalityType.APPLICATION_MODAL);
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

        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 40, 10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextPane textPane = createDialogTextPane(message, true);
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.setForeground(new Color(60, 35, 10));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(textPane);

        JButton okButton = createDialogButton("OK", new Color(34, 139, 34));
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(new Dimension(420, 200));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JTextPane createDialogTextPane(String text, boolean center) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setFont(DIALOG_FONT);
        pane.setForeground(Color.WHITE);
        pane.setText(text);
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs, center ? StyleConstants.ALIGN_CENTER : StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
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
        button.setPreferredSize(new Dimension(120, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public void loadHistory() {
        tableModel.setRowCount(0);
        
        if (gameFrame.getCurrentUser() != null) {
            int userId = gameFrame.getCurrentUser().getUserId();
            List<RaceHistory> histories = userManager.getUserHistory(userId);

            for (RaceHistory history : histories) {
                Object[] row = {
                    history.getTimestamp(),
                    history.getHorseName(),
                    history.getPosition(),
                    history.getTotalHorses(),
                    history.getCoinsEarned()
                };
                tableModel.addRow(row);
            }
            
            if (histories.isEmpty()) {
                showFriendlyInfoDialog(
                    "No Race History",
                    "No race history yet. Play your first race to see it here!"
                );
            }
        }
    }
}
