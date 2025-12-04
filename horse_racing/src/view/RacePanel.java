package view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import model.RaceHistory;
import model.RaceHorse;
import model.User;
import utils.HorseAssets;
import utils.SoundPlayer;
import utils.UserManager;

public class RacePanel extends JPanel {
    private GameFrame gameFrame;
    private UserManager userManager;
    private SoundPlayer soundPlayer = new SoundPlayer();
    private static final int NUM_COMPETITORS = 5;
    private static final int LANE_HEIGHT = 110;
    private static final int HORSE_WIDTH = 80;
    private static final int HORSE_HEIGHT = 80;
    private static final int TRACK_START_X = 100;
    private static final int TRACK_TOP_MARGIN = 15;
    private static final int RACE_UPDATE_DELAY_MS = 30;
    private static final int ANIMATION_DELAY_MS = 80;
    
    private int trackLength = 600;
    
    private List<RaceHorse> horses;
    private List<Thread> raceThreads;
    private List<Integer> laneMappings;
    private List<JLabel> horseLabels;
    private List<JLabel> nameLabels;
    private JLayeredPane trackLayeredPane;
    private JPanel trackBackgroundPanel;
    private JPanel resultPanel;  // Panel untuk hasil finish
    private JLabel[] resultLabels;  // Label untuk setiap posisi
    private JButton startButton;
    private JButton backButton;
    private boolean raceInProgress;
    private List<RaceHorse> finishOrder;
    private boolean useAnimatedGif;
    
    private Timer updateTimer;
    private Timer animationTimer;
    private int currentFrame = 0;
    
    private String[] horseNames = {"Thunder", "Lightning", "Storm", "Blaze", "Shadow"};
    
