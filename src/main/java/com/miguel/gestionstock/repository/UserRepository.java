package com.miguel.gestionstock.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.miguel.gestionstock.entity.UserAccount;
import com.miguel.gestionstock.util.HibernateUtil;

public class UserRepository {

    public void save(UserAccount user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public UserAccount findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<UserAccount> query = session.createQuery(
                    "from UserAccount u where lower(u.username) = :username", UserAccount.class);
            query.setParameter("username", username.toLowerCase());
            return query.uniqueResult();
        }
    }
}
