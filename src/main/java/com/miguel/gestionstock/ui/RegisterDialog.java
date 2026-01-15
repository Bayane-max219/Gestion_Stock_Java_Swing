package com.miguel.gestionstock.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.miguel.gestionstock.entity.UserAccount;
import com.miguel.gestionstock.service.UserService;

public class RegisterDialog extends JDialog {

    private final UserService userService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;

    private UserAccount createdUser;

    public RegisterDialog(JFrame parent, UserService userService) {
        super(parent, "Inscription", true);
        this.userService = userService;
        initializeComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
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
        formPanel.add(new JLabel("Nom complet :"), gbc);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Mot de passe :"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Confirmer le mot de passe :"), gbc);
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerButton = new JButton("S'inscrire");
        JButton cancelButton = new JButton("Annuler");

        registerButton.addActionListener(e -> onRegister());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        char[] pwd = passwordField.getPassword();
        char[] confirm = confirmPasswordField.getPassword();

        String password = new String(pwd);
        String confirmPassword = new String(confirm);

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserAccount user = userService.register(username, password, fullName);
            this.createdUser = user;
            JOptionPane.showMessageDialog(this, "Compte créé avec succès. Vous pouvez maintenant vous connecter.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur d'inscription",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur interne lors de l'inscription.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public UserAccount getCreatedUser() {
        return createdUser;
    }
}
