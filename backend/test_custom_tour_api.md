# Custom Tour API Testing Guide

## 1. Login to get JWT Token

**POST** `http://localhost:8080/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
    "email": "admin@admin.com",
    "password": "password123"
}
```

**Expected Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
        "id": 1,
        "email": "admin@admin.com",
        "firstName": "Admin",
        "lastName": "User"
    }
}
```

## 2. Create Custom Tour Request

**POST** `http://localhost:8080/api/tours/custom`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer [JWT_TOKEN_FROM_STEP_1]
```

**Body:**
```json
{
    "name": "Custom Adventure Tour",
    "region": "Western Province",
    "startDate": "2024-03-15",
    "endDate": "2024-03-20",
    "durationValue": 5,
    "durationUnit": "days",
    "groupSize": 4,
    "activities": ["Hiking", "Sightseeing"],
    "price": 1500.00,
    "specialRequirements": "We would like vegetarian meals and prefer mountain views"
}
```

**Expected Response (Success):**
```json
{
    "id": 6,
    "name": "Custom Adventure Tour",
    "category": "Custom Tour Request",
    "status": "PENDING_APPROVAL",
    "isCustom": true,
    "region": "Western Province",
    "price": 1500.00,
    "durationValue": 5,
    "durationUnit": "days",
    "availableSpots": 4,
    "createdBy": {
        "id": 1,
        "email": "admin@admin.com",
        "firstName": "Admin",
        "lastName": "User"
    },
    "createdAt": "2025-07-06T15:30:00",
    "highlights": [
        "Custom tour request",
        "Requested by: Admin User",
        "Contact: admin@admin.com",
        "Requested dates: 2024-03-15 to 2024-03-20",
        "Group size: 4 people"
    ]
}
```

## 3. Get Custom Tours for Current User

**GET** `http://localhost:8080/api/tours/custom/my-tours`

**Headers:**
```
Authorization: Bearer [JWT_TOKEN_FROM_STEP_1]
```

## 4. Get All Custom Tours (Admin Only)

**GET** `http://localhost:8080/api/tours/custom`

**Headers:**
```
Authorization: Bearer [JWT_TOKEN_FROM_STEP_1]
```

## 5. Get Pending Custom Tours (Admin Only)

**GET** `http://localhost:8080/api/tours/custom/pending`

**Headers:**
```
Authorization: Bearer [JWT_TOKEN_FROM_STEP_1]
```

## Expected Errors

### Validation Error (400):
```json
{
    "error": "Validation failed",
    "details": {
        "endDate": "End date must be after start date"
    }
}
```

### Authentication Error (401):
```json
{
    "error": "Unauthorized",
    "message": "JWT token is missing or invalid"
}
```
