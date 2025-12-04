package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.RaceHistory;
import utils.UserManager;


public class HistoryPanel extends JPanel implements Displayable {
    private GameFrame gameFrame;
    private UserManager userManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
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

        String[] columnNames = {"Date & Time", "Horse", "Position", "Total Horses", "Coins Earned"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        historyTable.getTableHeader().setBackground(new Color(101, 67, 33));
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.setSelectionBackground(new Color(255, 200, 100));
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(101, 67, 33));
        
        JButton refreshButton = new JButton("REFRESH");
        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadHistory());
        
        JButton backButton = new JButton("BACK TO MENU");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        backButton.setBackground(new Color(178, 34, 34));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> gameFrame.showPanel("mainMenu"));
        
        bottomPanel.add(refreshButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
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
                JOptionPane.showMessageDialog(this, 
                    "No race history yet. Start racing to build your history!", 
                    "No History", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
