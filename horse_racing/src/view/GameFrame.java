package view;

import javax.swing.*;
import java.awt.*;
import model.User;
import utils.UserManager;

public class GameFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private UserManager userManager;
    private User currentUser;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private HorseSelectionPanel horseSelectionPanel;
    private MainMenuPanel mainMenuPanel;
    private RacePanel racePanel;
    private UpgradePanel upgradePanel;
    private HistoryPanel historyPanel;
    
    public GameFrame() {
        userManager = new UserManager();
        
        setTitle("Horse Racing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        initPanels();
        
        add(mainPanel);
        
        showPanel("login");
    }
    
    private void initPanels() {
        loginPanel = new LoginPanel(this, userManager);
        registerPanel = new RegisterPanel(this, userManager);
        horseSelectionPanel = new HorseSelectionPanel(this, userManager);
        mainMenuPanel = new MainMenuPanel(this);
        racePanel = new RacePanel(this, userManager);
        upgradePanel = new UpgradePanel(this, userManager);
        historyPanel = new HistoryPanel(this, userManager);
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        mainPanel.add(horseSelectionPanel, "horseSelection");
        mainPanel.add(mainMenuPanel, "mainMenu");
        mainPanel.add(racePanel, "race");
        mainPanel.add(upgradePanel, "upgrade");
        mainPanel.add(historyPanel, "history");
    }
    
    public void showPanel(String panelName) {
        if (panelName.equals("mainMenu")) {
            mainMenuPanel.updateUserInfo();
        } else if (panelName.equals("race")) {
            racePanel.initializeRace();
        } else if (panelName.equals("upgrade")) {
            upgradePanel.updateStats();
        } else if (panelName.equals("history")) {
            historyPanel.loadHistory();
        }
        
        cardLayout.show(mainPanel, panelName);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public void updateMainMenu() {
        mainMenuPanel.updateUserInfo();
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
}
