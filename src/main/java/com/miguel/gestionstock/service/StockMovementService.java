package com.miguel.gestionstock.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.entity.StockMovement;
import com.miguel.gestionstock.repository.StockMovementRepository;
import com.miguel.gestionstock.util.HibernateUtil;

public class StockMovementService {

    private final StockMovementRepository stockMovementRepository = new StockMovementRepository();

    public void registerMovement(Long productId, String type, int quantity, String note) {
        if (productId == null) {
            throw new IllegalArgumentException("Produit non spécifié.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être un entier positif.");
        }

        String normalizedType;
        if ("ENTREE".equalsIgnoreCase(type) || "Entrée".equalsIgnoreCase(type)) {
            normalizedType = "ENTREE";
        } else if ("SORTIE".equalsIgnoreCase(type) || "Sortie".equalsIgnoreCase(type)) {
            normalizedType = "SORTIE";
        } else {
            throw new IllegalArgumentException("Type de mouvement invalide (Entrée ou Sortie).");
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Product product = session.get(Product.class, productId);
            if (product == null) {
                throw new IllegalArgumentException("Produit introuvable pour l'identifiant : " + productId);
            }

            int newQuantity = product.getQuantity();
            if ("ENTREE".equals(normalizedType)) {
                newQuantity += quantity;
            } else {
                newQuantity -= quantity;
            }

            if (newQuantity < 0) {
                throw new IllegalArgumentException("La quantité en stock ne peut pas devenir négative.");
            }

            product.setQuantity(newQuantity);

            StockMovement movement = new StockMovement();
            movement.setProduct(product);
            movement.setMovementType(normalizedType);
            movement.setQuantity(quantity);
            movement.setMovementDate(new Date());
            movement.setNote(note);

            session.save(movement);
            session.update(product);

            tx.commit();
        } catch (IllegalArgumentException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de l'enregistrement du mouvement de stock.", e);
        }
    }

    public List<StockMovement> getMovementsForProduct(Long productId) {
        return stockMovementRepository.findByProductId(productId);
    }
}
