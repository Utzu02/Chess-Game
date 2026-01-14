package gui;

import main.Main;

import javax.swing.*;
import java.awt.*;

public class ChessGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String LOGIN_PANEL = "LOGIN";
    public static final String MENU_PANEL = "MENU";
    public static final String GAME_PANEL = "GAME";
    public static final String END_GAME_PANEL = "END_GAME";

    private Main mainApp;
    private LoginPanel loginPanel;
    private MainMenuPanel menuPanel;
    private GamePanel gamePanel;
    private EndGamePanel endGamePanel;

    public ChessGUI() {
        mainApp = Main.getInstance();

        setTitle("Chess Master - POO 2025");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        menuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel(this);
        endGamePanel = new EndGamePanel(this);

        mainPanel.add(loginPanel, LOGIN_PANEL);
        mainPanel.add(menuPanel, MENU_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        mainPanel.add(endGamePanel, END_GAME_PANEL);

        add(mainPanel);

        showPanel(LOGIN_PANEL);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public Main getMainApp() {
        return mainApp;
    }

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public MainMenuPanel getMenuPanel() {
        return menuPanel;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public EndGamePanel getEndGamePanel() {
        return endGamePanel;
    }

    public JPanel getPanel(String panelName) {
        switch (panelName) {
            case LOGIN_PANEL:
                return loginPanel;
            case MENU_PANEL:
                return menuPanel;
            case GAME_PANEL:
                return gamePanel;
            case END_GAME_PANEL:
                return endGamePanel;
            default:
                return null;
        }
    }

    public void showMainMenu() {
        menuPanel.refreshUserInfo();
        showPanel(MENU_PANEL);
    }

    public void showEndGame() {
        showPanel(END_GAME_PANEL);
    }

    public static void launchGUI() {
        SwingUtilities.invokeLater(() -> {
            ChessGUI gui = new ChessGUI();
            gui.setVisible(true);
        });
    }
}
