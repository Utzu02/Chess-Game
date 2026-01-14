package gui;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.*;
import observer.GameObserver;
import pieces.Pawn;
import pieces.Piece;
import strategy.scoring.GameEndScoringStrategy;
import strategy.scoring.PieceCaptureScoringStrategy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class GamePanel extends JPanel implements GameObserver {
    private ChessGUI parentFrame;
    private Game game;
    private Player humanPlayer;

    private JButton[][] boardButtons;
    private JTextArea moveHistoryArea;
    private JLabel statusLabel;
    private JLabel turnLabel;
    private JLabel scoreLabel;

    private Position selectedPosition;
    private List<Position> possibleMoves;

    private static final Color BG_DARK = new Color(26, 32, 44);
    private static final Color BG_CARD = new Color(45, 55, 72);
    private static final Color BOARD_LIGHT = new Color(240, 217, 181);
    private static final Color BOARD_DARK = new Color(181, 136, 99);
    private static final Color HIGHLIGHT_SELECT = new Color(255, 206, 84);
    private static final Color HIGHLIGHT_MOVE = new Color(186, 202, 68);
    private static final Color TEXT_PRIMARY = new Color(237, 242, 247);
    private static final Color TEXT_SECONDARY = new Color(160, 174, 192);
    private static final Color ACCENT_BLUE = new Color(66, 153, 225);
    private static final Color ACCENT_GREEN = new Color(72, 187, 120);
    private static final Color ACCENT_RED = new Color(245, 101, 101);

    private static final String WHITE_KING = "\u2654";
    private static final String WHITE_QUEEN = "\u2655";
    private static final String WHITE_ROOK = "\u2656";
    private static final String WHITE_BISHOP = "\u2657";
    private static final String WHITE_KNIGHT = "\u2658";
    private static final String WHITE_PAWN = "\u2659";
    private static final String BLACK_KING = "\u265A";
    private static final String BLACK_QUEEN = "\u265B";
    private static final String BLACK_ROOK = "\u265C";
    private static final String BLACK_BISHOP = "\u265D";
    private static final String BLACK_KNIGHT = "\u265E";
    private static final String BLACK_PAWN = "\u265F";

    public GamePanel(ChessGUI parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_DARK);

        JPanel boardContainer = createBoardContainer();
        centerPanel.add(boardContainer);

        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        statusLabel = new JLabel("Chess Master");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statusLabel.setForeground(TEXT_PRIMARY);
        leftPanel.add(statusLabel);

        turnLabel = new JLabel("Your turn");
        turnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        turnLabel.setForeground(ACCENT_BLUE);
        leftPanel.add(turnLabel);

        panel.add(leftPanel, BorderLayout.WEST);

        scoreLabel = new JLabel("Score: 0 pts");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scoreLabel.setForeground(new Color(255, 215, 0));
        panel.add(scoreLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createBoardContainer() {
        JPanel outerContainer = new JPanel(new BorderLayout(5, 5));
        outerContainer.setBackground(BG_DARK);

        JPanel topCoords = new JPanel(new GridLayout(1, 8, 0, 0));
        topCoords.setBackground(BG_DARK);
        topCoords.setPreferredSize(new Dimension(640, 20));
        topCoords.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        for (char c = 'A'; c <= 'H'; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setForeground(TEXT_PRIMARY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            topCoords.add(label);
        }

        JPanel leftCoords = new JPanel(new GridLayout(8, 1, 0, 0));
        leftCoords.setBackground(BG_DARK);
        leftCoords.setPreferredSize(new Dimension(30, 640));
        for (int i = 8; i >= 1; i--) {
            JLabel label = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            label.setForeground(TEXT_PRIMARY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            leftCoords.add(label);
        }

        JPanel shadowPanel = new JPanel();
        shadowPanel.setLayout(new BorderLayout());
        shadowPanel.setBackground(BG_DARK);
        shadowPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 100), 8),
            BorderFactory.createLineBorder(new Color(40, 40, 40), 2)
        ));

        JPanel boardPanel = createBoardPanel();
        shadowPanel.add(boardPanel);

        outerContainer.add(topCoords, BorderLayout.NORTH);
        outerContainer.add(leftCoords, BorderLayout.WEST);
        outerContainer.add(shadowPanel, BorderLayout.CENTER);

        return outerContainer;
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 8, 0, 0));
        panel.setPreferredSize(new Dimension(640, 640));
        panel.setBackground(Color.BLACK);

        boardButtons = new JButton[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();

                square.setFont(new Font("Segoe UI", Font.PLAIN, 40));
                square.setFocusPainted(false);
                square.setBorderPainted(true);
                square.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
                square.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if ((row + col) % 2 == 0) {
                    square.setBackground(BOARD_LIGHT);
                } else {
                    square.setBackground(BOARD_DARK);
                }

                int finalRow = row;
                int finalCol = col;
                square.addActionListener(e -> handleSquareClick(finalRow, finalCol));

                square.addMouseListener(new java.awt.event.MouseAdapter() {
                    Color originalColor = square.getBackground();

                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (selectedPosition == null ||
                            !boardPositionToGamePosition(finalRow, finalCol).equals(selectedPosition)) {
                            square.setBackground(originalColor.brighter());
                        }
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (selectedPosition != null &&
                            boardPositionToGamePosition(finalRow, finalCol).equals(selectedPosition)) {
                            return;
                        }
                        if (possibleMoves != null &&
                            possibleMoves.contains(boardPositionToGamePosition(finalRow, finalCol))) {
                            return;
                        }
                        square.setBackground(originalColor);
                    }
                });

                boardButtons[row][col] = square;
                panel.add(square);
            }
        }

        return panel;
    }

    private JLabel capturedWhiteArea;
    private JLabel capturedBlackArea;

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(BG_DARK);

        JPanel whiteCard = createCard("White Captured");
        capturedWhiteArea = new JLabel("<html></html>");
        capturedWhiteArea.setFont(new Font("Segoe UI", Font.BOLD, 24));
        capturedWhiteArea.setForeground(TEXT_PRIMARY);
        capturedWhiteArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        capturedWhiteArea.setPreferredSize(new Dimension(260, 80));
        capturedWhiteArea.setVerticalAlignment(SwingConstants.TOP);
        whiteCard.add(capturedWhiteArea);
        panel.add(whiteCard);

        panel.add(Box.createVerticalStrut(10));

        JPanel blackCard = createCard("Black Captured");
        capturedBlackArea = new JLabel("<html></html>");
        capturedBlackArea.setFont(new Font("Segoe UI", Font.BOLD, 24));
        capturedBlackArea.setForeground(TEXT_PRIMARY);
        capturedBlackArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        capturedBlackArea.setPreferredSize(new Dimension(260, 80));
        capturedBlackArea.setVerticalAlignment(SwingConstants.TOP);
        blackCard.add(capturedBlackArea);
        panel.add(blackCard);

        panel.add(Box.createVerticalStrut(10));

        JPanel historyCard = createCard("Move History");
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        moveHistoryArea.setBackground(new Color(26, 32, 44));
        moveHistoryArea.setForeground(TEXT_SECONDARY);
        moveHistoryArea.setCaretColor(TEXT_PRIMARY);
        moveHistoryArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(260, 200));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        historyCard.add(scrollPane);
        panel.add(historyCard);

        return panel;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(280, 500));
        card.setMinimumSize(new Dimension(280, 100));
        card.setPreferredSize(new Dimension(280, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(10));

        return card;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(BG_DARK);

        JButton resignButton = createModernButton("Resign", ACCENT_RED);
        resignButton.addActionListener(e -> handleResign());
        panel.add(resignButton);

        JButton saveButton = createModernButton("Save & Exit", ACCENT_BLUE);
        saveButton.addActionListener(e -> handleSaveAndExit());
        panel.add(saveButton);

        JButton backButton = createModernButton("Back to Menu", new Color(113, 128, 150));
        backButton.addActionListener(e -> handleBackToMenu());
        panel.add(backButton);

        return panel;
    }

    private JButton createModernButton(String text, Color bgColor) {
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

    public void setGame(Game game, Player humanPlayer) {
        this.game = game;
        this.humanPlayer = humanPlayer;

        User currentUser = parentFrame.getMainApp().getCurrentUser();
        if (currentUser != null) {
            statusLabel.setText("Welcome, " + currentUser.getEmail());
        } else {
            statusLabel.setText("Welcome, " + humanPlayer.getName());
        }

        game.addObserver(this);
        updateBoard();
        updateMoveHistory();
        updateCapturedPieces();
        updateStatus();
        updateScore();

        if (!isHumanTurn()) {
            scheduleComputerMove();
        }
    }

    private void handleSquareClick(int row, int col) {
        if (game == null || !isHumanTurn()) {
            return;
        }

        try {
            Position clickedPos = boardPositionToGamePosition(row, col);
            Piece clickedPiece = game.getBoard().getPieceAt(clickedPos);

            if (selectedPosition == null) {
                if (clickedPiece != null && clickedPiece.getColor() == humanPlayer.getColor()) {
                    selectedPosition = clickedPos;
                    possibleMoves = clickedPiece.getPossibleMoves(game.getBoard());
                    highlightPossibleMoves();
                }
            } else {
                if (possibleMoves != null && possibleMoves.contains(clickedPos)) {
                    makeMove(selectedPosition, clickedPos);
                }
                selectedPosition = null;
                possibleMoves = null;
                updateBoard();
            }
        } catch (InvalidMoveException e) {
            selectedPosition = null;
            possibleMoves = null;
            updateBoard();
        }
    }

    private void makeMove(Position from, Position to) {
        try {
            Piece movingPiece = game.getBoard().getPieceAt(from);
            boolean needsPromotion = false;

            if (movingPiece instanceof Pawn) {
                int destRank = to.getY();
                if ((movingPiece.getColor() == Colors.WHITE && destRank == 8) ||
                    (movingPiece.getColor() == Colors.BLACK && destRank == 1)) {
                    needsPromotion = true;
                }
            }

            Piece capturedPiece = game.getBoard().getPieceAt(to);

            if (needsPromotion) {
                game.getBoard().movePiece(from, to, humanPlayer.getColor(), false);
                String promotionChoice = showPromotionDialog();
                game.getBoard().promotePawnTo(to, humanPlayer.getColor(), promotionChoice);
            } else {
                humanPlayer.makeMove(from, to, game.getBoard());
            }

            game.addMove(humanPlayer, from, to, capturedPiece);

            if (capturedPiece != null) {
                game.notifyPieceCaptured(capturedPiece);
            }
            game.notifyMoveMade(new Move(humanPlayer.getColor(), from, to, capturedPiece));

            updateBoard();
            updateMoveHistory();
            updateScore();

            if (checkGameEnd()) {
                return;
            }

            game.switchPlayer();
            game.registerBoardState();
            game.notifyPlayerSwitch(game.getCurrentPlayer());
            updateStatus();

            if (!isHumanTurn()) {
                scheduleComputerMove();
            }
        } catch (InvalidMoveException | InvalidCommandException ex) {
            // Silently ignore invalid moves - the move validation already prevents them
        }
    }

    private void scheduleComputerMove() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                SwingUtilities.invokeLater(() -> makeComputerMove());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void makeComputerMove() {
        try {
            Player computer = game.getPlayers().get((game.getCurrentPlayerIndex()));

            TreeSet<ChessPair<Position, Piece>> allPieces = game.getBoard().getPieces();
            List<ChessPair<Position, Piece>> computerPieces = new   ArrayList<>();

            for (ChessPair<Position, Piece> pair : allPieces) {
                if (pair.getValue().getColor() == computer.getColor()) {
                    computerPieces.add(pair);
                }
            }

            if (computerPieces.isEmpty()) {
                statusLabel.setText("No computer pieces found!");
                statusLabel.setForeground(ACCENT_RED);
                return;
            }

            Random random = new Random();
            boolean moveMade = false;

            while (!moveMade && !computerPieces.isEmpty()) {
                int randomIndex = random.nextInt(computerPieces.size());
                ChessPair<Position, Piece> selectedPair = computerPieces.get(randomIndex);
                Position from = selectedPair.getKey();

                try {
                    List<Position> moves = game.getBoard().getLegalMoves(from, computer.getColor());

                    if (!moves.isEmpty()) {
                        Position to = moves.get(random.nextInt(moves.size()));

                        Piece captured = game.getBoard().getPieceAt(to);
                        computer.makeMove(from, to, game.getBoard());
                        game.addMove(computer, from, to, captured);

                        if (captured != null) {
                            game.notifyPieceCaptured(captured);
                        }

                        updateBoard();
                        updateMoveHistory();
                        moveMade = true;

                        if (checkGameEnd()) {
                            return;
                        }

                        game.switchPlayer();
                        game.registerBoardState();
                        game.notifyPlayerSwitch(game.getCurrentPlayer());
                        updateStatus();
                    } else {
                        computerPieces.remove(randomIndex);
                    }
                } catch (InvalidMoveException e) {
                    computerPieces.remove(randomIndex);
                }
            }

            if (!moveMade) {
                checkGameEnd();
            }
        } catch (Exception ex) {
            statusLabel.setText("Computer error: " + ex.getMessage());
            statusLabel.setForeground(ACCENT_RED);
            ex.printStackTrace();
        }
    }

    private boolean checkGameEnd() {
        try {
            if (game.checkForCheckMate()) {
                Player winner = game.getPlayers().get((game.getCurrentPlayerIndex() + 1) % 2);
                boolean humanWon = winner.getColor() == humanPlayer.getColor();

                GameEndScoringStrategy endScoring = new GameEndScoringStrategy();
                int bonus = endScoring.getGameEndBonus(humanWon ?
                    GameEndScoringStrategy.GameResult.CHECKMATE_WIN :
                    GameEndScoringStrategy.GameResult.CHECKMATE_LOSS);

                updateUserScore(bonus);

                showCheckmateDialog(humanWon, bonus);

                parentFrame.getEndGamePanel().setGameResult(
                    humanWon ? "VICTORY!" : "DEFEAT",
                    humanPlayer.getPoints(),
                    bonus,
                    parentFrame.getMainApp().getCurrentUser().getPoints()
                );

                game.notifyCheckmate(winner);
                game.notifyGameEnd("Checkmate");

                removeGameFromUser();
                parentFrame.showPanel(ChessGUI.END_GAME_PANEL);
                return true;
            }

            if (game.checkForStalemate()) {
                GameEndScoringStrategy endScoring = new GameEndScoringStrategy();
                int bonus = endScoring.getGameEndBonus(GameEndScoringStrategy.GameResult.DRAW);
                updateUserScore(bonus);

                JOptionPane.showMessageDialog(
                    this,
                    "STALEMATE - It's a draw!\n\nBonus: +150 points",
                    "Draw",
                    JOptionPane.INFORMATION_MESSAGE
                );

                parentFrame.getEndGamePanel().setGameResult(
                    "DRAW",
                    humanPlayer.getPoints(),
                    bonus,
                    parentFrame.getMainApp().getCurrentUser().getPoints()
                );

                game.notifyGameEnd("Draw");

                removeGameFromUser();
                parentFrame.showPanel(ChessGUI.END_GAME_PANEL);
                return true;
            }
        } catch (InvalidMoveException e) {
        }

        return false;
    }

    private void handleResign() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Resign Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_CARD);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        mainPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Resign Game?");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        JLabel messageLabel = new JLabel("Are you sure you want to resign?");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        JLabel penaltyLabel = new JLabel("You will lose 150 points");
        penaltyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        penaltyLabel.setForeground(ACCENT_RED);
        penaltyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(penaltyLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG_CARD);

        JButton yesButton = createModernButton("Yes, Resign", ACCENT_RED);
        yesButton.setPreferredSize(new Dimension(140, 40));
        yesButton.addActionListener(e -> {
            dialog.dispose();
            GameEndScoringStrategy endScoring = new GameEndScoringStrategy();
            int penalty = endScoring.getGameEndBonus(GameEndScoringStrategy.GameResult.RESIGN);
            updateUserScore(penalty);

            parentFrame.getEndGamePanel().setGameResult(
                    "RESIGNED",
                    humanPlayer.getPoints(),
                    penalty,
                    parentFrame.getMainApp().getCurrentUser().getPoints()
            );

            game.notifyGameEnd("Resign");
            removeGameFromUser();
            parentFrame.showPanel(ChessGUI.END_GAME_PANEL);
        });

        JButton noButton = createModernButton("Cancel", new Color(113, 128, 150));
        noButton.setPreferredSize(new Dimension(140, 40));
        noButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);

        dialog.setSize(400, 230);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void handleSaveAndExit() {
        parentFrame.getMainApp().write();
        parentFrame.showMainMenu();
    }

    private void handleBackToMenu() {
        if (game.getMoves().isEmpty()) {
            removeGameFromUser();
            parentFrame.showMainMenu();
        } else {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Game in progress. Save before exiting?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                parentFrame.getMainApp().write();
                parentFrame.showMainMenu();
            } else if (confirm == JOptionPane.NO_OPTION) {
                parentFrame.showMainMenu();
            }
        }
    }

    private void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = boardPositionToGamePosition(row, col);
                Piece piece = game.getBoard().getPieceAt(pos);

                JButton square = boardButtons[row][col];
                String symbol = piece == null ? "" : getPieceSymbol(piece);

                square.setText(symbol);

                if ((row + col) % 2 == 0) {
                    square.setBackground(BOARD_LIGHT);
                } else {
                    square.setBackground(BOARD_DARK);
                }
            }
        }
    }

    private void highlightPossibleMoves() {
        updateBoard();

        if (possibleMoves != null) {
            for (Position pos : possibleMoves) {
                int[] coords = gamePositionToBoardPosition(pos);
                boardButtons[coords[0]][coords[1]].setBackground(HIGHLIGHT_MOVE);
            }
        }

        if (selectedPosition != null) {
            int[] coords = gamePositionToBoardPosition(selectedPosition);
            boardButtons[coords[0]][coords[1]].setBackground(HIGHLIGHT_SELECT);
        }
    }

    private void updateMoveHistory() {
        StringBuilder sb = new StringBuilder();
        List<Move> moves = game.getMoves();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            if (i % 2 == 0) {
                sb.append(String.format("%d. ", (i / 2) + 1));
            }

            Piece piece = game.getBoard().getPieceAt(move.getTo());
            String pieceSymbol = piece != null ? String.valueOf(piece.type()) : "?";

            sb.append(String.format("%s%d-%s%d ",
                    move.getFrom().getX(), move.getFrom().getY(),
                    move.getTo().getX(), move.getTo().getY()));

            if (move.getCapturedPiece() != null) {
                sb.append("✖ ");
            }

            if (i % 2 == 1) {
                sb.append("\n");
            }
        }

        moveHistoryArea.setText(sb.toString());
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
    }

    private void updateStatus() {
        if (isHumanTurn()) {
            turnLabel.setText("Your turn");
            turnLabel.setForeground(ACCENT_GREEN);
            if (!statusLabel.getText().startsWith("Welcome")) {
                User currentUser = parentFrame.getMainApp().getCurrentUser();
                if (currentUser != null) {
                    statusLabel.setText("Welcome, " + currentUser.getEmail());
                }
            }
            statusLabel.setForeground(TEXT_PRIMARY);
        } else {
            turnLabel.setText("Computer's turn");
            turnLabel.setForeground(ACCENT_RED);
        }
    }

    private void updateScore() {
        scoreLabel.setText(String.format("Score: %d pts", humanPlayer.getPoints()));
    }

    private void updateUserScore(int bonus) {
        User currentUser = parentFrame.getMainApp().getCurrentUser();
        if (currentUser != null) {
            int gamePoints = humanPlayer.getPoints() + bonus;
            currentUser.setPoints(currentUser.getPoints() + gamePoints);
        }
    }

    private void removeGameFromUser() {
        User currentUser = parentFrame.getMainApp().getCurrentUser();
        if (currentUser != null) {
            currentUser.removeGame(game);
        }
    }

    private boolean isHumanTurn() {
        return game != null && game.getCurrentPlayer().getColor() == humanPlayer.getColor();
    }

    private Position boardPositionToGamePosition(int row, int col) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        try {
            return new Position(file, rank);
        } catch (InvalidCommandException e) {
            throw new RuntimeException("Invalid position created from board coordinates", e);
        }
    }

    private int[] gamePositionToBoardPosition(Position pos) {
        int row = 8 - pos.getY();
        int col = pos.getX() - 'A';
        return new int[]{row, col};
    }

    private String getPieceSymbol(Piece piece) {
        boolean isWhite = piece.getColor() == Colors.WHITE;
        char type = piece.type();

        String symbol = switch (type) {
            case 'K' -> isWhite ? "\u2654" : "\u265A";
            case 'Q' -> isWhite ? "\u2655" : "\u265B";
            case 'R' -> isWhite ? "\u2656" : "\u265C";
            case 'B' -> isWhite ? "\u2657" : "\u265D";
            case 'N' -> isWhite ? "\u2658" : "\u265E";
            case 'P' -> isWhite ? "\u2659" : "\u265F";
            default -> "";
        };

        return "<html><body style='font-size:32px'>" + symbol + "</body></html>";
    }

    private String getPieceSymbolPlain(Piece piece) {
        boolean isWhite = piece.getColor() == Colors.WHITE;
        char type = piece.type();

        return switch (type) {
            case 'K' -> isWhite ? "\u2654" : "\u265A";
            case 'Q' -> isWhite ? "\u2655" : "\u265B";
            case 'R' -> isWhite ? "\u2656" : "\u265C";
            case 'B' -> isWhite ? "\u2657" : "\u265D";
            case 'N' -> isWhite ? "\u2658" : "\u265E";
            case 'P' -> isWhite ? "\u2659" : "\u265F";
            default -> "";
        };
    }

    private String showPromotionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pawn Promotion", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_CARD);

        final String[] result = {"QUEEN"};

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        mainPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Promote pawn to:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setBackground(BG_CARD);
        buttonPanel.setMaximumSize(new Dimension(500, 100));

        boolean isWhite = humanPlayer.getColor() == Colors.WHITE;

        String queenSymbol = isWhite ? "\u2655" : "\u265B";
        JButton queenBtn = createPromotionButton(queenSymbol, "Queen");
        queenBtn.addActionListener(e -> {
            result[0] = "QUEEN";
            dialog.dispose();
        });

        String rookSymbol = isWhite ? "\u2656" : "\u265C";
        JButton rookBtn = createPromotionButton(rookSymbol, "Rook");
        rookBtn.addActionListener(e -> {
            result[0] = "ROOK";
            dialog.dispose();
        });

        String bishopSymbol = isWhite ? "\u2657" : "\u265D";
        JButton bishopBtn = createPromotionButton(bishopSymbol, "Bishop");
        bishopBtn.addActionListener(e -> {
            result[0] = "BISHOP";
            dialog.dispose();
        });

        String knightSymbol = isWhite ? "\u2658" : "\u265E";
        JButton knightBtn = createPromotionButton(knightSymbol, "Knight");
        knightBtn.addActionListener(e -> {
            result[0] = "KNIGHT";
            dialog.dispose();
        });

        buttonPanel.add(queenBtn);
        buttonPanel.add(rookBtn);
        buttonPanel.add(bishopBtn);
        buttonPanel.add(knightBtn);

        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return result[0];
    }

    private JButton createPromotionButton(String symbol, String name) {
        JButton button = new JButton("<html><center><b style='font-size:24px'>" + symbol + "</b><br>" + name + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 100));
        button.setBackground(BG_DARK);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(74, 85, 104), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HIGHLIGHT_SELECT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BG_DARK);
            }
        });

        return button;
    }

    private void showCheckmateDialog(boolean humanWon, int bonus) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Checkmate", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BG_CARD);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_CARD);
        mainPanel.setBorder(new EmptyBorder(40, 50, 30, 50));

        JLabel titleLabel = new JLabel(humanWon ? "CHECKMATE! You Won!" : "CHECKMATE! You Lost!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(humanWon ? ACCENT_GREEN : ACCENT_RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(25));

        String symbol = humanWon ? "\uD83C\uDFC6" : "\u2620";
        JLabel symbolLabel = new JLabel("<html><center><span style='font-size:64px'>" + symbol + "</span></center></html>");
        symbolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(symbolLabel);

        mainPanel.add(Box.createVerticalStrut(25));

        String bonusText = bonus > 0 ? "+" + bonus + " points" : bonus + " points";
        JLabel bonusLabel = new JLabel(bonusText);
        bonusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        bonusLabel.setForeground(bonus > 0 ? new Color(255, 215, 0) : ACCENT_RED);
        bonusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(bonusLabel);

        mainPanel.add(Box.createVerticalStrut(30));

        JButton okButton = createModernButton("OK", humanWon ? ACCENT_GREEN : ACCENT_BLUE);
        okButton.setPreferredSize(new Dimension(180, 50));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_CARD);
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    @Override
    public void onMoveMade(Move move) {
    }

    @Override
    public void onPieceCaptured(Piece piece) {
        updateScore();
        updateCapturedPieces();
    }

    private void updateCapturedPieces() {
        if (game == null) return;

        StringBuilder whiteCaptured = new StringBuilder("<html><body style='font-size:20px'>");
        StringBuilder blackCaptured = new StringBuilder("<html><body style='font-size:20px'>");

        for (Move move : game.getMoves()) {
            Piece captured = move.getCapturedPiece();
            if (captured != null) {
                String symbol = getPieceSymbolPlain(captured);
                if (captured.getColor() == Colors.WHITE) {
                    whiteCaptured.append(symbol).append(" ");
                } else {
                    blackCaptured.append(symbol).append(" ");
                }
            }
        }

        whiteCaptured.append("</body></html>");
        blackCaptured.append("</body></html>");

        capturedWhiteArea.setText(whiteCaptured.toString());
        capturedBlackArea.setText(blackCaptured.toString());
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
        updateStatus();
    }

    @Override
    public void onCheck(Player playerInCheck) {
        if (playerInCheck == humanPlayer) {
            statusLabel.setText("CHECK! You are in check!");
            statusLabel.setForeground(ACCENT_RED);
        }
    }

    @Override
    public void onCheckmate(Player winner) {
    }

    @Override
    public void onGameEnd(String result) {
    }
}
