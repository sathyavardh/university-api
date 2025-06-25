package org.sathya.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sathya.config.HibernateUtil;
import org.sathya.model.Teacher;
import org.sathya.model.Course;
import org.sathya.model.Department;

import java.util.List;

public class TeacherDAO {

    // Create
    public void save(Teacher teacher) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Attach managed Department
            if (teacher.getDepartment() != null && teacher.getDepartment().getId() > 0) {
                teacher.setDepartment(session.get(Department.class, teacher.getDepartment().getId()));
            }

            // Attach managed Courses
            if (teacher.getCourses() != null) {
                List<Course> attachedCourses = teacher.getCourses().stream()
                        .map(c -> session.get(Course.class, c.getId()))
                        .filter(c -> c != null)
                        .toList();
                teacher.setCourses(attachedCourses);
            }

            session.persist(teacher);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    // Get All
    public List<Teacher> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Teacher", Teacher.class).list();
        }
    }

    // Get By ID
    public Teacher getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Teacher.class, id);
        }
    }

    // Update
    public void update(Teacher updatedTeacher) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Teacher existing = session.get(Teacher.class, updatedTeacher.getId());
            if (existing != null) {
                existing.setName(updatedTeacher.getName());

                // Update Department
                if (updatedTeacher.getDepartment() != null) {
                    Department dept = session.get(Department.class, updatedTeacher.getDepartment().getId());
                    existing.setDepartment(dept);
                }

                // Update Courses
                if (updatedTeacher.getCourses() != null) {
                    List<Course> managedCourses = updatedTeacher.getCourses().stream()
                            .map(c -> session.get(Course.class, c.getId()))
                            .filter(c -> c != null)
                            .toList();
                    existing.setCourses(managedCourses);
                }

                session.merge(existing);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }


    // Delete
    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Teacher teacher = session.get(Teacher.class, id);
            if (teacher != null) {
                tx = session.beginTransaction();
                session.remove(teacher);
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
