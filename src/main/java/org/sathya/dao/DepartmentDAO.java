package org.sathya.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sathya.config.HibernateUtil;
import org.sathya.model.Department;

import java.util.List;

public class DepartmentDAO {

    public void save(Department department) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(department);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Department> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Department", Department.class).list();
        }
    }

    public Department getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Department.class, id);
        }
    }

    public void update(Department updated) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Department existing = session.get(Department.class, updated.getId());
            if (existing != null) {
                existing.setName(updated.getName());
                session.merge(existing);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Department department = session.get(Department.class, id);
            if (department != null) {
                tx = session.beginTransaction();
                session.remove(department);
                tx.commit();
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return false;
    }
}
