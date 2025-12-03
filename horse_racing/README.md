# 📚 DOKUMENTASI KONSEP OOP - HORSE RACING 77:v

---

## 📁 STRUKTUR PROJECT

```
src/
├── App.java                    # Main class (entry point)
├── model/                      # Package untuk model/entity
│   ├── BaseEntity.java         # ⭐ Abstract Class
│   ├── Upgradable.java         # ⭐ Interface
│   ├── Movable.java            # ⭐ Interface
│   ├── Horse.java              # Inheritance + Interface Implementation
│   ├── RaceHorse.java          # Interface Implementation
│   ├── User.java               # Inheritance
│   └── RaceHistory.java        # Inheritance
├── view/                       # Package untuk GUI
│   ├── BasePanel.java          # ⭐ Abstract Class
│   ├── Displayable.java        # ⭐ Interface
│   ├── GameFrame.java          # ⭐ GUI (JFrame)
│   ├── LoginPanel.java         # GUI (JPanel)
│   ├── RegisterPanel.java      # GUI (JPanel)
│   ├── MainMenuPanel.java      # GUI + Interface Implementation
│   ├── HorseSelectionPanel.java# GUI (JPanel)
│   ├── RacePanel.java          # ⭐ GUI + Thread
│   ├── UpgradePanel.java       # GUI + Interface Implementation
│   └── HistoryPanel.java       # GUI + Interface Implementation
└── utils/                      # Package untuk utility
    ├── DatabaseConnection.java # Koneksi database
    ├── UserManager.java        # Manajemen user
    └── HorseAssets.java        # Asset management
```

---

## 1️⃣ ABSTRACT CLASS

### 📄 File: `src/model/BaseEntity.java`

```java
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Abstract method - HARUS diimplementasi oleh subclass
    public abstract String getName();
    public abstract String getInfo();
}
```

**Penjelasan:**

- `abstract class` = class yang tidak bisa di-instantiate langsung
- `abstract method` = method tanpa body, WAJIB di-override oleh subclass
- Digunakan sebagai **blueprint** untuk class `Horse`, `User`, dan `RaceHistory`

### 📄 File: `src/view/BasePanel.java`

```java
public abstract class BasePanel extends JPanel {
    protected GameFrame gameFrame;
    protected BufferedImage backgroundImage;

    // Concrete method (sudah ada implementasi)
    protected void loadBackgroundImage() { ... }

    // Abstract method - HARUS diimplementasi oleh subclass
    protected abstract void initComponents();
}
```

**Penjelasan:**

- Kombinasi **concrete method** dan **abstract method**
- Concrete method: `loadBackgroundImage()`, `paintComponent()` - sudah ada implementasinya
- Abstract method: `initComponents()` - harus di-override

---

## 2️⃣ INTERFACE

### 📄 File: `src/model/Upgradable.java`

```java
public interface Upgradable {
    void upgradeSpeed(int amount);
    void upgradeStamina(int amount);
    void upgradeAcceleration(int amount);
    void levelUp();
    int getLevel();
}
```

**Penjelasan:**

- Interface = kontrak yang HARUS dipenuhi oleh class yang mengimplementasikannya
- Semua method di interface secara default adalah `public abstract`
- Diimplementasikan oleh class `Horse`

### 📄 File: `src/model/Movable.java`

```java
public interface Movable {
    void move();
    int getPosition();
    void setPosition(int position);
    boolean isFinished();
    void setFinished(boolean finished);
}
```

**Penjelasan:**

- Interface untuk objek yang bisa bergerak
- Diimplementasikan oleh class `RaceHorse`

### 📄 File: `src/view/Displayable.java`

```java
public interface Displayable {
    void refreshDisplay();
    String getPanelName();
}
```

**Penjelasan:**

- Interface untuk panel yang bisa di-refresh
- Diimplementasikan oleh `MainMenuPanel`, `UpgradePanel`, `HistoryPanel`

