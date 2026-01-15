package com.miguel.gestionstock.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.entity.UserAccount;
import com.miguel.gestionstock.service.ProductService;
import com.miguel.gestionstock.service.StockMovementService;

public class MainFrame extends JFrame {

    private JTable productTable;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> sortCombo;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton reportsButton;

    private UserAccount currentUser;

    private final ProductService productService = new ProductService();
    private final StockMovementService stockMovementService = new StockMovementService();

    public MainFrame() {
        this(null);
    }

    public MainFrame(UserAccount currentUser) {
        super("Gestion de Stock");
        this.currentUser = currentUser;
        initializeFrame();
        initializeMenuBar();
        initializeContent();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 247, 250));

        URL iconUrl = getClass().getResource("/logo/logo.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void initializeMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 41, 59));
        menuBar.setForeground(Color.WHITE);

        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem exportItem = new JMenuItem("Exporter produits (CSV)");
        JMenuItem exitItem = new JMenuItem("Quitter");
        exportItem.addActionListener(e -> onExportProductsCsv());
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu productMenu = new JMenu("Produits");
        JMenuItem addProductItem = new JMenuItem("Ajouter");
        JMenuItem editProductItem = new JMenuItem("Modifier");
        JMenuItem deleteProductItem = new JMenuItem("Supprimer");
        JMenuItem movementsItem = new JMenuItem("Mouvements de stock");

        addProductItem.addActionListener(e -> onAddProduct());
        editProductItem.addActionListener(e -> onEditProduct());
        deleteProductItem.addActionListener(e -> onDeleteProduct());
        movementsItem.addActionListener(e -> onManageStockMovements());

        productMenu.add(addProductItem);
        productMenu.add(editProductItem);
        productMenu.add(deleteProductItem);
        productMenu.add(movementsItem);
        menuBar.add(productMenu);

        JMenu reportsMenu = new JMenu("Rapports");
        JMenuItem stockReportItem = new JMenuItem("État du stock");
        JMenuItem categoryReportItem = new JMenuItem("Par catégorie");
        JMenuItem outOfStockItem = new JMenuItem("Ruptures de stock");

        stockReportItem.addActionListener(e -> onStockReport());
        categoryReportItem.addActionListener(e -> onCategoryReport());
        outOfStockItem.addActionListener(e -> onOutOfStockReport());

        reportsMenu.add(stockReportItem);
        reportsMenu.add(categoryReportItem);
        reportsMenu.add(outOfStockItem);
        menuBar.add(reportsMenu);

        JMenu userMenu = new JMenu("Utilisateur");
        JMenuItem logoutItem = new JMenuItem("Se déconnecter");
        logoutItem.addActionListener(e -> onLogout());
        userMenu.add(logoutItem);
        menuBar.add(userMenu);

        setJMenuBar(menuBar);
    }

    private void initializeContent() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 64, 175));

        JLabel titleLabel = new JLabel("  Gestion de stock - Tableau des produits");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(241, 245, 249));

        JLabel searchLabel = new JLabel("Recherche :");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Rechercher");

        JLabel categoryLabel = new JLabel("Catégorie :");
        categoryFilter = new JComboBox<>(new String[] {"Toutes", "Informatique", "Alimentation", "Vêtements"});

        JLabel sortLabel = new JLabel("Trier par :");
        sortCombo = new JComboBox<>(new String[] {"Nom", "Prix", "Quantité"});

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(categoryLabel);
        topPanel.add(categoryFilter);
        topPanel.add(sortLabel);
        topPanel.add(sortCombo);

        searchButton.addActionListener(e -> onSearch());
        searchField.addActionListener(e -> onSearch());

        headerPanel.add(topPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        productTable = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"ID", "Nom", "Description", "Quantité", "Prix", "Catégorie"});
        productTable.setModel(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(24);
        productTable.getTableHeader().setBackground(new Color(30, 64, 175));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(productTable.getTableHeader().getFont().deriveFont(Font.BOLD));

        productTable.setGridColor(new Color(226, 232, 240));
        productTable.setShowHorizontalLines(true);
        productTable.setShowVerticalLines(false);

        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(241, 245, 249));

        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        refreshButton = new JButton("Rafraîchir");
        reportsButton = new JButton("Rapports");

        stylePrimaryButton(addButton);
        styleSecondaryButton(editButton);
        styleDangerButton(deleteButton);
        styleSecondaryButton(refreshButton);
        styleSecondaryButton(reportsButton);

        addButton.addActionListener(e -> onAddProduct());
        editButton.addActionListener(e -> onEditProduct());
        deleteButton.addActionListener(e -> onDeleteProduct());
        refreshButton.addActionListener(e -> onRefresh());
        reportsButton.addActionListener(e -> onStockReport());

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(reportsButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadAllProducts();
        showLowStockAlertIfNeeded();
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMargin(new Insets(6, 14, 6, 14));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(226, 232, 240));
        button.setForeground(new Color(30, 41, 59));
        button.setFocusPainted(false);
        button.setMargin(new Insets(6, 14, 6, 14));
    }

    private void styleDangerButton(JButton button) {
        button.setBackground(new Color(220, 38, 38));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setMargin(new Insets(6, 14, 6, 14));
    }

    private void onAddProduct() {
        ProductFormDialog dialog;
        try {
            dialog = new ProductFormDialog(this, null);
        } catch (Throwable ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ouverture du formulaire d'ajout : " + ex.getClass().getSimpleName() +
                            (ex.getMessage() != null ? " - " + ex.getMessage() : ""),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                Product product = dialog.getProduct();
                productService.createProduct(product);
                loadAllProducts();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du produit.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à modifier.", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idValue = productTable.getValueAt(selectedRow, 0);
        if (idValue == null) {
            JOptionPane.showMessageDialog(this, "Le produit sélectionné n'a pas d'identifiant.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = (idValue instanceof Long) ? (Long) idValue : Long.valueOf(idValue.toString());
        Product existing = productService.getProductById(id);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Le produit n'existe plus en base. Rafraîchissez la liste.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            loadAllProducts();
            return;
        }

        ProductFormDialog dialog = new ProductFormDialog(this, existing);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                Product updated = dialog.getProduct();
                productService.updateProduct(updated);
                loadAllProducts();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur de validation",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du produit.", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer.", "Avertissement",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce produit ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Object idValue = productTable.getValueAt(selectedRow, 0);
        if (idValue == null) {
            JOptionPane.showMessageDialog(this, "Le produit sélectionné n'a pas d'identifiant.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = (idValue instanceof Long) ? (Long) idValue : Long.valueOf(idValue.toString());

        try {
            productService.deleteProduct(id);
            loadAllProducts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du produit.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onManageStockMovements() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit pour gérer ses mouvements.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idValue = productTable.getValueAt(selectedRow, 0);
        if (idValue == null) {
            JOptionPane.showMessageDialog(this, "Le produit sélectionné n'a pas d'identifiant.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long id = (idValue instanceof Long) ? (Long) idValue : Long.valueOf(idValue.toString());
        Product product = productService.getProductById(id);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Le produit n'existe plus en base. Rafraîchissez la liste.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            loadAllProducts();
            return;
        }

        StockMovementDialog dialog = new StockMovementDialog(this, product, stockMovementService);
        dialog.setVisible(true);

        // Après éventuels mouvements, recharger la liste pour mettre à jour la quantité
        loadAllProducts();
    }

    private void onRefresh() {
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        sortCombo.setSelectedIndex(0);
        loadAllProducts();
    }

    private void onStockReport() {
        List<Product> products = productService.getAllProducts();
        int totalQuantity = 0;
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Product p : products) {
            totalQuantity += p.getQuantity();
            if (p.getPrice() != null) {
                totalValue = totalValue.add(p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())));
            }
        }

        String message = "Nombre de produits : " + products.size() +
                "\nQuantité totale en stock : " + totalQuantity +
                "\nValeur totale du stock : " + totalValue;

        JOptionPane.showMessageDialog(this, message, "État du stock", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onOutOfStockReport() {
        List<Product> outOfStock = productService.getOutOfStockProducts();
        if (outOfStock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit en rupture ou sous le seuil.", "Ruptures de stock",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Product p : outOfStock) {
            sb.append("- ").append(p.getName()).append(" (quantité : ").append(p.getQuantity()).append(")\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Produits en rupture de stock",
                JOptionPane.WARNING_MESSAGE);
    }

    private void onCategoryReport() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit en base.", "Rapport par catégorie",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Integer> quantityByCategory = new HashMap<>();
        Map<String, BigDecimal> valueByCategory = new HashMap<>();

        for (Product p : products) {
            String category = (p.getCategory() == null || p.getCategory().trim().isEmpty())
                    ? "Sans catégorie"
                    : p.getCategory();

            int quantity = quantityByCategory.getOrDefault(category, 0) + p.getQuantity();
            quantityByCategory.put(category, quantity);

            if (p.getPrice() != null) {
                BigDecimal value = valueByCategory.getOrDefault(category, BigDecimal.ZERO)
                        .add(p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())));
                valueByCategory.put(category, value);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Rapport par catégorie :\n\n");
        for (Map.Entry<String, Integer> entry : quantityByCategory.entrySet()) {
            String category = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal value = valueByCategory.getOrDefault(category, BigDecimal.ZERO);
            sb.append("- ").append(category)
                    .append(" : quantité = ").append(quantity)
                    .append(", valeur = ").append(value)
                    .append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Rapport par catégorie",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Déconnexion",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        dispose();
        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
    }

    private void onSearch() {
        String nameFilter = searchField.getText();
        String category = (String) categoryFilter.getSelectedItem();
        String sortBy = (String) sortCombo.getSelectedItem();

        List<Product> products = productService.searchProducts(nameFilter, category, sortBy);
        updateTable(products);
    }

    private void loadAllProducts() {
        List<Product> products = productService.getAllProducts();
        updateTable(products);
    }

    private void updateTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);

        for (Product p : products) {
            model.addRow(new Object[] { p.getId(), p.getName(), p.getDescription(), p.getQuantity(), p.getPrice(),
                    p.getCategory() });
        }
    }

    private void showLowStockAlertIfNeeded() {
        List<Product> lowStock = productService.getOutOfStockProducts();
        if (lowStock.isEmpty()) {
            return;
        }

        int threshold = productService.getOutOfStockThreshold();
        StringBuilder sb = new StringBuilder();
        sb.append("Certains produits sont sous le seuil de stock (<= ")
                .append(threshold)
                .append(") :\n\n");

        for (Product p : lowStock) {
            sb.append("- ").append(p.getName())
                    .append(" (quantité : ").append(p.getQuantity()).append(")\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Alerte stock faible",
                JOptionPane.WARNING_MESSAGE);
    }

    private void onExportProductsCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("produits.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        List<Product> products = productService.getAllProducts();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id;nom;description;quantite;prix;categorie\n");
            for (Product p : products) {
                writer.write(nullSafeCsv(p.getId()));
                writer.write(";");
                writer.write(nullSafeCsv(p.getName()));
                writer.write(";");
                writer.write(nullSafeCsv(p.getDescription()));
                writer.write(";");
                writer.write(nullSafeCsv(p.getQuantity()));
                writer.write(";");
                writer.write(nullSafeCsv(p.getPrice()));
                writer.write(";");
                writer.write(nullSafeCsv(p.getCategory()));
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Export CSV terminé.", "Export",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'export CSV : " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nullSafeCsv(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString()
                .replace(";", ",")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
