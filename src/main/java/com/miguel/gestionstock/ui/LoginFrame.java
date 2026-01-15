package com.miguel.gestionstock.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

import com.miguel.gestionstock.entity.UserAccount;
import com.miguel.gestionstock.service.UserService;

public class LoginFrame extends JFrame {

    private final UserService userService = new UserService();

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super("Connexion - Gestion de Stock");
        initializeFrame();
        initializeContent();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(248, 250, 252));

        URL iconUrl = getClass().getResource("/logo/logo.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void initializeContent() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 64, 175));

        JLabel titleLabel = new JLabel("  Connexion à la gestion de stock");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(241, 245, 249));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nom d'utilisateur :"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Mot de passe :"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(241, 245, 249));

        JButton loginButton = new JButton("Se connecter");
        JButton registerButton = new JButton("S'inscrire");

        loginButton.addActionListener(e -> onLogin());
        registerButton.addActionListener(e -> onOpenRegister());

        stylePrimaryButton(loginButton);
        styleSecondaryButton(registerButton);

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(226, 232, 240));
        button.setForeground(new Color(30, 41, 59));
        button.setFocusPainted(false);
    }

    private void onLogin() {
        String username = usernameField.getText().trim();
        char[] pwd = passwordField.getPassword();
        String password = new String(pwd);

        try {
            UserAccount user = userService.authenticate(username, password);
            Arrays.fill(pwd, '\0');

            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur interne lors de la connexion.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onOpenRegister() {
        RegisterDialog dialog = new RegisterDialog(this, userService);
        dialog.setVisible(true);
        UserAccount created = dialog.getCreatedUser();
        if (created != null) {
            usernameField.setText(created.getUsername());
            passwordField.requestFocusInWindow();
        }
    }
}
