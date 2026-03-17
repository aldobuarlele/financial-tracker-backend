# 💰 Family Financial Tracker - Backend API

A robust, enterprise-grade RESTful API designed to manage multi-user family finances. Built with **Spring Boot 3** and **Java 17**, this system handles complex financial logic, secure authentication, and real-time family data synchronization.

## 🚀 Key Features

* **🔐 Enterprise Security:**
    * Implemented **Spring Security** with **JWT (JSON Web Token)** for stateless authentication.
    * Role-Based Access Control (RBAC) distinguishing between **Parent** (Admin) and **Child** (User) roles.

* **👨‍👩‍👧‍👦 Family Ecosystem Logic:**
    * **Parent Role:** Automatically generates a unique 6-character `Family Code` upon registration. Has global view access to all family members' transactions.
    * **Child Role:** Joins an existing family unit using the code. Data privacy is enforced between siblings.

* **💸 Smart Transaction Engine:**
    * **Multi-Wallet Support:** Users can manage multiple accounts (Bank, Cash, E-Wallet).
    * **Atomic Transfers:** Implemented transactional integrity for fund transfers between wallets. If one side fails, the entire operation rolls back to prevent data inconsistency.

* **📧 Automated Notifications:**
    * Integrated **JavaMailSender (SMTP)** to automatically email the `Family Code` to Parents immediately after successful registration.

* **📊 Reporting & Analytics API:**
    * Dedicated endpoints for aggregated statistics (Income vs Expense).
    * Category-based breakdown for visualization.


## 🛠️ Tech Stack

* **Core:** Java 17, Spring Boot 3
* **Database:** PostgreSQL (Relational Data Modeling)
* **ORM:** Hibernate / Spring Data JPA
* **Security:** Spring Security, JWT
* **Build Tool:** Maven
* **Utilities:** Lombok, JavaMailSender

## ⚙️ Setup & Configuration

### 1. Database Setup
Ensure PostgreSQL is running on port `5433` (or configure `application.properties`).
```sql
CREATE DATABASE finance_db;
-- Grant privileges to your user
GRANT ALL PRIVILEGES ON DATABASE finance_db TO finance_user;
