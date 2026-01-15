package com.miguel.gestionstock.repository;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.miguel.gestionstock.entity.StockMovement;
import com.miguel.gestionstock.util.HibernateUtil;

public class StockMovementRepository {

    public List<StockMovement> findByProductId(Long productId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<StockMovement> query = session.createQuery(
                    "from StockMovement m where m.product.id = :productId order by m.movementDate desc",
                    StockMovement.class);
            query.setParameter("productId", productId);
            return query.list();
        }
    }
}
