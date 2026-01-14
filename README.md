# Chess Master - Java OOP Project

A fully-featured chess game built with Java Swing, demonstrating advanced Object-Oriented Programming concepts and design patterns.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Architecture & Design Patterns](#architecture--design-patterns)
- [OOP Concepts](#oop-concepts-demonstrated)
- [GUI Overview](#gui-overview)
- [Project Structure](#project-structure)

## 🎯 Overview

Chess Master is a comprehensive chess application that allows users to play chess against an AI opponent. The project showcases professional software engineering practices, including multiple design patterns, a clean separation of concerns, and a modern graphical user interface.

### Key Highlights

- **Full Chess Implementation**: Complete chess rules including castling, en passant, pawn promotion, check, checkmate, and stalemate
- **AI Opponent**: Play against a computer opponent with legal move validation
- **User Management**: Account system with persistent storage
- **Game Persistence**: Save and resume multiple games
- **Modern GUI**: Dark-themed, professional interface with Unicode chess pieces
- **Scoring System**: Points based on captured pieces and game outcomes

## 🛠 Technologies Used

- **Java SE 11+**: Core programming language
- **Java Swing**: GUI framework
- **JSON**: Data persistence format

### Libraries

- `javax.swing.*`: GUI components
- `java.awt.*`: Graphics and layout management
- Standard Java collections (`List`, `Map`, `Set`, `TreeSet`)
- Java Streams API for functional operations

## 🏗 Architecture & Design Patterns

This project implements **4 major design patterns** as per OOP best practices:

### 1. Observer Pattern

**Purpose**: Enables the game to notify multiple UI components about game state changes without tight coupling.

**Implementation**:
- **Subject**: `Game` class maintains a list of observers
- **Observer Interface**: `GameObserver` defines callback methods
- **Concrete Observer**: `GamePanel` implements `GameObserver`

**Benefits**:
- Decouples game logic from UI updates
- Multiple observers can react to the same event
- Easy to add new observers without modifying game logic

**Example**:
```java
// Observer interface
public interface GameObserver {
    void onMoveMade(Move move);
    void onPieceCaptured(Piece piece);
    void onPlayerSwitch(Player currentPlayer);
    void onCheck(Player playerInCheck);
    void onCheckmate(Player winner);
    void onGameEnd(String result);
}

// Game notifies observers
public void notifyPieceCaptured(Piece piece) {
    for (GameObserver observer : observers) {
        observer.onPieceCaptured(piece);
    }
}
```

### 2. Strategy Pattern

**Purpose**: Defines a family of algorithms (scoring strategies) and makes them interchangeable.

**Implementation**:
- **Strategy Interface**: `ScoringStrategy`
- **Concrete Strategies**:
  - `PieceCaptureScoringStrategy`: Calculates points for captured pieces
  - `GameEndScoringStrategy`: Calculates bonus/penalty for game outcomes

**Benefits**:
- Easy to add new scoring algorithms
- Scoring logic is encapsulated and reusable
- Strategy can be changed at runtime

**Example**:
```java
// Strategy interface
public interface ScoringStrategy {
    int calculateScore(Piece piece);
}

// Concrete strategy
public class PieceCaptureScoringStrategy implements ScoringStrategy {
    @Override
    public int calculateScore(Piece piece) {
        return switch (piece.type()) {
            case 'P' -> 10;  // Pawn
            case 'N', 'B' -> 30;  // Knight, Bishop
            case 'R' -> 50;  // Rook
            case 'Q' -> 90;  // Queen
            default -> 0;
        };
    }
}
```

### 3. Factory Pattern

**Purpose**: Creates chess pieces without exposing the creation logic.

**Implementation**:
- **Factory Class**: `PieceFactory`
- **Products**: All piece classes (King, Queen, Rook, Bishop, Knight, Pawn)

**Benefits**:
- Centralized piece creation
- Easy to add new piece types
- Reduces code duplication

**Example**:
```java
public class PieceFactory {
    public static Piece createPiece(char type, Colors color, Position position) {
        return switch (type) {
            case 'K' -> new King(color, position);
            case 'Q' -> new Queen(color, position);
            case 'R' -> new Rook(color, position);
            case 'B' -> new Bishop(color, position);
            case 'N' -> new Knight(color, position);
            case 'P' -> new Pawn(color, position);
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        };
    }
}
```

### 4. Singleton Pattern

**Purpose**: Ensures only one instance of certain utility classes exists.

**Implementation**:
- **Singleton Classes**:
  - `JsonReaderUtil`: Handles JSON file operations
  - Application instance management

**Benefits**:
- Prevents multiple instances of utility classes
- Global access point
- Controlled resource management

**Example**:
```java
public class JsonReaderUtil {
    private static JsonReaderUtil instance;

    private JsonReaderUtil() {
        // Private constructor
    }

    public static JsonReaderUtil getInstance() {
        if (instance == null) {
            instance = new JsonReaderUtil();
        }
        return instance;
    }
}
```

## 🎨 GUI Overview

### Architecture

The GUI is built using **Java Swing** with a **CardLayout** system for seamless panel switching.

#### Main Frame Structure

```java
public class ChessGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // All panels managed by CardLayout
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private EndGamePanel endGamePanel;
}
```

### Panel Hierarchy

```
ChessGUI (JFrame)
├── LoginPanel           → User authentication
├── RegisterPanel        → New user registration
├── MainMenuPanel        → Game selection & user stats
├── GamePanel           → Active chess game
│   ├── Top Panel       → Welcome message, turn, score
│   ├── Board Container → Chess board with coordinates
│   ├── Right Panel     → Captured pieces & move history
│   └── Bottom Panel    → Control buttons
└── EndGamePanel        → Game results summary
```

### Design System

#### Color Palette

The application uses a modern dark theme:

| Component | Color Code | Usage |
|-----------|------------|-------|
| **BG_DARK** | `#1A202C` | Main background |
| **BG_CARD** | `#2D3748` | Panels and cards |
| **BOARD_LIGHT** | `#F0D9B5` | Light chess squares |
| **BOARD_DARK** | `#B58863` | Dark chess squares |
| **TEXT_PRIMARY** | `#EDF2F7` | Main text |
| **TEXT_SECONDARY** | `#A0AEB0` | Secondary text |
| **ACCENT_BLUE** | `#4299E1` | Information/Continue |
| **ACCENT_GREEN** | `#48BB78` | Success/Your turn |
| **ACCENT_RED** | `#F56565` | Danger/Computer turn |
| **HIGHLIGHT_SELECT** | `#FFCE54` | Selected piece |
| **HIGHLIGHT_MOVE** | `#BAC444` | Valid move squares |

#### Typography

```java
// Headers and titles
new Font("Segoe UI", Font.BOLD, 22-28)

// Body text and labels
new Font("Segoe UI", Font.PLAIN, 14-16)

// Chess pieces (Unicode symbols)
new Font("Segoe UI", Font.PLAIN, 32-40)

// Buttons
new Font("Segoe UI", Font.BOLD, 14)

// Move history (monospace)
new Font("Consolas", Font.PLAIN, 13)
```

### Scoring System

Points are awarded for captured pieces:

| Piece | Points |
|-------|--------|
| **Pawn** (♙/♟) | 10 |
| **Knight** (♘/♞) | 30 |
| **Bishop** (♗/♝) | 30 |
| **Rook** (♖/♜) | 50 |
| **Queen** (♕/♛) | 90 |
| **King** (♔/♚) | 0 (cannot be captured) |

**Game End Bonuses**:
- **Checkmate Win**: +300 points
- **Checkmate Loss**: -300 points
- **Stalemate (Draw)**: +150 points
- **Resign**: -150 points

**Note**: Points from each game are added to your cumulative total across all games.

## 📁 Project Structure

```
TemaPOO/
│
├── src/                           # Source code directory
│   │
│   ├── main/                      # Application entry point
│   │   └── Main.java              # Main class, initializes application
│   │
│   ├── gui/                       # Graphical User Interface
│   │   ├── ChessGUI.java          # Main frame with CardLayout
│   │   ├── LoginPanel.java        # User login screen
│   │   ├── RegisterPanel.java     # User registration screen
│   │   ├── MainMenuPanel.java     # Main menu with game options
│   │   ├── GamePanel.java         # Active chess game interface
│   │   └── EndGamePanel.java      # Game results and statistics
│   │
│   ├── model/                     # Data models and game logic
│   │   ├── Game.java              # Game state, logic, and rules
│   │   ├── Board.java             # Chess board representation
│   │   ├── Player.java            # Player data and actions
│   │   ├── User.java              # User account information
│   │   ├── Move.java              # Move representation
│   │   ├── Position.java          # Board position (A1-H8 notation)
│   │   ├── Colors.java            # Enum: WHITE, BLACK
│   │   └── ChessPair.java         # Generic pair utility class
│   │
│   ├── pieces/                    # Chess piece implementations
│   │   ├── Piece.java             # Abstract base class for all pieces
│   │   ├── King.java              # King piece (♔/♚)
│   │   ├── Queen.java             # Queen piece (♕/♛)
│   │   ├── Rook.java              # Rook piece (♖/♜)
│   │   ├── Bishop.java            # Bishop piece (♗/♝)
│   │   ├── Knight.java            # Knight piece (♘/♞)
│   │   └── Pawn.java              # Pawn piece (♙/♟)
│   │
│   ├── factory/                   # Factory Pattern
│   │   └── PieceFactory.java      # Creates chess pieces
│   │
│   ├── strategy/                  # Strategy Pattern
│   │   └── scoring/
│   │       ├── ScoringStrategy.java              # Strategy interface
│   │       ├── PieceCaptureScoringStrategy.java  # Points for captures
│   │       └── GameEndScoringStrategy.java       # Bonus/penalty on game end
│   │
│   ├── observer/                  # Observer Pattern
│   │   └── GameObserver.java      # Observer interface for game events
│   │
│   ├── exceptions/                # Custom exceptions
│   │   ├── InvalidMoveException.java     # Thrown for invalid moves
│   │   └── InvalidCommandException.java  # Thrown for invalid commands
│   │
│   └── utils/                     # Utility classes
│       └── JsonReaderUtil.java    # JSON read/write operations
│
├── input/                         # Data persistence files
│   ├── accounts.json              # User accounts (email, password, points)
│   └── games.json                 # Saved games (board state, moves)
│
├── bin/                           # Compiled .class files (generated)
│
└── README.md                      # This file
```

### Key Directories Explained

#### `src/main/`
Contains the application entry point. `Main.java` initializes the GUI and loads saved data.

#### `src/gui/`
All graphical user interface components. Uses Java Swing with CardLayout for panel switching.

#### `src/model/`
Core business logic and data models. Contains game rules, board state, and player information.

#### `src/pieces/`
Implementation of all chess pieces with their specific movement rules. Each piece extends the abstract `Piece` class.

#### `src/factory/`
Factory pattern implementation for creating chess pieces dynamically.

#### `src/strategy/`
Strategy pattern implementation for scoring calculations. Different strategies can be applied for different game situations.

#### `src/observer/`
Observer pattern implementation allowing the UI to react to game events without tight coupling.

#### `input/`
JSON files for data persistence. User accounts and game states are saved here.
