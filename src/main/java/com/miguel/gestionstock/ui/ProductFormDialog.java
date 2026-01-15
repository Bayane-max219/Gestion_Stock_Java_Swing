package com.miguel.gestionstock.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.miguel.gestionstock.entity.Product;

public class ProductFormDialog extends JDialog {

    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField quantityField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;

    private boolean confirmed = false;
    private Product product;

    public ProductFormDialog(JFrame parent, Product product) {
        super(parent, true);
        this.product = product;
        setTitle(product == null ? "Ajouter un produit" : "Modifier un produit");
        initializeComponents();
        if (product != null) {
            fillForm(product);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nom :"), gbc);
        nameField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Description :"), gbc);
        descriptionField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Quantité :"), gbc);
        quantityField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Prix :"), gbc);
        priceField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Catégorie :"), gbc);
        categoryCombo = new JComboBox<>(new String[] { "Informatique", "Alimentation", "Vêtements", "Autre" });
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fillForm(Product product) {
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        quantityField.setText(String.valueOf(product.getQuantity()));
        if (product.getPrice() != null) {
            priceField.setText(product.getPrice().toPlainString());
        }
        if (product.getCategory() != null) {
            categoryCombo.setSelectedItem(product.getCategory());
        }
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String priceText = priceField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom du produit est obligatoire.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La quantité doit être un entier.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Le prix doit être un nombre valide.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (product == null) {
            product = new Product();
        }

        product.setName(name);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setCategory(category);

        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Product getProduct() {
        return product;
    }
}