    public RacePanel(GameFrame gameFrame, UserManager userManager) {
        this.gameFrame = gameFrame;
        this.userManager = userManager;
        this.horses = new ArrayList<>();
        this.raceThreads = new ArrayList<>();
        this.laneMappings = new ArrayList<>();
        this.finishOrder = new ArrayList<>();
        this.horseLabels = new ArrayList<>();
        this.nameLabels = new ArrayList<>();
        this.raceInProgress = false;
        this.useAnimatedGif = HorseAssets.hasAnimatedGif();
        
        setLayout(new BorderLayout());
        setBackground(new Color(139, 69, 19));
        if (useAnimatedGif) {
            System.out.println("Initializing pre-rendered frames...");
            boolean success = HorseAssets.preRenderFrames(HORSE_WIDTH, HORSE_HEIGHT);
            if (success) {
                System.out.println("✓ Pre-rendering complete!");
            } else {
                System.err.println("✗ Pre-rendering failed, falling back to static images");
                useAnimatedGif = false;
            }
        }
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(101, 67, 33));
        JLabel titleLabel = new JLabel("HORSE RACE");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(139, 69, 19));
        
        trackLayeredPane = new JLayeredPane();
        
        trackBackgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTrack(g);
                if (!useAnimatedGif) {
                    drawHorses(g);
                }
            }
        };
        trackBackgroundPanel.setBackground(new Color(210, 180, 140));
        trackBackgroundPanel.setOpaque(true);
        
        trackLayeredPane.add(trackBackgroundPanel, JLayeredPane.DEFAULT_LAYER);
        
        trackLayeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateTrackSize();
            }
        });
        
        centerPanel.add(trackLayeredPane, BorderLayout.CENTER);
        
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(101, 67, 33));
        resultPanel.setPreferredSize(new Dimension(140, NUM_COMPETITORS * LANE_HEIGHT + 50));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel resultTitle = new JLabel("FINISH");
        resultTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        resultTitle.setForeground(Color.WHITE);
        resultTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultPanel.add(resultTitle);
        resultPanel.add(Box.createVerticalStrut(10));
        
        resultLabels = new JLabel[NUM_COMPETITORS];
        for (int i = 0; i < NUM_COMPETITORS; i++) {
            resultLabels[i] = new JLabel("-");
            resultLabels[i].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            resultLabels[i].setForeground(Color.LIGHT_GRAY);
            resultLabels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            resultLabels[i].setOpaque(true);
            resultLabels[i].setBackground(new Color(80, 50, 30));
            resultLabels[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 40, 20), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            resultLabels[i].setMaximumSize(new Dimension(110, 35));
            resultLabels[i].setPreferredSize(new Dimension(110, 35));
            resultLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
            resultPanel.add(resultLabels[i]);
            resultPanel.add(Box.createVerticalStrut(5));
        }
        
        centerPanel.add(resultPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(101, 67, 33));
        
        startButton = new JButton("START RACE");
        startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startRace());
        
        backButton = new JButton("BACK TO MENU");
        backButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        backButton.setBackground(new Color(178, 34, 34));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            if (!raceInProgress) {
                gameFrame.showPanel("mainMenu");
            }
        });
        
        bottomPanel.add(startButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void updateTrackSize() {
        int panelWidth = trackLayeredPane.getWidth();
        int panelHeight = trackLayeredPane.getHeight();
        
        if (panelWidth > 0 && panelHeight > 0) {
            trackLength = panelWidth - TRACK_START_X - 100;
            if (trackLength < 400) trackLength = 400;
            
            trackBackgroundPanel.setBounds(0, 0, panelWidth, panelHeight);
            
            if (useAnimatedGif && !horseLabels.isEmpty()) {
                updateHorseLabelPositions();
            }
            
            trackBackgroundPanel.repaint();
        }
    }
    
    private void updateResultPanel() {
        SwingUtilities.invokeLater(() -> {
            synchronized (finishOrder) {
                for (int i = 0; i < finishOrder.size() && i < resultLabels.length; i++) {
                    RaceHorse horse = finishOrder.get(i);
                    String rankText = getRankingSuffix(i + 1);
                    String displayText = rankText + " " + horse.getName();
                    if (horse.isPlayer()) {
                        displayText += " ★";
                    }
                    
                    resultLabels[i].setText(displayText);
                    
                    if (i == 0) {
                        resultLabels[i].setBackground(new Color(255, 215, 0));
                        resultLabels[i].setForeground(Color.BLACK);
                    } else if (i == 1) {
                        resultLabels[i].setBackground(new Color(192, 192, 192));
                        resultLabels[i].setForeground(Color.BLACK);
                    } else if (i == 2) {
                        resultLabels[i].setBackground(new Color(205, 127, 50));
                        resultLabels[i].setForeground(Color.WHITE);
                    } else {
                        resultLabels[i].setBackground(new Color(100, 100, 100));
                        resultLabels[i].setForeground(Color.WHITE);
                    }
                }
            }
        });
    }
    
    private void resetResultPanel() {
        for (int i = 0; i < resultLabels.length; i++) {
            resultLabels[i].setText("-");
            resultLabels[i].setBackground(new Color(80, 50, 30));
            resultLabels[i].setForeground(Color.LIGHT_GRAY);
        }
    }
    
    private void drawTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int trackHeight = NUM_COMPETITORS * LANE_HEIGHT;
        int finishX = TRACK_START_X + trackLength;
        for (int i = 0; i < NUM_COMPETITORS; i++) {
            int y = TRACK_TOP_MARGIN + i * LANE_HEIGHT;
            if (i % 2 == 0) {
                g2d.setColor(new Color(222, 194, 154));
            } else {
                g2d.setColor(new Color(200, 170, 130));
            }
            g2d.fillRect(TRACK_START_X, y, trackLength + 20, LANE_HEIGHT);
        }
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i <= NUM_COMPETITORS; i++) {
            int y = TRACK_TOP_MARGIN + i * LANE_HEIGHT;
            g2d.drawLine(TRACK_START_X, y, finishX + 20, y);
        }
        
        g2d.setColor(new Color(0, 180, 0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(TRACK_START_X, TRACK_TOP_MARGIN, TRACK_START_X, TRACK_TOP_MARGIN + trackHeight);
        
        int checkerSize = LANE_HEIGHT / 4;
        for (int row = 0; row < (trackHeight / checkerSize); row++) {
            for (int col = 0; col < 2; col++) {
                if ((row + col) % 2 == 0) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.fillRect(finishX + col * checkerSize, TRACK_TOP_MARGIN + row * checkerSize, 
                            checkerSize, checkerSize);
            }
        }
        
        g2d.setColor(new Color(80, 50, 20));
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        for (int i = 0; i < NUM_COMPETITORS; i++) {
            int laneY = TRACK_TOP_MARGIN + i * LANE_HEIGHT + LANE_HEIGHT / 2 + 5;
            g2d.drawString("Lane " + (i + 1), 10, laneY);
        }
    }
    
    private String getRankingSuffix(int position) {
        switch (position) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return position + "th";
        }
    }
    
    private void drawHorses(Graphics g) {
        if (horses.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        
        for (int i = 0; i < horses.size(); i++) {
            RaceHorse horse = horses.get(i);
            int x = TRACK_START_X + horse.getPosition();
            int laneNumber = laneMappings.get(i);
            int laneY = TRACK_TOP_MARGIN + laneNumber * LANE_HEIGHT;
            int horseY = laneY + (LANE_HEIGHT - HORSE_HEIGHT) / 2;
            
            BufferedImage horseImage = HorseAssets.createHorseImage(HORSE_WIDTH, HORSE_HEIGHT);
            g2d.drawImage(horseImage, x, horseY, null);
            
            String name = horse.getName();
            FontMetrics fm = g2d.getFontMetrics();
            int nameWidth = fm.stringWidth(name);
            int nameX = x + (HORSE_WIDTH - nameWidth) / 2;
            int nameY = laneY + 12;
            
            g2d.setColor(new Color(255, 255, 255, 180));
            g2d.fillRoundRect(nameX - 3, nameY - 10, nameWidth + 6, 14, 4, 4);
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(name, nameX, nameY);
            
            if (horse.isPlayer()) {
                g2d.setColor(new Color(255, 200, 0));
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                int youX = x + (HORSE_WIDTH / 2) - 15;
                int youY = laneY + LANE_HEIGHT - 5;
                g2d.drawString("★ YOU", youX, youY);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            }
        }
    }
    
    private void createHorseLabels() {
        clearHorseLabels();
        
        if (!useAnimatedGif || horses.isEmpty()) return;
        
        if (!HorseAssets.hasPreRenderedFrames()) {
            System.err.println("No pre-rendered frames available!");
            useAnimatedGif = false;
            return;
        }
        
        Dimension frameDim = HorseAssets.getPreRenderedDimensions();
        int frameWidth = frameDim.width;
        int frameHeight = frameDim.height;
        
        System.out.println("Creating horse labels with frame size: " + frameWidth + "x" + frameHeight);
        
        for (int i = 0; i < horses.size(); i++) {
            RaceHorse horse = horses.get(i);
            int laneNumber = laneMappings.get(i);
            
            BufferedImage firstFrame = HorseAssets.getPreRenderedFrame(0);
            if (firstFrame == null) {
                System.err.println("Failed to get first frame for horse " + i);
                continue;
            }
            
            JLabel horseLabel = new JLabel(new ImageIcon(firstFrame));
            horseLabel.setOpaque(false);
            
            int x = TRACK_START_X + horse.getPosition();
            int laneY = TRACK_TOP_MARGIN + laneNumber * LANE_HEIGHT;
            int horseY = laneY + (LANE_HEIGHT - frameHeight) / 2;
            horseLabel.setBounds(x, horseY, frameWidth, frameHeight);
            
            horseLabels.add(horseLabel);
            trackLayeredPane.add(horseLabel, JLayeredPane.PALETTE_LAYER);
            
            String labelText = horse.isPlayer() ? horse.getName() + " ★" : horse.getName();
            JLabel nameLabel = new JLabel(labelText);
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            nameLabel.setForeground(horse.isPlayer() ? new Color(180, 120, 0) : Color.BLACK);
            nameLabel.setOpaque(true);
            nameLabel.setBackground(new Color(255, 255, 255, 200));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
            
            int nameWidth = nameLabel.getPreferredSize().width;
            int nameHeight = nameLabel.getPreferredSize().height;
            int nameX = x + (frameWidth - nameWidth) / 2;
            int nameY = laneY + 5;
            nameLabel.setBounds(nameX, nameY, nameWidth, nameHeight);
            
            nameLabels.add(nameLabel);
            trackLayeredPane.add(nameLabel, JLayeredPane.MODAL_LAYER);
        }
        
        trackLayeredPane.revalidate();
        trackLayeredPane.repaint();
    }
    
    private void clearHorseLabels() {
        stopAnimationTimer();
        
        for (JLabel label : horseLabels) {
            trackLayeredPane.remove(label);
        }
        for (JLabel label : nameLabels) {
            trackLayeredPane.remove(label);
        }
        horseLabels.clear();
        nameLabels.clear();
        trackLayeredPane.revalidate();
        trackLayeredPane.repaint();
    }
    
    private void updateHorseLabelPositions() {
        if (!useAnimatedGif || horseLabels.isEmpty()) return;
        
        Dimension frameDim = HorseAssets.getPreRenderedDimensions();
        int frameWidth = frameDim.width;
        int frameHeight = frameDim.height;
        
        for (int i = 0; i < horses.size() && i < horseLabels.size(); i++) {
            RaceHorse horse = horses.get(i);
            JLabel horseLabel = horseLabels.get(i);
            JLabel nameLabel = nameLabels.get(i);
            int laneNumber = laneMappings.get(i);
            
            int x = TRACK_START_X + horse.getPosition();
            int laneY = TRACK_TOP_MARGIN + laneNumber * LANE_HEIGHT;
            int horseY = laneY + (LANE_HEIGHT - frameHeight) / 2;
            
            horseLabel.setLocation(x, horseY);
            
            int nameWidth = nameLabel.getWidth();
            int nameX = x + (frameWidth - nameWidth) / 2;
            int nameY = laneY + 5;
            nameLabel.setLocation(nameX, nameY);
        }
    }
    
    private void updateAnimationFrames() {
        if (!useAnimatedGif || horseLabels.isEmpty()) return;
        
        int totalFrames = HorseAssets.getPreRenderedFrameCount();
        if (totalFrames == 0) return;
        
        currentFrame = (currentFrame + 1) % totalFrames;
        BufferedImage frame = HorseAssets.getPreRenderedFrame(currentFrame);
        
        if (frame != null) {
            for (JLabel horseLabel : horseLabels) {
                horseLabel.setIcon(new ImageIcon(frame));
            }
        }
    }
    
    private void startAnimationTimer() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        if (!HorseAssets.hasPreRenderedFrames()) {
            return;
        }
        
        currentFrame = 0;
        
        animationTimer = new Timer(ANIMATION_DELAY_MS, e -> updateAnimationFrames());
        animationTimer.start();
        System.out.println("Animation timer started (FPS: " + (1000.0 / ANIMATION_DELAY_MS) + ")");
    }
    
    private void stopAnimationTimer() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
            System.out.println("Animation timer stopped");
        }
    }
    
    public void initializeRace() {
        horses.clear();
        raceThreads.clear();
        finishOrder.clear();
        laneMappings.clear();
        
        resetResultPanel();
        
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        
        stopAnimationTimer();
        
        User currentUser = gameFrame.getCurrentUser();
        
        horses.add(new RaceHorse(
            currentUser.getHorse().getName(),
            "",
            currentUser.getHorse().calculateRaceSpeed(),
            true
        ));
        
        for (int i = 1; i < NUM_COMPETITORS; i++) {
            int randomSpeed = 40 + (int)(Math.random() * 40);
            horses.add(new RaceHorse(
                horseNames[i],
                "",
                randomSpeed,
                false
            ));
        }
        
        for (int i = 0; i < NUM_COMPETITORS; i++) {
            laneMappings.add(i);
        }
        Collections.shuffle(laneMappings);
        
        if (useAnimatedGif) {
            createHorseLabels();
        }
        
        trackBackgroundPanel.repaint();
    }
    
    private void startRace() {
        if (raceInProgress) return;
        
        initializeRace();
        raceInProgress = true;
        soundPlayer.playLoop("assets/backsound.wav");
        startButton.setEnabled(false);
        backButton.setEnabled(false);
        finishOrder.clear();
        resetResultPanel();
        
        updateTimer = new Timer(RACE_UPDATE_DELAY_MS, e -> {
            if (useAnimatedGif) {
                updateHorseLabelPositions();
            } else {
                trackBackgroundPanel.repaint();
            }
        });
        updateTimer.setCoalesce(true);
        updateTimer.start();
        
        if (useAnimatedGif) {
            startAnimationTimer();
        }
        
        for (RaceHorse horse : horses) {
            Thread raceThread = new Thread(() -> {
                while (horse.getPosition() < trackLength && raceInProgress) {
                    horse.move();
                    
                    if (horse.getPosition() >= trackLength && !horse.isFinished()) {
                        horse.setFinished(true);
                        synchronized (finishOrder) {
                            finishOrder.add(horse);
                        }
                        updateResultPanel();
                    }
                    
                    try {
                        Thread.sleep(RACE_UPDATE_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            
            raceThreads.add(raceThread);
            raceThread.start();
        }
        
        new Thread(() -> {
            for (Thread thread : raceThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                if (updateTimer != null) {
                    updateTimer.stop();
                }
                stopAnimationTimer();
                raceInProgress = false;
                soundPlayer.stop();
                showRaceResults();
                startButton.setEnabled(true);
                backButton.setEnabled(true);
            });
        }).start();
    }
    
    private void showRaceResults() {
        User currentUser = gameFrame.getCurrentUser();
        int playerPosition = -1;
        
        for (int i = 0; i < finishOrder.size(); i++) {
            if (finishOrder.get(i).isPlayer()) {
                playerPosition = i + 1;
                break;
            }
        }
        
        int coinsEarned = 0;
        if (playerPosition == 1) {
            coinsEarned = 100;
        } else if (playerPosition == 2) {
            coinsEarned = 50;
        } else if (playerPosition == 3) {
            coinsEarned = 25;
        }
        
        currentUser.addCoins(coinsEarned);
        userManager.updateUser(currentUser);
        
        RaceHistory history = new RaceHistory(
            currentUser.getUserId(),
            currentUser.getHorse().getName(),
            playerPosition,
            NUM_COMPETITORS,
            coinsEarned
        );
        userManager.addRaceHistory(history);
        
        StringBuilder results = new StringBuilder();
        results.append("RACE RESULTS\n\n");
        for (int i = 0; i < finishOrder.size(); i++) {
            RaceHorse horse = finishOrder.get(i);
            results.append(String.format("%d. %s %s\n", 
                i + 1, 
                horse.getName(),
                horse.isPlayer() ? "(YOU)" : ""));
        }
        results.append("\n");
        
        if (playerPosition == 1) {
            results.append("🏆 CONGRATULATIONS! YOU WON! 🏆\n");
        } else if (playerPosition <= 3) {
            results.append("🎉 Good race! You placed " + playerPosition + "!\n");
        } else {
            results.append("Keep practicing!\n");
        }
        
        results.append("Coins earned: " + coinsEarned);
        
        JOptionPane.showMessageDialog(this, results.toString(), 
            "Race Finished", JOptionPane.INFORMATION_MESSAGE);
        
        gameFrame.updateMainMenu();
    }
}