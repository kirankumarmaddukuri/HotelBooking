# 🚀 Postman API Testing Guide

This document contains all the REST API endpoints for the Hotel Booking System. You can use these details to test the backend directly using Postman.

> **Base URL**: `http://localhost:8080`

---

## 🔐 1. Authentication APIs

### 1.1 User Registration
- **URL**: `/api/auth/signup`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "name": "Test User",
    "email": "testuser@gmail.com",
    "password": "password123"
  }
  ```

### 1.2 User Login
- **URL**: `/api/auth/signin`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Body**:
  ```json
  {
    "email": "testuser@gmail.com",
    "password": "password123"
  }
  ```
> **IMPORTANT**: The response will contain a `token`. Copy this token! For all protected routes below, you must go to the **Authorization** tab in Postman, select **Bearer Token**, and paste this token.

---

## 🏨 2. Hotel & Room APIs (Public)
*These endpoints do not require a JWT token.*

### 2.1 Get All Hotels (Optional Search)
- **URL**: `/api/hotels`
- **Method**: `GET`
- **Query Parameters** (Optional): 
  - `?location=Mumbai`
  - `?checkInDate=2026-06-01&checkOutDate=2026-06-05`

### 2.2 Get Single Hotel Details
- **URL**: `/api/hotels/{id}` *(e.g., `/api/hotels/1`)*
- **Method**: `GET`

### 2.3 Get Available Rooms for Hotel
- **URL**: `/api/hotels/{hotelId}/rooms` *(e.g., `/api/hotels/1/rooms`)*
- **Method**: `GET`
- **Query Parameters** (Optional): 
  - `?checkInDate=2026-06-01&checkOutDate=2026-06-05`

---

## 🛡️ 3. Admin APIs (Requires Admin JWT)
*You must log in as `admin@hotel.com` to use these.*

### 3.1 Create New Hotel
- **URL**: `/api/hotels`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Body**:
  ```json
  {
    "name": "New Luxury Hotel",
    "location": "Pune, Maharashtra",
    "description": "A beautiful test hotel.",
    "amenities": "Pool, Wi-Fi",
    "rooms": [
      {
        "roomType": "Standard Room",
        "price": 5000.00
      }
    ]
  }
  ```

### 3.2 Add Room to Existing Hotel
- **URL**: `/api/hotels/{hotelId}/rooms` *(e.g., `/api/hotels/1/rooms`)*
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Body**:
  ```json
  {
    "roomType": "Penthouse",
    "price": 15000.00
  }
  ```

### 3.3 Get All Bookings (System Wide)
- **URL**: `/api/bookings/admin/all`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`

### 3.4 Update Booking Status (Approve/Reject)
- **URL**: `/api/bookings/{bookingId}/status` *(e.g., `/api/bookings/1/status`)*
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Body**:
  ```json
  {
    "status": "CONFIRMED" 
  }
  ```
  *(Status can be `CONFIRMED` or `CANCELLED`)*

---

## 👤 4. User Booking APIs (Requires User JWT)
*You must log in as a standard user to use these.*

### 4.1 Create a Booking
- **URL**: `/api/bookings`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Body**:
  ```json
  {
    "roomId": 1,
    "checkInDate": "2026-06-10",
    "checkOutDate": "2026-06-15"
  }
  ```

### 4.2 Get My Bookings
- **URL**: `/api/bookings`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`

### 4.3 Cancel My Booking
- **URL**: `/api/bookings/{bookingId}` *(e.g., `/api/bookings/1`)*
- **Method**: `DELETE`
- **Headers**: `Authorization: Bearer <token>`
