package training.afpa.cda24060.squatrbnb.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import training.afpa.cda24060.squatrbnb.utilitaires.HibernateUtil;

import java.util.List;
import java.util.Optional;

public abstract class GenericDAO<T, ID> {

    protected final Class<T> entityClass;

    protected GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    public Optional<T> findById(ID id) {
        try (Session session = getSession()) {
            T entity = session.get(entityClass, id);
            return Optional.ofNullable(entity);
        }
    }

    public List<T> findAll() {
        try (Session session = getSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass)
                    .getResultList();
        }
    }

    public T save(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public T update(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            T merged = session.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void delete(T entity) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            session.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }
}