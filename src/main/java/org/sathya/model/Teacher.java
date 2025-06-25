package org.sathya.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    public Teacher() {}

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Department getDepartment() { return department; }

    public void setDepartment(Department department) { this.department = department; }

    public List<Course> getCourses() { return courses; }

    public void setCourses(List<Course> courses) { this.courses = courses; }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department=" + (department != null ? department.getName() : null) +
                ", coursesCount=" + (courses != null ? courses.size() : 0) +
                '}';
    }
}
