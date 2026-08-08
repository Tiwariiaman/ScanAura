# 🚀 ScanAura - AI Powered Digital Restaurant QR Platform

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![JWT](https://img.shields.io/badge/JWT-Security-red)
![Gemini AI](https://img.shields.io/badge/Gemini-AI-purple)
![Cloudinary](https://img.shields.io/badge/Cloudinary-Images-blue)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-green)

> An AI-powered SaaS platform that enables restaurants, cafes, and food businesses to create and manage digital menus using QR codes, AI menu import, and subscription-based business management.

---

## 📖 Overview

ScanAura is a full-stack SaaS backend designed for restaurants to digitize their menu management process.

Restaurant owners can:

- Register their business
- Generate Digital & Physical QR Codes
- Upload restaurant branding
- Manage menu categories and items
- Import menus using Gemini AI from images/PDFs
- Manage subscriptions
- Receive QR-based customer visits
- Provide customers with a modern digital menu experience

---

# ✨ Key Features

## 🔐 Authentication & Security

- JWT Authentication
- Role-Based Authorization (ADMIN / BUSINESS_OWNER)
- BCrypt Password Encryption
- Spring Security
- Stateless Authentication

---

## 🏢 Business Management

- Business Registration
- Update Business Profile
- Business Logo Upload
- Public Restaurant Profile
- Business Activation / Deactivation

---

## 🍽 Menu Management

- Category CRUD
- Catalog (Menu Item) CRUD
- Display Order
- Veg / Non-Veg
- Availability Status
- Best Seller
- Recommended Items

---

## 🤖 AI Menu Import

Powered by **Google Gemini AI**

Features

- Upload Menu Image
- Upload PDF Menu
- AI OCR Processing
- Automatic Category Detection
- Automatic Menu Item Detection
- Duplicate Detection
- Automatic Category Creation
- Automatic Catalog Creation

Supported Formats

- JPG
- PNG
- WEBP
- PDF

---

## 📷 Image Management

Cloudinary Integration

- Food Images
- Business Logo
- Payment Screenshot Upload

---

## 🔳 QR Code System

Digital QR

- Auto Generated
- Public Restaurant URL

Physical QR

- QR Inventory
- QR Assignment
- QR Deactivation
- QR Management

---

## 💳 Subscription System

Plans

- Trial
- Basic
- Plus

Features

- 7-Day Trial
- Monthly Plan
- Yearly Plan
- Manual UPI Payment Workflow
- Payment Screenshot Upload
- Admin Approval
- Admin Rejection
- Subscription History
- Subscription Renewal

---

## 🚀 AI Usage Management

Plan Based AI Usage

- AI Import Limits
- Usage Tracking
- Automatic Validation
- Automatic Counter Reset
- Premium Plan Support

---

## ⚙️ Scheduler

Automatic Subscription Expiry

- Trial Expiry
- Active Subscription Expiry
- Daily Scheduler

---

## 👨‍💼 Admin Module

Dashboard

- Total Businesses
- Active Businesses
- Trial Users
- Active Subscribers
- Expired Subscribers
- Pending Subscription Requests
- QR Inventory Statistics

Business Management

- View Businesses
- Search Businesses
- Activate Business
- Deactivate Business

QR Management

- Generate Physical QR
- Inventory Management
- Deactivate QR

Subscription Management

- Approve Requests
- Reject Requests
- View History

---

## 📚 REST API Documentation

- Swagger / OpenAPI
- JWT Authentication Support
- Interactive API Testing

---

# 🏗 Architecture

```
Controller
        │
        ▼
Service
        │
        ▼
Repository
        │
        ▼
PostgreSQL
```

Architecture Patterns

- Layered Architecture
- Repository Pattern
- Dependency Injection
- DTO Pattern
- Global Exception Handling
- Centralized API Response
- Transaction Management

---

# 🛠 Tech Stack

### Backend

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway

### AI

- Google Gemini API

### Cloud

- Cloudinary

### Documentation

- Swagger / OpenAPI

### Security

- JWT
- BCrypt

### Build

- Maven

### Version Control

- Git
- GitHub

---

# 📂 Project Structure

```
src
├── admin
├── ai
├── auth
├── business
├── catalog
├── category
├── common
├── qr
├── subscription
└── config
```

---

# 🔄 Backend Workflow

```
Restaurant Owner

↓

Register

↓

Create Business

↓

Generate QR

↓

Upload Menu

↓

AI Import (Gemini)

↓

Manage Menu

↓

Customer Scans QR

↓

Digital Menu Opens

↓

Subscription Management

↓

Admin Approval
```

---

# 🔐 Security

- JWT Authentication
- Role Based Authorization
- Password Encryption
- Protected Admin APIs
- Subscription Validation
- AI Usage Validation

---

# 📈 Current Status

## ✅ Backend Completed

- Authentication
- Business Module
- QR Module
- Category Module
- Catalog Module
- AI Module
- Cloudinary Integration
- Subscription Module
- Admin Dashboard
- Swagger Documentation
- Scheduler
- Security

---

# 🚀 Future Roadmap

- Flutter Mobile Application
- WhatsApp Ordering
- Analytics Dashboard
- Table-wise QR
- Customer Reviews
- Restaurant Insights
- Payment Gateway Integration
- Multi-Branch Support

---

# 👨‍💻 About This Project

ScanAura was designed and developed as an independent full-stack backend project to solve real-world restaurant menu digitization challenges.

The project includes:

- End-to-end backend architecture
- RESTful API development
- Database design
- Authentication & authorization
- AI integration
- QR management
- Subscription management
- Production-ready backend practices

AI tools were used to accelerate research, design discussions, and implementation support, while the application architecture, business logic, integration, testing, and final implementation were independently built and integrated by the developer.

---

# 📬 Contact

**Aman Tiwari**

- 📧 Email: amantiwari2557@gmail.com
- 💼 LinkedIn: https://www.linkedin.com/in/aman--tiwari--
- 💻 GitHub: https://github.com/Tiwariiaman