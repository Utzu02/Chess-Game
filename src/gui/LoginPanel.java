package gui;

import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private ChessGUI parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginButton;
    private JButton createAccountButton;

    private static final Color BG_DARK = new Color(26, 32, 44);
    private static final Color BG_CARD = new Color(45, 55, 72);
    private static final Color ACCENT_BLUE = new Color(66, 153, 225);
    private static final Color ACCENT_GREEN = new Color(72, 187, 120);
    private static final Color TEXT_PRIMARY = new Color(237, 242, 247);
    private static final Color TEXT_SECONDARY = new Color(160, 174, 192);

    public LoginPanel(ChessGUI parent) {
        this.parentFrame = parent;
        setLayout(new GridBagLayout());
        setBackground(BG_DARK);

        initComponents();
    }

    private void initComponents() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(BG_CARD);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));

        JLabel titleLabel = new JLabel("Chess Master");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        cardPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Welcome back! Please login to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(subtitleLabel);

        cardPanel.add(Box.createVerticalStrut(30));

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        emailLabel.setForeground(TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(emailLabel);

        cardPanel.add(Box.createVerticalStrut(8));

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(400, 40));
        emailField.setPreferredSize(new Dimension(400, 40));
        emailField.setBackground(new Color(26, 32, 44));
        emailField.setForeground(TEXT_PRIMARY);
        emailField.setCaretColor(TEXT_PRIMARY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(emailField);

        cardPanel.add(Box.createVerticalStrut(20));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passwordLabel.setForeground(TEXT_SECONDARY);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(passwordLabel);

        cardPanel.add(Box.createVerticalStrut(8));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(400, 40));
        passwordField.setPreferredSize(new Dimension(400, 40));
        passwordField.setBackground(new Color(26, 32, 44));
        passwordField.setForeground(TEXT_PRIMARY);
        passwordField.setCaretColor(TEXT_PRIMARY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(passwordField);

        cardPanel.add(Box.createVerticalStrut(10));

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(252, 129, 129));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(errorLabel);

        cardPanel.add(Box.createVerticalStrut(20));

        loginButton = createStyledButton("Sign In", ACCENT_BLUE);
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(loginButton);

        cardPanel.add(Box.createVerticalStrut(12));

        createAccountButton = createStyledButton("Create New Account", ACCENT_GREEN);
        createAccountButton.addActionListener(e -> handleCreateAccount());
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(createAccountButton);

        add(cardPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setMaximumSize(new Dimension(400, 45));
        button.setPreferredSize(new Dimension(400, 45));
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

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        try {
            User user = parentFrame.getMainApp().login(email, password);
            if (user != null) {
                errorLabel.setText(" ");
                parentFrame.getMenuPanel().refreshUserInfo();
                parentFrame.showPanel(ChessGUI.MENU_PANEL);
            } else {
                showError("Invalid email or password");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void handleCreateAccount() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        try {
            User user = parentFrame.getMainApp().newAccount(email, password);
            if (user != null) {
                errorLabel.setForeground(ACCENT_GREEN);
                errorLabel.setText("Account created! Please sign in.");
                passwordField.setText("");
            } else {
                showError("Account already exists");
            }
        } catch (Exception e) {
            showError("Account creation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setForeground(new Color(252, 129, 129));
        errorLabel.setText(message);
    }

    public void reset() {
        emailField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}
