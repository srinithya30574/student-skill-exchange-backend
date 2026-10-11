# API Documentation

## 1. Overview

The Student Skill Exchange Platform uses REST APIs to connect the React frontend with the Spring Boot backend.

**Base URL:** `http://localhost:8080/api`

Requests and responses use JSON format unless otherwise specified.

## 2. Authentication APIs

### Register a User

- **Method:** POST
- **Endpoint:** `/auth/register`
- **Purpose:** Creates a new student account.

Request body:

```json
{
  "name": "Student Name",
  "email": "student@example.com",
  "password": "password123",
  "department": "CSE",
  "year": 2,
  "bio": "Interested in learning programming"
}
```

### Login

- **Method:** POST
- **Endpoint:** `/auth/login`
- **Purpose:** Authenticates a student using email and password.

Request body:

```json
{
  "email": "student@example.com",
  "password": "password123"
}
```

## 3. User APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/users` | Retrieve users |
| GET | `/users/{id}` | Retrieve a user by ID |
| PUT | `/users/{id}` | Update user details |

## 4. Skills APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/skills/categories` | Retrieve skill categories |
| GET | `/skills` | Retrieve skills |
| GET | `/users/{userId}/skills` | Retrieve a user's skills |
| POST | `/users/{userId}/skills` | Add a skill |
| PUT | `/users/{userId}/skills/{userSkillId}` | Update a skill |
| DELETE | `/users/{userId}/skills/{userSkillId}` | Delete a skill |

The user's skills can have a type such as `TEACH` or `LEARN`, depending on the backend implementation.

## 5. Student Search APIs

- **Method:** GET
- **Endpoint:** `/students/search`
- **Purpose:** Search for students using supported filters.

Example:

`/students/search?skill=React&department=CSE&year=2&excludeUserId=2`

Supported filters depend on the backend implementation.

## 6. Matching APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/matches/{userId}` | Retrieve matching students |
| GET | `/matches/{userId}/with/{otherUserId}` | Retrieve a match between two students |

## 7. Exchange Request APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/requests` | Send an exchange request |
| GET | `/requests/received/{userId}` | Retrieve received requests |
| GET | `/requests/sent/{userId}` | Retrieve sent requests |
| PUT | `/requests/{requestId}/accept?userId={id}` | Accept a request |
| PUT | `/requests/{requestId}/reject?userId={id}` | Reject a request |

Example request body for sending a request:

```json
{
  "senderId": 1,
  "receiverId": 2,
  "message": "Would you like to exchange Java and React skills?"
}
```

## 8. Connection APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/connections/{userId}` | Retrieve a user's connections |

## 9. Notification APIs

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/notifications/user/{userId}` | Retrieve notifications |
| GET | `/notifications/user/{userId}/unread-count` | Count unread notifications |
| PUT | `/notifications/{notificationId}/read?userId={id}` | Mark a notification as read |
| PUT | `/notifications/user/{userId}/read-all` | Mark all notifications as read |

## 10. Rating APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/ratings` | Submit a rating and feedback |
| GET | `/ratings/user/{userId}` | Retrieve ratings for a user |
| GET | `/ratings/user/{userId}/summary` | Retrieve rating summary |
| GET | `/ratings/given/{userId}` | Retrieve ratings given by a user |

Example request body:

```json
{
  "reviewerId": 1,
  "reviewedUserId": 2,
  "rating": 4,
  "feedback": "Helpful and clear explanation"
}
```

## 11. Dashboard API

- **Method:** GET
- **Endpoint:** `/dashboard/{userId}`
- **Purpose:** Retrieves dashboard information for a student.

## 12. Common HTTP Status Codes

| Status Code | Meaning |
|---|---|
| 200 | Request successful |
| 201 | Resource created |
| 204 | Request successful with no response body |
| 400 | Bad request |
| 401 | Unauthorized |
| 404 | Resource not found |

## 13. Important Note

This document describes the intended API structure. Actual endpoints, request fields, and response formats must be verified against the Spring Boot controllers and DTOs implemented in the project. An endpoint listed here is not necessarily implemented until it is confirmed in the backend source code.