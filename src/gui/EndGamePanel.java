package gui;

import main.Main;
import model.Colors;
import model.Game;
import model.Player;
import strategy.scoring.GameEndScoringStrategy;
import strategy.scoring.PieceCaptureScoringStrategy;

import javax.swing.*;
import java.awt.*;

public class EndGamePanel extends JPanel {
    private ChessGUI parentFrame;
    private Game game;
    private Player humanPlayer;
    private String gameResult;

    private JLabel resultLabel;
    private JTextArea scoreBreakdownArea;
    private JButton playAgainButton;
    private JButton mainMenuButton;
    private JButton exitButton;

    private static final Color BG_DARK = new Color(26, 32, 44);
    private static final Color BG_CARD = new Color(45, 55, 72);
    private static final Color TEXT_PRIMARY = new Color(237, 242, 247);
    private static final Color TEXT_SECONDARY = new Color(160, 174, 192);
    private static final Color ACCENT_GREEN = new Color(72, 187, 120);
    private static final Color ACCENT_BLUE = new Color(66, 153, 225);
    private static final Color ACCENT_RED = new Color(245, 101, 101);

    public EndGamePanel(ChessGUI parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_DARK);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        resultLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        resultLabel.setForeground(TEXT_PRIMARY);
        topPanel.add(resultLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_CARD);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel breakdownTitle = new JLabel("Score Breakdown", SwingConstants.CENTER);
        breakdownTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        breakdownTitle.setForeground(TEXT_PRIMARY);
        breakdownTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        centerPanel.add(breakdownTitle, BorderLayout.NORTH);

