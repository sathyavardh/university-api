package org.sathya.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sathya.config.HibernateUtil;
import org.sathya.model.Course;

import java.util.List;

public class CourseDAO {

    //Create
    public void save(Course course) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            System.out.println("Saving course: " + course);
            session.persist(course);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
        }
    }


    //GetAll
    public List<Course> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Course", Course.class).list();
        }
    }

    //GetById
    public Course getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Course.class, id);
        }
    }

    //Update
    public void update(Course updatedCourse) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Corrected ID access
            Course existingCourse = session.get(Course.class, updatedCourse.getId());
            if (existingCourse != null) {
                // Correct field updates
                existingCourse.setTitle(updatedCourse.getTitle());
                existingCourse.setDescription(updatedCourse.getDescription());

                session.merge(existingCourse); // You can also use session.update(existingCourse)
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    //Delete
    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Course course = session.get(Course.class, id);
            if (course != null) {
                tx = session.beginTransaction();
                session.remove(course);
                tx.commit();
                System.out.println("Deleted Successfully");
                return true;
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return false;
    }

}
