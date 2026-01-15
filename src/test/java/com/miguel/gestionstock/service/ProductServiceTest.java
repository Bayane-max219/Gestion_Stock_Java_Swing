package com.miguel.gestionstock.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.miguel.gestionstock.entity.Product;

class ProductServiceTest {

    private final ProductService productService = new ProductService();

    @Test
    void createProduct_withValidData_persistsAndAssignsId() {
        Product product = new Product("Test Produit", "Produit pour test valide", 10,
                new BigDecimal("99.99"), "TestCategorie");

        productService.createProduct(product);

        assertNotNull(product.getId(), "L'identifiant doit être renseigné après la création du produit.");
    }

    @Test
    void createProduct_withoutName_throwsIllegalArgumentException() {
        Product product = new Product(null, "Sans nom", 5, new BigDecimal("10.00"), "Test");

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product),
                "La création d'un produit sans nom devrait lever une IllegalArgumentException.");
    }

    @Test
    void createProduct_withNegativeQuantity_throwsIllegalArgumentException() {
        Product product = new Product("Produit négatif", "Quantité négative", -1,
                new BigDecimal("10.00"), "Test");

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product),
                "La création d'un produit avec une quantité négative devrait lever une IllegalArgumentException.");
    }

    @Test
    void createProduct_withNegativePrice_throwsIllegalArgumentException() {
        Product product = new Product("Produit prix négatif", "Prix négatif", 5,
                new BigDecimal("-1.00"), "Test");

        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(product),
                "La création d'un produit avec un prix négatif devrait lever une IllegalArgumentException.");
    }
}