        scoreBreakdownArea = new JTextArea();
        scoreBreakdownArea.setEditable(false);
        scoreBreakdownArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        scoreBreakdownArea.setBackground(BG_CARD);
        scoreBreakdownArea.setForeground(TEXT_SECONDARY);
        scoreBreakdownArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(scoreBreakdownArea, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setBackground(BG_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        playAgainButton = createStyledButton("Play Again", ACCENT_GREEN);
        playAgainButton.addActionListener(e -> handlePlayAgain());

        mainMenuButton = createStyledButton("Main Menu", ACCENT_BLUE);
        mainMenuButton.addActionListener(e -> handleMainMenu());

        exitButton = createStyledButton("Exit", ACCENT_RED);
        exitButton.addActionListener(e -> handleExit());

        bottomPanel.add(playAgainButton);
        bottomPanel.add(mainMenuButton);
        bottomPanel.add(exitButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public void displayResult(Game game, Player humanPlayer, String result) {
        this.game = game;
        this.humanPlayer = humanPlayer;
        this.gameResult = result;

        updateResultLabel(result);

        calculateAndDisplayScore(result);
    }

    private void updateResultLabel(String result) {
        switch (result) {
            case "CHECKMATE_WIN":
                resultLabel.setText("VICTORY!");
                resultLabel.setForeground(new Color(0, 150, 0));
                break;
            case "CHECKMATE_LOSS":
                resultLabel.setText("DEFEAT");
                resultLabel.setForeground(new Color(200, 50, 50));
                break;
            case "DRAW":
            case "STALEMATE":
                resultLabel.setText("DRAW");
                resultLabel.setForeground(new Color(100, 100, 150));
                break;
            case "RESIGN":
                resultLabel.setText("RESIGNED");
                resultLabel.setForeground(new Color(150, 100, 50));
                break;
            default:
                resultLabel.setText("GAME OVER");
                resultLabel.setForeground(new Color(60, 60, 60));
        }
    }

    private void calculateAndDisplayScore(String result) {
        StringBuilder breakdown = new StringBuilder();

        int initialScore = humanPlayer.getPoints();

        int capturedPiecePoints = calculateCapturedPiecePoints();

        int gameEndBonus = calculateGameEndBonus(result);

        int finalScore = initialScore + capturedPiecePoints + gameEndBonus;

        humanPlayer.setPoints(finalScore);

        breakdown.append(String.format("%-30s %6d\n", "Initial Score:", initialScore));
        breakdown.append(String.format("%-30s %6d\n", "Captured Pieces:", capturedPiecePoints));
        breakdown.append(String.format("%-30s %6s\n", "", "--------"));
        breakdown.append(String.format("%-30s %6d\n", "Subtotal:", initialScore + capturedPiecePoints));
        breakdown.append("\n");
        breakdown.append(String.format("%-30s %6s%d\n", "Game Result Bonus:",
                                        gameEndBonus >= 0 ? "+" : "", gameEndBonus));
        breakdown.append(String.format("%-30s %6s\n", "", "========"));
        breakdown.append(String.format("%-30s %6d\n", "FINAL SCORE:", finalScore));

        breakdown.append("\n\nBonus Points:\n");
        breakdown.append("  Checkmate Win:  +300\n");
        breakdown.append("  Checkmate Loss: -300\n");
        breakdown.append("  Draw/Stalemate: +150\n");
        breakdown.append("  Resign:         -150\n");

        scoreBreakdownArea.setText(breakdown.toString());
    }

    private int calculateCapturedPiecePoints() {
        if (game == null || humanPlayer == null) {
            return 0;
        }

        PieceCaptureScoringStrategy captureStrategy = new PieceCaptureScoringStrategy();
        int totalPoints = 0;

        return 0;
    }

    private int calculateGameEndBonus(String result) {
        GameEndScoringStrategy endStrategy = new GameEndScoringStrategy();

        GameEndScoringStrategy.GameResult gameResult = switch (result) {
            case "CHECKMATE_WIN" -> GameEndScoringStrategy.GameResult.CHECKMATE_WIN;
            case "CHECKMATE_LOSS" -> GameEndScoringStrategy.GameResult.CHECKMATE_LOSS;
            case "DRAW", "STALEMATE" -> GameEndScoringStrategy.GameResult.DRAW;
            case "RESIGN" -> GameEndScoringStrategy.GameResult.RESIGN;
            default -> GameEndScoringStrategy.GameResult.SAVE_AND_EXIT;
        };

        return endStrategy.getGameEndBonus(gameResult);
    }

    private void handlePlayAgain() {
        parentFrame.showMainMenu();

        SwingUtilities.invokeLater(() -> {
            MainMenuPanel menuPanel = (MainMenuPanel) parentFrame.getPanel("MENU");
            if (menuPanel != null) {
                menuPanel.showNewGameDialog();
            }
        });
    }

    private void handleMainMenu() {
        parentFrame.showMainMenu();
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Main.getInstance().write();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    public void displayResultSimple(String result, int initialScore, int finalScore,
                                    int capturedPiecePoints, int gameEndBonus) {
        this.gameResult = result;

        updateResultLabel(result);

        StringBuilder breakdown = new StringBuilder();
        breakdown.append(String.format("%-30s %6d\n", "Initial Score:", initialScore));
        breakdown.append(String.format("%-30s %6d\n", "Captured Pieces:", capturedPiecePoints));
        breakdown.append(String.format("%-30s %6s\n", "", "--------"));
        breakdown.append(String.format("%-30s %6d\n", "Subtotal:", initialScore + capturedPiecePoints));
        breakdown.append("\n");
        breakdown.append(String.format("%-30s %6s%d\n", "Game Result Bonus:",
                                        gameEndBonus >= 0 ? "+" : "", gameEndBonus));
        breakdown.append(String.format("%-30s %6s\n", "", "========"));
        breakdown.append(String.format("%-30s %6d\n", "FINAL SCORE:", finalScore));

        breakdown.append("\n\nBonus Points:\n");
        breakdown.append("  Checkmate Win:  +300\n");
        breakdown.append("  Checkmate Loss: -300\n");
        breakdown.append("  Draw/Stalemate: +150\n");
        breakdown.append("  Resign:         -150\n");

        scoreBreakdownArea.setText(breakdown.toString());
    }

    public void setGameResult(String result, int playerScore, int bonus, int totalScore) {
        this.gameResult = result;

        resultLabel.setText(result);
        switch (result.toUpperCase()) {
            case "VICTORY!", "VICTORY":
                resultLabel.setForeground(new Color(0, 150, 0));
                break;
            case "DEFEAT":
                resultLabel.setForeground(new Color(200, 50, 50));
                break;
            case "DRAW":
                resultLabel.setForeground(new Color(100, 100, 150));
                break;
            case "RESIGNED":
                resultLabel.setForeground(new Color(150, 100, 50));
                break;
            default:
                resultLabel.setForeground(new Color(60, 60, 60));
        }

        StringBuilder breakdown = new StringBuilder();
        int initialScore = playerScore - bonus;
        breakdown.append(String.format("%-30s %6d\n", "Score before game:", initialScore));
        breakdown.append(String.format("%-30s %6s%d\n", "Game Result Bonus:",
                                        bonus >= 0 ? "+" : "", bonus));
        breakdown.append(String.format("%-30s %6s\n", "", "========"));
        breakdown.append(String.format("%-30s %6d\n", "FINAL SCORE:", totalScore));

        breakdown.append("\n\nBonus Points:\n");
        breakdown.append("  Checkmate Win:  +300\n");
        breakdown.append("  Checkmate Loss: -300\n");
        breakdown.append("  Draw/Stalemate: +150\n");
        breakdown.append("  Resign:         -150\n");

        scoreBreakdownArea.setText(breakdown.toString());
    }
}
