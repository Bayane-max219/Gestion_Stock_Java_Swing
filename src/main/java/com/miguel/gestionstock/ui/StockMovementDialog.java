package com.miguel.gestionstock.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.entity.StockMovement;
import com.miguel.gestionstock.service.StockMovementService;

public class StockMovementDialog extends JDialog {

    private final Product product;
    private final StockMovementService stockMovementService;

    private JLabel productLabel;
    private JComboBox<String> typeCombo;
    private JTextField quantityField;
    private JTextField noteField;
    private JTable movementTable;

    public StockMovementDialog(JFrame parent, Product product, StockMovementService stockMovementService) {
        super(parent, true);
        this.product = product;
        this.stockMovementService = stockMovementService;

        setTitle("Mouvements de stock - " + product.getName());
        initializeComponents();
        loadMovements();
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
        formPanel.add(new JLabel("Produit :"), gbc);
        productLabel = new JLabel(product.getName());
        gbc.gridx = 1;
        formPanel.add(productLabel, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Type :"), gbc);
        typeCombo = new JComboBox<>(new String[] { "Entrée", "Sortie" });
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

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
        formPanel.add(new JLabel("Note :"), gbc);
        noteField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(noteField, gbc);

        add(formPanel, BorderLayout.NORTH);

        movementTable = new JTable(new DefaultTableModel(new Object[][] {},
                new String[] { "Date", "Type", "Quantité", "Note" }));
        JScrollPane scrollPane = new JScrollPane(movementTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Enregistrer");
        JButton closeButton = new JButton("Fermer");

        saveButton.addActionListener(e -> onSave());
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMovements() {
        List<StockMovement> movements = stockMovementService.getMovementsForProduct(product.getId());
        DefaultTableModel model = (DefaultTableModel) movementTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (StockMovement m : movements) {
            String typeLabel = "ENTREE".equals(m.getMovementType()) ? "Entrée" : "Sortie";
            model.addRow(new Object[] {
                    m.getMovementDate() != null ? sdf.format(m.getMovementDate()) : "",
                    typeLabel,
                    m.getQuantity(),
                    m.getNote() });
        }
    }

    private void onSave() {
        String typeLabel = (String) typeCombo.getSelectedItem();
        String type = "Entrée".equals(typeLabel) ? "ENTREE" : "SORTIE";
        String quantityText = quantityField.getText().trim();
        String note = noteField.getText().trim();

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La quantité doit être un entier.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            stockMovementService.registerMovement(product.getId(), type, quantity, note);
            loadMovements();
            quantityField.setText("");
            noteField.setText("");
            JOptionPane.showMessageDialog(this, "Mouvement enregistré avec succès.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du mouvement.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
