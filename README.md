# 🛡️ Financial Tracker API (Backend)

Backend Service untuk aplikasi pencatatan keuangan pribadi. Dibangun menggunakan **Java Spring Boot 3** dengan arsitektur RESTful API yang aman.

Project ini bertugas menangani logika bisnis, perhitungan saldo, validasi transaksi, dan keamanan data menggunakan **JWT Authentication**.

## 🚀 Tech Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3
- **Security:** Spring Security 6 + JWT (JSON Web Token)
- **Database:** PostgreSQL
- **Architecture:** Controller-Service-Repository Pattern
- **Coding Style:** Explicit Getter/Setter (No Lombok dependency)

## ⚙️ Prerequisites
Pastikan Anda sudah menginstall:
1. Java Development Kit (JDK) 17 atau lebih baru.
2. PostgreSQL Database.
3. Maven (atau gunakan wrapper `mvnw` bawaan).

## 🛠️ Installation & Setup

### 1. Database Configuration
Buat database baru di PostgreSQL bernama `finance_db`.
Lalu, atur kredensial database Anda di file `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=password_anda
