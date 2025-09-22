# Hospital Registration Information System

## 📌 Project Overview
This project is an **Information System for Hospital Registration**, developed as a course project.  
The system is designed to manage **patients, staff, and communication** within a hospital.  
It is built using **Spring MVC** following the **MVC architectural pattern**.

The system provides role-based access control:
- **Chief Doctor** – manages doctors, nurses, and hospital operations.
- **Doctors** – manage patients, their records, and medical history.
- **Nurses** – assist doctors, manage appointments, and patient flow.

---

## ✨ Features
- Patient registration and management
- Staff management (doctors, nurses, administrators)
- Role-based access control with different permissions
- Secure authentication and authorization
- Appointment and medical records handling
- Internal communication support between staff

---

## 🛠️ Technologies Used
- **Java 17+**
- **Spring MVC / Spring Boot**
- **Spring Security** (authentication & authorization)
- **Thymeleaf** (server-side templates)
- **Hibernate / JPA** (ORM for database operations)
- **MySQL** (relational database)
- **Maven** (dependency management and build tool)

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/your-username/HospitalRegistrationSystem.git
cd HospitalRegistrationSystem
```

## Configure the database

Create a MySQL database:
```SQL
CREATE DATABASE hospital_db;
```
Update application.properties with your DB credentials:
```SQL
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```
## Build and run
```bash
mvn spring-boot:run
```

## Access the system
Open: ```http://localhost:8080```

## 📂 Project Structure
```bash
HospitalRegistrationSystem/
│── src/main/java/...      # Java source code
│   ├── controllers/       # Spring MVC controllers
│   ├── models/            # Entity classes (JPA)
│   ├── repositories/      # Data access layer
│   ├── services/          # Business logic
│── src/main/resources/
│   ├── templates/         # Thymeleaf views
│   ├── static/            # CSS, JS, images
│   └── application.properties
│── pom.xml                # Maven configuration
```
