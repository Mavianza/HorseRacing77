package view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
    private BufferedImage trackImage;
    private static final int NUM_COMPETITORS = 5;
    private static final int LANE_HEIGHT = 110;
    private static final int HORSE_WIDTH = 160;
    private static final int HORSE_HEIGHT = 160;
    private static final int TRACK_START_X = 100;
    private static final int RACE_UPDATE_DELAY_MS = 30;
    private static final int ANIMATION_DELAY_MS = 80;
    private int trackLength;
    private List<RaceHorse> horses;
    private List<Thread> raceThreads;
    private List<Integer> laneMappings;
    private List<JLabel> horseLabels;
    private List<JLabel> nameLabels;
    private JLayeredPane trackLayeredPane;
    private JPanel trackBackgroundPanel;
    private JPanel resultPanel;  
    private JLabel[] resultLabels;  
    private JButton startButton;
    private JButton backButton;
    private boolean raceInProgress;
    private List<RaceHorse> finishOrder;
    private boolean useAnimatedGif;
    
    private Timer updateTimer;
    private Timer animationTimer;
    private int currentFrame = 0;
    
    private String[] horseNames = {"Cuki", "Suki", "Oming", "Mujaer", "Jaki"};
    
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
        try {
            trackImage = ImageIO.read(new File("assets/TrackBalapan.png"));
            System.out.println("Track image loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load track image, fallback to manual drawTrack: " + e.getMessage());
            trackImage = null; 
        }
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

                if (trackImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    //gambar track sebagai background, di-scale mengikuti ukuran panel
                    g2d.drawImage(trackImage, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        
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
    
    private String getRankingSuffix(int position) {
        switch (position) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return position + "th";
        }
    }
    
    private void createHorseLabels() {
        clearHorseLabels();
        
        //Langsung ambil dimensi frame
        Dimension frameDim = HorseAssets.getPreRenderedDimensions();
        int frameWidth = frameDim.width;
        int frameHeight = frameDim.height;
        
        //pembuatan label
        for (int i = 0; i < horses.size(); i++) {
            RaceHorse horse = horses.get(i);
            int laneNumber = laneMappings.get(i);

            BufferedImage firstFrame = HorseAssets.getPreRenderedFrame(0);
            
            JLabel horseLabel = new JLabel(new ImageIcon(firstFrame));
            horseLabel.setOpaque(false);
            
            //hitung posisi kuda
            int x = TRACK_START_X + horse.getPosition();
            int laneY = getLaneTop(laneNumber);
            int laneHeight = getLaneHeight();
            int horseY = laneY + (laneHeight - frameHeight) / 2;
            
            horseLabel.setBounds(x, horseY, frameWidth, frameHeight);
            
            horseLabels.add(horseLabel);
            trackLayeredPane.add(horseLabel, JLayeredPane.PALETTE_LAYER);

            String labelText = horse.isPlayer() ? horse.getName() + " (YOU)" : horse.getName();
            JLabel nameLabel = new JLabel(labelText);
            nameLabel.setOpaque(true); 
            
            if (horse.isPlayer()) {
                nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                nameLabel.setForeground(Color.RED);
                nameLabel.setBackground(Color.WHITE);
                nameLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 2),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
            } else {
                nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                nameLabel.setForeground(Color.BLACK);
                nameLabel.setBackground(new Color(255, 255, 255, 200));
                nameLabel.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
            }
            
            //hitung posisi nama
            int nameWidth = nameLabel.getPreferredSize().width;
            int nameHeight = nameLabel.getPreferredSize().height;
            int nameX = x + (frameWidth - nameWidth) / 2;
            int nameY = laneY + 5;
            
            nameLabel.setBounds(nameX, nameY, nameWidth, nameHeight);
            
            nameLabels.add(nameLabel);
            trackLayeredPane.add(nameLabel, JLayeredPane.MODAL_LAYER);
        }
        
        //refresh
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
            int laneY = getLaneTop(laneNumber);
            int laneHeight = getLaneHeight();
            int horseY = laneY + (laneHeight - frameHeight) / 2;
            
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
        
        // horses.add(new RaceHorse(
        //     currentUser.getHorse().getName(),
        //     "",
        //     currentUser.getHorse().calculateRaceSpeed(),
        //     true
        // ));

        // cons rh 2
        horses.add(new RaceHorse(currentUser.getHorse(), true));
        
        // cons rh 2
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
        
        createHorseLabels();
        
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
        
        updateTimer = new Timer(RACE_UPDATE_DELAY_MS, e -> updateHorseLabelPositions());
        updateTimer.setCoalesce(true);
        updateTimer.start();
        
        startAnimationTimer();
        
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

    //susun teks hasil balapan
    private void showRaceResults() {
        User currentUser = gameFrame.getCurrentUser();
        int playerPosition = -1;
        RaceHorse playerHorse = null;
    
        //cari posisi player di finishOrder
        synchronized (finishOrder) {
            for (int i = 0; i < finishOrder.size(); i++) {
                RaceHorse h = finishOrder.get(i);
                if (h.isPlayer()) {
                    playerPosition = i + 1;
                    playerHorse = h;
                    break;
                }
            }
        }
    
        //hitung coins
        int coinsEarned = 0;
        if (playerPosition == 1) {
            coinsEarned = 100;
        } else if (playerPosition == 2) {
            coinsEarned = 50;
        } else if (playerPosition == 3) {
            coinsEarned = 25;
        }
    
        //update koin player + simpan ke DB
        if (currentUser != null) {
            currentUser.addCoins(coinsEarned);
            userManager.updateUser(currentUser);
    
            //simpan history balapan
            RaceHistory history = new RaceHistory(
                    currentUser.getUserId(),
                    currentUser.getHorse().getName(),
                    playerPosition,
                    NUM_COMPETITORS,
                    coinsEarned
            );
            userManager.addRaceHistory(history);
        }

        StringBuilder sb = new StringBuilder();
    
        if (finishOrder.isEmpty()) {
            sb.append("No race results available.");
        } else {
            if (playerHorse != null) {
                sb.append("You finished ")
                  .append(getRankingSuffix(playerPosition))
                  .append("!\nCoins earned: ")
                  .append(coinsEarned)
                  .append("\n\n");
            }
    
            if (playerPosition == 1) {
                sb.append("🏆 CONGRATULATIONS! YOU WON! 🏆\n\n");
            } else if (playerPosition > 0 && playerPosition <= 3) {
                sb.append("🎉 Good race! You placed ")
                  .append(playerPosition)
                  .append("!\n\n");
            } else if (playerPosition > 0) {
                sb.append("Keep practicing!\n\n");
            }
    
            //daftar lengkap hasil
            for (int i = 0; i < finishOrder.size(); i++) {
                RaceHorse h = finishOrder.get(i);
                String rank = getRankingSuffix(i + 1);
    
                sb.append(rank)
                  .append(" - ")
                  .append(h.getName());
    
                if (h.isPlayer()) {
                    sb.append(" (You)");
                }
    
                if (i < finishOrder.size() - 1) {
                    sb.append("\n");
                }
            }
        }
    
        showRaceResultsDialog(sb.toString());
    
        //refresh main menu (label coins dll)
        gameFrame.updateMainMenu();
    }

    private void showRaceResultsDialog(String resultText) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Race Result",
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
        content.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));

        JLabel titleLabel = new JLabel("RACE RESULTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 235, 205));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextPane textPane = createDialogTextPane(
                resultText,
                new Font(Font.SANS_SERIF, Font.PLAIN, 16),
                new Color(255, 235, 205),
                true
        );

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(10));
        center.add(textPane);

        JButton okButton = createDialogButton("CONTINUE", new Color(34, 139, 34));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(okButton);

        content.add(center, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(new Dimension(460, 360));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private int getLaneHeight() {
        int h = trackBackgroundPanel.getHeight();
        if (h <= 0) {
            // fallback kalau belum ter-layout
            return LANE_HEIGHT; // atau 110
        }
        return h / NUM_COMPETITORS;
    }

    //posisi Y (top) dari lane ke-`laneIndex`
    private int getLaneTop(int laneIndex) {
        return getLaneHeight() * laneIndex;
    }

    //helper JTextPane untuk dialog
    private JTextPane createDialogTextPane(String text, Font font, Color color, boolean center) {
        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        pane.setFocusable(false);
        pane.setOpaque(false);
    
        pane.setFont(font);
        pane.setText(text);
    
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
    
        StyleConstants.setAlignment(attrs,
                center ? StyleConstants.ALIGN_CENTER : StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(attrs, color);
        StyleConstants.setFontFamily(attrs, font.getFamily());
        StyleConstants.setFontSize(attrs, font.getSize());
    
        doc.setParagraphAttributes(0, doc.getLength(), attrs, true);
    
        pane.setAlignmentX(Component.CENTER_ALIGNMENT);
        return pane;
    }

    private JButton createDialogButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
                Color base = bgColor;
                if (!isEnabled()) {
                    base = base.darker();
                } else if (getModel().isPressed()) {
                    base = base.darker();
                } else if (getModel().isRollover()) {
                    base = base.brighter();
                }
    
                g2d.setColor(base);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
    
                g2d.setColor(Color.WHITE);
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
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}