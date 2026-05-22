# Smart Cafe Management System

A Java-based console application for managing a cafe with separate **User Panel** and **Admin Panel**. Built using **JDBC** for MySQL database connectivity.

---

## Features

### User Panel
- Secure login using **User ID** and **Password**
- View the full menu (without stock quantity)
- Place orders with support for:
  - Individual food items with custom quantity
  - Combo offers
- Automatic bill generation with GST (5%) and discount (10% on orders above ₹1000)
- Order receipt with all details

### Admin Panel
- Secure admin login (`admin` / `admin123`)
- Add, Update, and Delete food items
- View all registered users
- View complete order history of any user
- Sales report with total revenue

---

## Database Setup

### 1. Create Database

```sql
CREATE DATABASE IF NOT EXISTS cafe_db;
USE cafe_db;
