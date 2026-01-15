package com.miguel.gestionstock.service;

import java.math.BigDecimal;
import java.util.List;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository = new ProductRepository();

    private static final int DEFAULT_OUT_OF_STOCK_THRESHOLD = 5;

    public int getOutOfStockThreshold() {
        return DEFAULT_OUT_OF_STOCK_THRESHOLD;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String nameFilter, String categoryFilter, String sortBy) {
        return productRepository.search(nameFilter, categoryFilter, sortBy);
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStock(DEFAULT_OUT_OF_STOCK_THRESHOLD);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void createProduct(Product product) {
        validateProduct(product);
        productRepository.saveOrUpdate(product);
    }

    public void updateProduct(Product product) {
        if (product.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du produit est manquant pour la mise à jour.");
        }
        validateProduct(product);
        productRepository.saveOrUpdate(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire.");
        }

        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("La quantité doit être un entier positif.");
        }

        BigDecimal price = product.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix doit être un nombre positif.");
        }
    }
}