---

## 3️⃣ INHERITANCE (Pewarisan)

### 📄 File: `src/model/Horse.java`

```java
public class Horse extends BaseEntity implements Upgradable {
    // Mewarisi dari BaseEntity
    // Mengimplementasi interface Upgradable

    @Override
    public String getName() { return name; }  // Override abstract method

    @Override
    public String getInfo() { ... }  // Override abstract method

    @Override
    public void upgradeSpeed(int amount) { ... }  // Implementasi interface
}
```

**Diagram Inheritance:**

```
        BaseEntity (abstract)
        /     |      \
       /      |       \
    Horse    User   RaceHistory
      |
      | implements
      v
   Upgradable (interface)
```

### 📄 File: `src/model/User.java`

```java
public class User extends BaseEntity {
    @Override
    public String getName() { return username; }

    @Override
    public String getInfo() { ... }
}
```

### 📄 File: `src/model/RaceHistory.java`

```java
public class RaceHistory extends BaseEntity {
    @Override
    public String getName() { return horseName; }

    @Override
    public String getInfo() { return toString(); }
}
```

### 📄 File: `src/model/RaceHorse.java`

```java
public class RaceHorse implements Movable {
    @Override
    public void move() { ... }

    @Override
    public int getPosition() { return position; }

    @Override
    public boolean isFinished() { return finished; }
}
```

---

## 4️⃣ POLYMORPHISM

### A. Method Overriding

Polymorphism melalui **override method** dari parent class/interface.

📄 **Contoh di `Horse.java`:**

```java
@Override
public String getName() {
    return name;  // Implementasi spesifik untuk Horse
}

@Override
public String getInfo() {
    return String.format("Horse: %s (Level %d) - Speed: %d, Stamina: %d, Acceleration: %d",
                       name, level, speed, stamina, acceleration);
}
```

📄 **Contoh di `User.java`:**

```java
@Override
public String getName() {
    return username;  // Implementasi berbeda - return username
}

@Override
public String getInfo() {
    return String.format("User: %s - Coins: %d, Horse: %s",
                       username, coins, horse != null ? horse.getName() : "None");
}
```

### B. Polymorphism dengan Interface

📄 **Contoh penggunaan polymorphism:**

```java
// Bisa menggunakan tipe interface sebagai reference
Movable movableObject = new RaceHorse("Thunder", "brown", 50, false);
movableObject.move();  // Memanggil method move() dari RaceHorse

Upgradable upgradableObject = new Horse("Lightning");
upgradableObject.upgradeSpeed(10);  // Memanggil method dari Horse
```

### C. Polymorphism dengan Abstract Class

```java
// Bisa menggunakan tipe abstract class sebagai reference
BaseEntity entity1 = new Horse("Thunder");
BaseEntity entity2 = new User("player1", "password");
BaseEntity entity3 = new RaceHistory(1, "Thunder", 1, 5, 100);

// Semua bisa memanggil method yang sama, tapi hasil berbeda
System.out.println(entity1.getInfo());  // Output info Horse
System.out.println(entity2.getInfo());  // Output info User
System.out.println(entity3.getInfo());  // Output info RaceHistory
```

---

## 5️⃣ GUI (Graphical User Interface)

### A. JFrame - Window Utama

📄 **File: `src/view/GameFrame.java`**

```java
public class GameFrame extends JFrame {
    private CardLayout cardLayout;  // Layout untuk switch antar panel
    private JPanel mainPanel;

    public GameFrame() {
        setTitle("Horse Racing Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Menambahkan panel-panel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(racePanel, "race");
        // ...
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);  // Switch panel
    }
}
```

### B. JPanel - Panel/Container

📄 **File: `src/view/LoginPanel.java`**

```java
public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Custom painting untuk background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
```

### C. Komponen GUI yang Digunakan

