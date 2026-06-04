# Supabase Email & Password Authentication Setup Guide

This guide details the setup and code implementation for supporting Email/Password sign-up and login flows, returning authentication JWT tokens and integrating session persistence.

## 1. Credentials Configuration
To integrate Supabase in the Android application, configure your credentials in the AI Studio SECRETS panel or your `.env` workspace:

```env
SUPABASE_URL=https://your-project-ref.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 2. Supabase GoTrue Auth REST Endpoints

Because the official Supabase Kotlin Android SDK can occasionally introduce version-catalog alignment issues with serialization, we use a robust client-side network model matching Supabase's native REST Authentication Engine (GoTrue).

### A. Sign Up with Email and Password
* **HTTP Method**: `POST`
* **URL**: `https://<your-project-id>.supabase.co/auth/v1/signup`
* **Headers**:
  * `apikey`: `<your-supabase-anon-key>`
  * `Content-Type`: `application/json`
* **JSON Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "securepassword123",
    "data": {
      "full_name": "Resident Patient"
    }
  }
  ```

---

### B. Sign In / Login with Email and Password
* **HTTP Method**: `POST`
* **URL**: `https://<your-project-id>.supabase.co/auth/v1/token?grant_type=password`
* **Headers**:
  * `apikey`: `<your-supabase-anon-key>`
  * `Content-Type`: `application/json`
* **JSON Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "securepassword123"
  }
  ```

* **Successful Response Body (200 OK)**:
  Returns the session information containing the bearer JWT token, refresh token, and user details:
  ```json
  {
    "access_token": "eyJhbGci...",
    "token_type": "bearer",
    "expires_in": 3600,
    "refresh_token": "dGg0Zg...",
    "user": {
      "id": "e63a144e-1234-4bc6-8d19-...",
      "email": "user@example.com",
      "user_metadata": {
        "full_name": "Resident Patient"
      }
    }
  }
  ```

---

## 3. Dynamic Patient Data Syncing Architecture
Once the client receives the `access_token` JWT inside the application, pass it as an Authorization Bearer header to query, insert, or synchronize `medicines` records securely:

* **Headers**:
  * `apikey`: `<supabase-anon-key>`
  * `Authorization`: `Bearer <user-access_token>`
