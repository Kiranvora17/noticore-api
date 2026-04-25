# Noticore — Professional Email Notification API

> A production-grade, multi-tenant transactional email API platform built for developers and startups who need reliable email delivery with deep deliverability insights.

---

## Overview

Noticore is a backend API platform that abstracts the complexity of transactional email infrastructure. Instead of integrating directly with AWS SES and managing domain verification, DKIM records, bounce handling, and delivery tracking themselves, developers call a single clean API.

The platform is built on top of **AWS Simple Email Service (SES)** and is designed to be deployed as a public API on RapidAPI, enabling any developer to integrate professional email sending into their application within minutes.

---

## Problem We Solve

Most transactional email solutions either:
- Require complex setup and domain configuration
- Provide no visibility into **why** an email failed to deliver
- Are too expensive for indie developers and early-stage startups

Noticore provides a simple REST API with **detailed deliverability intelligence** — not just "sent" or "failed", but exactly what happened at every stage of delivery.

---

## Core Features

### ✅ Multi-Tenant Architecture
- Every API subscriber gets an isolated tenant environment
- Tenant identification via `X-RapidAPI-User` header — stable across API key rotations
- Complete data isolation — tenants can never access each other's data

### ✅ Domain Management
- Register and verify custom sending domains via AWS SES DKIM
- Returns complete DNS records (type, name, value) ready to add to any DNS provider
- Automatic domain verification via background scheduler
- Support for multiple domains per tenant

### ✅ Asynchronous Email Sending
- Non-blocking API — returns immediately with a notification ID
- Background processing via RabbitMQ message queue
- Retry mechanism — up to 3 attempts with 5-minute intervals using Dead Letter Exchange (DLX)
- Prevents duplicate delivery via RabbitMQ manual acknowledgment

### ✅ Notification Status Tracking
Full status lifecycle tracking per notification:
```
QUEUED → PROCESSING → DELIVERED
                    → FAILED → (retry) → PERMANENTLY_FAILED
```

### 🔄 Deliverability Intelligence (In Progress)
- SNS webhook integration for real-time delivery events
- Hard bounce detection — invalid email addresses
- Soft bounce detection — temporary delivery failures
- Spam/complaint detection
- Per-notification event history with actionable error classification

### 📋 Template Management (Planned)
- Create and manage reusable email templates with dynamic variables
- AWS SES template integration
- Send emails by referencing template name with variable substitution

### 📋 Suppression List Management (Planned)
- Check if an email address is suppressed before sending
- Remove addresses from suppression list
- Proactive bounce rate reduction

### 📋 Reputation Dashboard (Planned)
- Bounce rate, complaint rate, delivery rate per tenant
- Domain health scoring
- Sending statistics over time

---

## API Endpoints

### Domain Management
```
POST   /api/v1/domains              Register a new sending domain
GET    /api/v1/domains              List all domains for tenant
GET    /api/v1/domains/{id}         Get domain details and verification status
```

### Email Notifications
```
POST   /api/v1/notifications/email              Send an email
GET    /api/v1/notifications/email              List all notifications
GET    /api/v1/notifications/email/{id}         Get notification status
POST   /api/v1/notifications/email/{id}/retry   Manually retry a failed notification
```

### Webhooks
```
POST   /api/v1/webhooks/sns         Receives AWS SES delivery events via SNS
```

---

## How It Works

### 1. Domain Registration Flow
```
Developer registers domain → API calls AWS SES verifyDomainDkim()
→ Returns 3 CNAME records → Developer adds to DNS provider
→ Background scheduler polls SES every 4 minutes
→ Status updated to VERIFIED automatically
```

### 2. Email Sending Flow
```
POST /notifications/email
→ Validate from domain is verified
→ Save notification with status QUEUED
→ Publish notification ID to RabbitMQ
→ Return immediate response to developer

@RabbitListener (background):
→ Fetch notification from DB
→ Update status to PROCESSING
→ Call AWS SES to send email
→ On success → wait for SNS delivery event
→ On failure → retry up to 3 times via Dead Letter Exchange
```

### 3. Delivery Tracking Flow
```
AWS SES → SNS Topic → POST /api/v1/webhooks/sns
→ Parse delivery event (delivered/bounced/complained)
→ Update notification status in DB
→ Developer queries status via GET /notifications/email/{id}
```

---

## Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Language | Java 17 |
| Database | PostgreSQL with Liquibase migrations |
| Message Queue | RabbitMQ with Dead Letter Exchange |
| Email Delivery | AWS Simple Email Service (SES) |
| Connection Pool | HikariCP |
| ORM | Hibernate / Spring Data JPA |
| Deployment | AWS EC2 |
| API Distribution | RapidAPI |

---

## Architecture Decisions

### Why RabbitMQ
Email sending via SES takes 1-2 seconds per request. Synchronous sending would block the API for each request. RabbitMQ decouples the API from the sending process — developers get an immediate response and emails are processed asynchronously with guaranteed delivery.

### Why Dead Letter Exchange for Retries
Instead of a polling-based retry scheduler, we use RabbitMQ's native DLX + TTL mechanism. Failed messages are published to a retry queue with a 5-minute TTL. After expiry, RabbitMQ automatically routes them back to the main queue — no polling, precise timing, zero extra infrastructure.

### Why Multi-Tenancy via X-RapidAPI-User
RapidAPI's `X-RapidAPI-User` header contains the subscriber's permanent username — stable across API key rotations. This eliminates the key rotation problem that affects stateful multi-tenant APIs built on RapidAPI. Tenant creation is lazy — first request automatically provisions the tenant.

### Why Domain-Level Verification
Industry standard approach — verifies domain ownership via DKIM DNS records, establishes sending reputation at the domain level, allows sending from any address on the verified domain, and scales to unlimited tenants without per-address verification overhead.

---

## AWS SES Usage

Noticore uses AWS SES as the underlying email delivery infrastructure for the following purposes:

- **Domain Identity Verification** — `verifyDomainDkim()` to register tenant domains and generate DKIM tokens
- **Email Sending** — `sendEmail()` for transactional email delivery
- **Identity Status Checking** — `getIdentityDkimAttributes()` for domain verification status polling
- **Delivery Notifications** — SNS integration for bounce, complaint, and delivery events

**Sending Volume:** Initial deployment targets developer and startup use cases with expected volume of 1,000-10,000 emails/month. All emails are transactional — triggered by developer API calls, not bulk marketing campaigns.

**Compliance:** All sending domains are verified by their owners via DNS records. Noticore enforces domain ownership verification before allowing any email to be sent from a domain.

---

## Project Status

| Feature | Status |
|---|---|
| Multi-tenant interceptor | ✅ Complete |
| Domain registration | ✅ Complete |
| Domain verification scheduler | ✅ Complete |
| RabbitMQ integration | ✅ Complete |
| Email sending (async) | 🔄 In Progress |
| SNS webhook | 🔄 In Progress |
| Retry via DLX | 🔄 In Progress |
| Template management | 📋 Planned |
| Suppression list | 📋 Planned |
| Reputation dashboard | 📋 Planned |

---

## Contact

**Developer:** Kiran Vora  
**Purpose:** This project is being built as a production-grade portfolio API to demonstrate backend engineering capabilities including distributed systems, async processing, multi-tenancy, and cloud infrastructure.

---

*Built with Spring Boot · Powered by AWS SES · Distributed via RapidAPI*
