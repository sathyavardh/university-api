package org.sathya.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sathya.config.HibernateUtil;
import org.sathya.model.Student;
import org.sathya.model.StudentProfile;
import org.sathya.model.Course;
import org.sathya.model.Department;

import java.util.List;

public class StudentDAO {

    // Create
    public void save(Student student) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Handle profile
            StudentProfile profile = student.getProfile();
            if (profile != null) {
                student.setProfile(profile);
            }

            // Attach department by ID
            if (student.getDepartment() != null && student.getDepartment().getId() != 0) {
                Department dept = session.get(Department.class, student.getDepartment().getId());
                student.setDepartment(dept);
            }

            // Attach course references by ID only
            if (student.getCourses() != null) {
                List<Course> attachedCourses = student.getCourses().stream()
                        .map(c -> session.get(Course.class, c.getId()))
                        .filter(c -> c != null)
                        .toList();
                student.setCourses(attachedCourses);
            }

            session.persist(student);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    // Get All
    public List<Student> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student", Student.class).list();
        }
    }

    // Get By ID
    public Student getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, id);
        }
    }

    // Update
    public void update(Student updatedStudent) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Student existingStudent = session.get(Student.class, updatedStudent.getId());
            if (existingStudent != null) {
                existingStudent.setName(updatedStudent.getName());

                // Update profile
                if (updatedStudent.getProfile() != null) {
                    StudentProfile newProfile = updatedStudent.getProfile();
                    StudentProfile existingProfile = existingStudent.getProfile();

                    if (existingProfile != null) {
                        existingProfile.setAddress(newProfile.getAddress());
                        existingProfile.setPhoneNo(newProfile.getPhoneNo());
                        existingProfile.setDob(newProfile.getDob());
                    } else {
                        existingStudent.setProfile(newProfile);
                    }
                }

                // Update department
                if (updatedStudent.getDepartment() != null && updatedStudent.getDepartment().getId() != 0) {
                    Department dept = session.get(Department.class, updatedStudent.getDepartment().getId());
                    if (dept != null) {
                        existingStudent.setDepartment(dept);
                    } else {
                        throw new IllegalArgumentException("Invalid Department ID: " + updatedStudent.getDepartment().getId());
                    }
                }

                // Prepare attachedCourses outside if-block
                List<Course> attachedCourses = null;
                if (updatedStudent.getCourses() != null) {
                    attachedCourses = updatedStudent.getCourses().stream()
                            .map(c -> session.get(Course.class, c.getId()))
                            .filter(c -> c != null)
                            .toList();

                    // Reassign safely
                    existingStudent.getCourses().clear();
                    existingStudent.getCourses().addAll(attachedCourses);
                }

                session.merge(existingStudent);
                tx.commit();
            } else {
                throw new IllegalArgumentException("Student with ID " + updatedStudent.getId() + " not found.");
            }

        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
        }
    }




    // Delete
    public boolean delete(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Student student = session.get(Student.class, id);
            if (student != null) {
                tx = session.beginTransaction();
                session.remove(student);
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