| Komponen         | File                      | Kegunaan                 |
| ---------------- | ------------------------- | ------------------------ |
| `JFrame`         | GameFrame.java            | Window utama             |
| `JPanel`         | Semua panel               | Container untuk komponen |
| `JButton`        | Semua panel               | Tombol interaksi         |
| `JTextField`     | LoginPanel, RegisterPanel | Input text               |
| `JPasswordField` | LoginPanel, RegisterPanel | Input password           |
| `JLabel`         | Semua panel               | Menampilkan text/gambar  |
| `JTable`         | HistoryPanel              | Menampilkan data tabular |
| `JScrollPane`    | HistoryPanel              | Scroll untuk table       |
| `JLayeredPane`   | RacePanel                 | Layer untuk animasi      |
| `JOptionPane`    | Semua panel               | Dialog popup             |

### D. Layout Manager

| Layout          | File                      | Kegunaan           |
| --------------- | ------------------------- | ------------------ |
| `CardLayout`    | GameFrame.java            | Switch antar panel |
| `GridBagLayout` | LoginPanel, MainMenuPanel | Layout fleksibel   |
| `BorderLayout`  | RacePanel, HistoryPanel   | Layout 5 region    |
| `FlowLayout`    | Button panels             | Layout berurutan   |

### E. Event Handling

📄 **Contoh di `LoginPanel.java`:**

```java
loginButton.addActionListener(e -> handleLogin());
registerButton.addActionListener(e -> gameFrame.showPanel("register"));
passwordField.addActionListener(e -> handleLogin());  // Enter key
```

---

## 6️⃣ THREAD (Multi-threading)

### 📄 File: `src/view/RacePanel.java`

### A. Timer untuk Animasi

```java
private Timer updateTimer;
private Timer animationTimer;

// Timer untuk update posisi kuda
updateTimer = new Timer(RACE_UPDATE_DELAY_MS, e -> {
    updateRacePositions();
    repaint();
});

// Timer untuk animasi frame
animationTimer = new Timer(ANIMATION_DELAY_MS, e -> {
    currentFrame = (currentFrame + 1) % totalFrames;
    updateHorseAnimations();
});
```

### B. Thread untuk Setiap Kuda

```java
private List<Thread> raceThreads;

private void startRace() {
    raceThreads.clear();

    for (RaceHorse horse : horses) {
        Thread horseThread = new Thread(() -> {
            while (!horse.isFinished() && raceInProgress) {
                horse.move();  // Kuda bergerak

                if (horse.getPosition() >= trackLength) {
                    horse.setFinished(true);
                    synchronized(finishOrder) {
                        finishOrder.add(horse);
                    }
                }

                try {
                    Thread.sleep(RACE_UPDATE_DELAY_MS);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        raceThreads.add(horseThread);
        horseThread.start();
    }
}
```

### C. Synchronized untuk Thread Safety

```java
synchronized(finishOrder) {
    finishOrder.add(horse);  // Thread-safe access
}
```

### D. SwingUtilities untuk GUI Thread

```java
SwingUtilities.invokeLater(() -> {
    updateUI();  // Update GUI dari thread lain
});
```

### Diagram Thread:

```
Main Thread (EDT - Event Dispatch Thread)
    │
    ├── Timer Thread (updateTimer)
    │   └── Update posisi & repaint setiap 30ms
    │
    ├── Timer Thread (animationTimer)
    │   └── Update frame animasi setiap 80ms
    │
    └── Horse Threads (per kuda)
        ├── Horse 1 Thread → move() → check finish
        ├── Horse 2 Thread → move() → check finish
        ├── Horse 3 Thread → move() → check finish
        ├── Horse 4 Thread → move() → check finish
        └── Horse 5 Thread → move() → check finish
```

---

## 7️⃣ ENCAPSULATION

### Penggunaan Access Modifier

