package com.miguel.gestionstock.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.miguel.gestionstock.entity.Product;
import com.miguel.gestionstock.util.HibernateUtil;

public class ProductRepository {

    public void saveOrUpdate(Product product) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(product);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Product findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        }
    }

    public List<Product> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Product p order by p.name", Product.class).list();
        }
    }

    public List<Product> search(String nameFilter, String categoryFilter, String sortBy) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("from Product p where 1=1");
            Map<String, Object> params = new HashMap<>();

            if (nameFilter != null && !nameFilter.trim().isEmpty()) {
                hql.append(" and lower(p.name) like :name");
                params.put("name", "%" + nameFilter.toLowerCase() + "%");
            }

            if (categoryFilter != null && !categoryFilter.trim().isEmpty()
                    && !"Toutes".equalsIgnoreCase(categoryFilter)) {
                hql.append(" and lower(p.category) = :category");
                params.put("category", categoryFilter.toLowerCase());
            }

            if ("Prix".equalsIgnoreCase(sortBy)) {
                hql.append(" order by p.price");
            } else if ("Quantité".equalsIgnoreCase(sortBy)) {
                hql.append(" order by p.quantity");
            } else {
                hql.append(" order by p.name");
            }

            Query<Product> query = session.createQuery(hql.toString(), Product.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.list();
        }
    }

    public List<Product> findOutOfStock(int threshold) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Product> query = session.createQuery(
                    "from Product p where p.quantity <= :threshold order by p.quantity asc", Product.class);
            query.setParameter("threshold", threshold);
            return query.list();
        }
    }
}
