package gui;

import main.Main;
import model.Colors;
import model.Game;
import model.Player;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenuPanel extends JPanel {
    private ChessGUI parentFrame;
    private JLabel pointsLabel;
    private JLabel gamesLabel;
    private JLabel welcomeLabel;

    public MainMenuPanel(ChessGUI parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52));

        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(40, 44, 52));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);

        pointsLabel = new JLabel("Points: 0");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        pointsLabel.setForeground(new Color(255, 215, 0));
        statsPanel.add(pointsLabel);

        gamesLabel = new JLabel("Active Games: 0");
        gamesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gamesLabel.setForeground(new Color(100, 181, 246));
        statsPanel.add(gamesLabel);

        topPanel.add(statsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(40, 44, 52));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JButton newGameButton = createMenuButton("New Game", new Color(76, 175, 80));
        newGameButton.addActionListener(e -> showNewGameDialog());
        centerPanel.add(newGameButton, gbc);

        gbc.gridy = 1;
        JButton continueButton = createMenuButton("Continue Game", new Color(33, 150, 243));
        continueButton.addActionListener(e -> showContinueGameDialog());
        centerPanel.add(continueButton, gbc);

        gbc.gridy = 2;
        JButton logoutButton = createMenuButton("Logout", new Color(255, 152, 0));
        logoutButton.addActionListener(e -> handleLogout());
        centerPanel.add(logoutButton, gbc);

        gbc.gridy = 3;
        JButton exitButton = createMenuButton("Exit", new Color(244, 67, 54));
        exitButton.addActionListener(e -> handleExit());
        centerPanel.add(exitButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

    public void showNewGameDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 55, 72));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 55, 72));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Start New Game");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(237, 242, 247));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(25));

        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(160, 174, 192));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);

        mainPanel.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(350, 40));
        nameField.setPreferredSize(new Dimension(350, 40));
        nameField.setBackground(new Color(26, 32, 44));
        nameField.setForeground(new Color(237, 242, 247));
        nameField.setCaretColor(new Color(237, 242, 247));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameField);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel colorLabel = new JLabel("Choose Color:");
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        colorLabel.setForeground(new Color(160, 174, 192));
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(colorLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        colorPanel.setBackground(new Color(45, 55, 72));
        colorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup colorGroup = new ButtonGroup();

        JRadioButton whiteButton = new JRadioButton("White");
        whiteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        whiteButton.setForeground(new Color(237, 242, 247));
        whiteButton.setBackground(new Color(45, 55, 72));
        whiteButton.setFocusPainted(false);
        whiteButton.setSelected(true);

        JRadioButton blackButton = new JRadioButton("Black");
        blackButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        blackButton.setForeground(new Color(237, 242, 247));
        blackButton.setBackground(new Color(45, 55, 72));
        blackButton.setFocusPainted(false);

        colorGroup.add(whiteButton);
        colorGroup.add(blackButton);
        colorPanel.add(whiteButton);
        colorPanel.add(blackButton);

        mainPanel.add(colorPanel);

        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(45, 55, 72));

        JButton startButton = createStyledButton("Start Game", new Color(72, 187, 120));
        startButton.setPreferredSize(new Dimension(140, 45));
        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                showErrorDialog("Please enter your name");
                return;
            }

            Colors playerColor = whiteButton.isSelected() ? Colors.WHITE : Colors.BLACK;
            dialog.dispose();
            startNewGame(playerName, playerColor);
        });

        JButton cancelButton = createStyledButton("Cancel", new Color(113, 128, 150));
        cancelButton.setPreferredSize(new Dimension(140, 45));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(450, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void startNewGame(String playerName, Colors playerColor) {
        Main mainApp = parentFrame.getMainApp();
        User currentUser = mainApp.getCurrentUser();

        int newId = mainApp.getGames().isEmpty() ? 1 : Collections.max(mainApp.getGames().keySet()) + 1;
        Game game = new Game(newId);

        Player humanPlayer = new Player(playerName, playerColor);
        Player computerPlayer = new Player("Computer", playerColor == Colors.WHITE ? Colors.BLACK : Colors.WHITE);

        List<Player> players = new ArrayList<>();
        if (playerColor == Colors.WHITE) {
            players.add(humanPlayer);
            players.add(computerPlayer);
        } else {
            players.add(computerPlayer);
            players.add(humanPlayer);
        }

        game.setPlayers(players);

        try {
            game.start(humanPlayer.getColor());

            mainApp.getGames().put(newId, game);

            if (currentUser != null) {
                currentUser.addGame(game);
            }

            parentFrame.getGamePanel().setGame(game, humanPlayer);
            parentFrame.showPanel(ChessGUI.GAME_PANEL);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting game: " + ex.getMessage());
        }
    }

    private String getCurrentUserEmail() {
        Main mainApp = parentFrame.getMainApp();
        User currentUser = mainApp.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        }
        return "";
    }

    private void showContinueGameDialog() {
        Main mainApp = parentFrame.getMainApp();
        User currentUser = mainApp.getCurrentUser();

        if (currentUser == null || currentUser.getActiveGames().isEmpty()) {
            showNoGamesDialog();
            return;
        }

        List<Game> games = currentUser.getActiveGames();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Continue Game", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 55, 72));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 55, 72));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Select a game to continue:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(237, 242, 247));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel gamesPanel = new JPanel();
        gamesPanel.setLayout(new BoxLayout(gamesPanel, BoxLayout.Y_AXIS));
        gamesPanel.setBackground(new Color(26, 32, 44));
        gamesPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        ButtonGroup gameGroup = new ButtonGroup();
        final JRadioButton[] selectedButton = {null};

        for (Game game : games) {
            JRadioButton radioButton = new JRadioButton(
                "Game #" + game.getId() + " - " + game.getMoves().size() + " moves"
            );
            radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            radioButton.setForeground(new Color(237, 242, 247));
            radioButton.setBackground(new Color(26, 32, 44));
            radioButton.setFocusPainted(false);
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (selectedButton[0] == null) {
                radioButton.setSelected(true);
                selectedButton[0] = radioButton;
            }

            radioButton.addActionListener(e -> selectedButton[0] = radioButton);
            gameGroup.add(radioButton);
            gamesPanel.add(radioButton);
            gamesPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(gamesPanel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(400, Math.min(200, games.size() * 40)));
        scrollPane.setMaximumSize(new Dimension(450, 250));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scrollPane);

        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(45, 55, 72));

        JButton okButton = createStyledButton("Continue", new Color(72, 187, 120));
        okButton.setPreferredSize(new Dimension(140, 45));
        okButton.addActionListener(e -> {
            if (selectedButton[0] != null) {
                String text = selectedButton[0].getText();
                int gameId = Integer.parseInt(text.split("#")[1].split(" ")[0]);

                Game selectedGame = games.stream()
                    .filter(g -> g.getId() == gameId)
                    .findFirst()
                    .orElse(null);

                if (selectedGame != null) {
                    Player humanPlayer = getHumanPlayer(selectedGame);
                    if (humanPlayer != null) {
                        try {
                            selectedGame.resume(humanPlayer.getColor());
                            parentFrame.getGamePanel().setGame(selectedGame, humanPlayer);
                            parentFrame.showPanel(ChessGUI.GAME_PANEL);
                            dialog.dispose();
                        } catch (Exception ex) {
                            showErrorDialog("Error resuming game: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        JButton cancelButton = createStyledButton("Cancel", new Color(113, 128, 150));
        cancelButton.setPreferredSize(new Dimension(140, 45));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showNoGamesDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "No Games", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 55, 72));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 55, 72));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel messageLabel = new JLabel("No games in progress");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(new Color(237, 242, 247));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JButton okButton = createStyledButton("OK", new Color(66, 153, 225));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 55, 72));
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showErrorDialog(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Error", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 55, 72));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 55, 72));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(245, 101, 101));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JButton okButton = createStyledButton("OK", new Color(245, 101, 101));
        okButton.setPreferredSize(new Dimension(120, 40));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 55, 72));
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
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

    private Player getHumanPlayer(Game game) {
        for (Player player : game.getPlayers()) {
            if (!player.isComputer()) {
                return player;
            }
        }
        return null;
    }

    private void handleLogout() {
        parentFrame.getMainApp().write();
        parentFrame.getLoginPanel().reset();
        parentFrame.showPanel(ChessGUI.LOGIN_PANEL);
    }

    private void handleExit() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirm Exit", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 55, 72));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(45, 55, 72));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Confirm Exit");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(237, 242, 247));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel messageLabel = new JLabel("Are you sure you want to exit?");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(160, 174, 192));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(25));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(45, 55, 72));

        JButton yesButton = createStyledButton("Yes", new Color(244, 67, 54));
        yesButton.setPreferredSize(new Dimension(120, 40));
        yesButton.addActionListener(e -> {
            dialog.dispose();
            try {
                parentFrame.getMainApp().write();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        JButton noButton = createStyledButton("No", new Color(113, 128, 150));
        noButton.setPreferredSize(new Dimension(120, 40));
        noButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public void refreshUserInfo() {
        Main mainApp = parentFrame.getMainApp();
        User currentUser = mainApp.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String name = email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
            welcomeLabel.setText("Welcome, " + name);
            pointsLabel.setText("Points: " + currentUser.getPoints());
            gamesLabel.setText("Active Games: " + currentUser.getActiveGames().size());
        }
    }
}
