# 🎓 University Management API

A simple Java + Hibernate-based University API using `HttpServer`, supporting entity relationships like students, departments, teachers, and courses — with full CRUD operations.

## 🧹 Entity Relationships (Hibernate)

### 🧑‍🎓 Student
- `@OneToOne` → [`StudentProfile`](#studentprofile)
- `@ManyToOne` → [`Department`](#department)
- `@ManyToMany` → [`Course`](#course)

### 🪪 StudentProfile
- `@OneToOne(mappedBy = "profile")` ← `Student`

### 🏢 Department
- `@OneToMany(mappedBy = "department")` → `Student`
- `@OneToMany(mappedBy = "department")` → `Teacher`

### 📚 Course
- `@ManyToMany(mappedBy = "courses")` ← `Student`

### 👨‍🏫 Teacher
- `@ManyToOne` → `Department`

---

## 🌐 API Endpoints

| Method | Endpoint                          | Description                          |
|--------|-----------------------------------|--------------------------------------|
| GET    | `/students`                       | List all students                    |
| POST   | `/students`                       | Create a student with profile        |
| GET    | `/students?id=1`                  | Get student by ID                    |
| PUT    | `/students?id=1`                  | Update student and profile by ID     |
| DELETE | `/students?id=1`                  | Delete student by ID                 |
| GET    | `/departments`                    | List all departments                 |
| POST   | `/departments`                    | Create a new department              |
| GET    | `/departments/{id}/students`      | List students under a department     |
| GET    | `/teachers`                       | List all teachers                    |
| POST   | `/teachers`                       | Add a teacher to a department        |
| GET    | `/courses`                        | List all courses                     |
| POST   | `/courses`                        | Add a new course                     |
| POST   | `/students/{id}/courses`          | Enroll student in courses            |
| GET    | `/students/{id}/courses`          | Get courses enrolled by a student    |

---

## 🛠 Tech Stack

- Core Java (`HttpServer`, `HttpHandler`)
- Hibernate ORM (v6+)
- Gson for JSON serialization
- PostgreSQL or H2 (configurable)
- No external frameworks like Spring Boot

---

## 🏁 How to Run

1. Clone the repository
2. Update your `hibernate.cfg.xml` with PostgreSQL credentials
3. Build the project using Maven or your IDE
4. Run:

```java
public static void main(String[] args) {
    SimpleHttpServer.start();  // Starts server on http://localhost:9090
}
```

---

## 🔄 Sample Flow (POST Requests)

### ➕ Create Department
`POST /departments`
```json
{
  "name": "Computer Science"
}
```

### ➕ Create Teacher (linked to Department)
`POST /teachers`
```json
{
  "name": "Dr. Ramesh",
  "department": {
    "id": 1
  }
}
```

### ➕ Create Course (linked to Teacher)
`POST /courses`
```json
{
  "title": "Spring Boot Essentials",
  "description": "Learn to build REST APIs",
  "teacher": {
    "id": 1
  }
}
```

### ➕ Create Student with Profile, Department, and Courses
`POST /students`
```json
{
  "name": "Sathya",
  "profile": {
    "address": "Erode",
    "phoneNo": "9876543210",
    "dob": "2003-04-28"
  },
  "department": {
    "id": 1
  },
  "courses": [
    { "id": 1 }
  ]
}
```

These demonstrate the complete flow from creating a department to associating teachers, courses, and enrolling students.
