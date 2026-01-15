package com.miguel.gestionstock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.entity.StockMovement;
import com.miguel.gestionstock.util.HibernateUtil;

class StockMovementServiceTest {

    private final ProductService productService = new ProductService();
    private final StockMovementService stockMovementService = new StockMovementService();

    private Long productId;

    @BeforeEach
    void setUp() {
        Product product = new Product("Produit Mouvement", "Pour tests de mouvements", 10,
                new BigDecimal("50.00"), "TestMouv");
        productService.createProduct(product);
        this.productId = product.getId();
    }

    @AfterEach
    void tearDown() {
        if (productId == null) {
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            session.createQuery("delete from StockMovement m where m.product.id = :productId")
                    .setParameter("productId", productId)
                    .executeUpdate();

            Product p = session.get(Product.class, productId);
            if (p != null) {
                session.delete(p);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
    }

    @Test
    void registerMovement_entryIncreasesQuantity() {
        stockMovementService.registerMovement(productId, "ENTREE", 5, "Réception test");

        Product updated = productService.getProductById(productId);
        assertEquals(15, updated.getQuantity(),
                "Une entrée de 5 unités devrait augmenter la quantité de 10 à 15.");
    }

    @Test
    void registerMovement_exitDecreasesQuantity() {
        stockMovementService.registerMovement(productId, "SORTIE", 4, "Sortie test");

        Product updated = productService.getProductById(productId);
        assertEquals(6, updated.getQuantity(),
                "Une sortie de 4 unités devrait diminuer la quantité de 10 à 6.");
    }

    @Test
    void registerMovement_exitTooManyUnits_throwsIllegalArgumentExceptionAndDoesNotChangeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> stockMovementService.registerMovement(productId, "SORTIE", 100, "Sortie trop grande"),
                "Une sortie rendant le stock négatif devrait lever une IllegalArgumentException.");

        Product updated = productService.getProductById(productId);
        assertEquals(10, updated.getQuantity(),
                "Après une tentative de sortie invalide, la quantité doit rester inchangée.");
    }
}