| Modifier    | Contoh                           | Penjelasan                     |
| ----------- | -------------------------------- | ------------------------------ |
| `private`   | `private String name;`           | Hanya bisa diakses dalam class |
| `protected` | `protected GameFrame gameFrame;` | Bisa diakses oleh subclass     |
| `public`    | `public void move()`             | Bisa diakses dari mana saja    |

### Contoh Encapsulation di `Horse.java`:

```java
public class Horse extends BaseEntity implements Upgradable {
    // Private fields - tidak bisa diakses langsung
    private String name;
    private int speed;
    private int stamina;
    private int acceleration;
    private int level;

    // Public getter - untuk mengakses data
    public int getSpeed() { return speed; }

    // Public setter - untuk mengubah data dengan validasi
    public void setSpeed(int speed) { this.speed = speed; }

    // Controlled modification through methods
    @Override
    public void upgradeSpeed(int amount) {
        this.speed += amount;
    }
}
```

---

## 8️⃣ DIAGRAM HUBUNGAN CLASS

```
┌─────────────────────────────────────────────────────────────────┐
│                         INTERFACES                               │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Upgradable  │  │   Movable    │  │ Displayable  │          │
│  │  - upgrade   │  │  - move()    │  │ - refresh()  │          │
│  │    Speed()   │  │  - getPos()  │  │ - getName()  │          │
│  │  - levelUp() │  │  - isFinish  │  │              │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                 │                 │                   │
└─────────│─────────────────│─────────────────│───────────────────┘
          │                 │                 │
          │ implements      │ implements      │ implements
          │                 │                 │
┌─────────▼─────────────────▼─────────────────▼───────────────────┐
│                      ABSTRACT CLASSES                            │
├─────────────────────────────────────────────────────────────────┤
│  ┌────────────────────────┐     ┌────────────────────────┐     │
│  │      BaseEntity        │     │       BasePanel        │     │
│  │  (abstract)            │     │  (abstract)            │     │
│  │  + getName(): String   │     │  + initComponents()    │     │
│  │  + getInfo(): String   │     │  + paintComponent()    │     │
│  └──────────┬─────────────┘     └────────────────────────┘     │
│             │                                                   │
└─────────────│───────────────────────────────────────────────────┘
              │ extends
              │
┌─────────────▼───────────────────────────────────────────────────┐
│                      CONCRETE CLASSES                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  MODEL:                                                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │     Horse       │  │      User       │  │   RaceHistory   │ │
│  │ extends Base    │  │ extends Base    │  │ extends Base    │ │
│  │ impl Upgradable │  │ Entity          │  │ Entity          │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                  │
│  ┌─────────────────┐                                            │
│  │   RaceHorse     │                                            │
│  │ impl Movable    │                                            │
│  └─────────────────┘                                            │
│                                                                  │
│  VIEW:                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  MainMenuPanel  │  │  UpgradePanel   │  │  HistoryPanel   │ │
│  │ impl Displayable│  │ impl Displayable│  │ impl Displayable│ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📝 QUICK REFERENCE

| Konsep         | Lokasi File                                           | Keyword                             |
| -------------- | ----------------------------------------------------- | ----------------------------------- |
| Abstract Class | `BaseEntity.java`, `BasePanel.java`                   | `abstract class`, `abstract method` |
| Interface      | `Upgradable.java`, `Movable.java`, `Displayable.java` | `interface`, `implements`           |
| Inheritance    | `Horse.java`, `User.java`, `RaceHistory.java`         | `extends`                           |
| Polymorphism   | Semua class yang override method                      | `@Override`                         |
| Encapsulation  | Semua model class                                     | `private`, `getter`, `setter`       |
| GUI - JFrame   | `GameFrame.java`                                      | `extends JFrame`                    |
| GUI - JPanel   | Semua panel di `view/`                                | `extends JPanel`                    |
| Thread         | `RacePanel.java`                                      | `Thread`, `Timer`, `synchronized`   |

---
