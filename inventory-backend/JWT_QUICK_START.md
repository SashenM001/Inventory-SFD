# JWT Authentication Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Set Environment Variables (Windows PowerShell)

```powershell
# Navigate to backend directory
cd "d:\FMS Project\Inventory_SFD\inventory-backend"

# Set required environment variables
$env:MYSQL_PASSWORD="Kanil12Mysql22_"
$env:JWT_SECRET="your-super-secret-jwt-key-at-least-32-characters-minimum"

# Verify they're set
Write-Host $env:JWT_SECRET
```

### Step 2: Start the Backend

```powershell
# Run the JAR file with Java 21
java -jar target/inventory-service-1.0.0.jar

# Expected output:
# Started InventoryApplication in 5.234 seconds (JVM running for 5.678)
```

### Step 3: Test Authentication

#### Using Postman (Recommended)

**Request 1: Obtain JWT Token**

- **Method**: POST
- **URL**: `http://localhost:8082/api/v1/auth/login`
- **Headers**: Content-Type: application/json
- **Body (raw JSON)**:
  ```json
  {
    "username": "admin",
    "password": "admin"
  }
  ```
- **Click Send**
- **Response**:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzAwMDAwMCwiZXhwIjoxNzAzMDg2NDAwfQ.signature",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
  ```

**Request 2: Access Protected Endpoint**

- **Method**: GET
- **URL**: `http://localhost:8082/api/v1/suppliers`
- **Headers**:
  - Key: `Authorization`
  - Value: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` (paste token from Step 1)
- **Click Send**
- **Response**: HTTP 200 OK with suppliers list

#### Using cURL (Command Line)

```bash
# Step 1: Get token
TOKEN=$(curl -s -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.accessToken')

echo "Token: $TOKEN"

# Step 2: Use token to access API
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8082/api/v1/suppliers
```

---

## 🔐 How It Works

### 1. Login (Get Token)

```
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "admin"
}
↓
Server validates credentials
↓
Server generates JWT token
↓
Response: {accessToken, tokenType: "Bearer", expiresIn: 86400000}
```

### 2. Use Token (Access Protected API)

```
GET /api/v1/suppliers
Authorization: Bearer {accessToken}
↓
Server extracts token from Authorization header
↓
Server validates token signature and expiration
↓
If valid: Grant access to resource
If invalid: Return HTTP 403 Forbidden
```

---

## 📋 Test Scenarios

### Scenario 1: ❌ Request Without Token

```bash
curl http://localhost:8082/api/v1/suppliers

# Response: 403 Forbidden
{
  "timestamp": "2024-01-20T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/suppliers"
}
```

### Scenario 2: ❌ Request With Invalid Token

```bash
curl -H "Authorization: Bearer invalid.token.here" \
  http://localhost:8082/api/v1/suppliers

# Response: 403 Forbidden
{
  "timestamp": "2024-01-20T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/v1/suppliers"
}
```

### Scenario 3: ❌ Invalid Login Credentials

```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"wrongpassword"}'

# Response: 401 Unauthorized
{
  "timestamp": "2024-01-20T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials",
  "path": "/api/v1/auth/login"
}
```

### Scenario 4: ✅ Valid Request With Token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8082/api/v1/suppliers

# Response: 200 OK
[
  {
    "id": 1,
    "name": "Supplier A",
    "email": "supplier@example.com",
    "phone": "555-1234",
    "address": "123 Main St",
    "zipCode": "12345",
    "createdAt": "2024-01-20T10:30:00.000+00:00",
    "updatedAt": "2024-01-20T10:30:00.000+00:00"
  },
  {
    "id": 2,
    "name": "Supplier B",
    "email": "supplier2@example.com",
    "phone": "555-5678",
    "address": "456 Oak Ave",
    "zipCode": "54321",
    "createdAt": "2024-01-20T10:30:00.000+00:00",
    "updatedAt": "2024-01-20T10:30:00.000+00:00"
  }
]
```

---

## 🔧 Available Endpoints

### Public Endpoints (No Authentication Required)

- `POST /api/v1/auth/login` - Get JWT token
- `GET /actuator/health` - Application health check

### Protected Endpoints (JWT Token Required)

**Suppliers**

- `GET /api/v1/suppliers` - List all suppliers
- `POST /api/v1/suppliers` - Create supplier
- `GET /api/v1/suppliers/{id}` - Get supplier details
- `PUT /api/v1/suppliers/{id}` - Update supplier
- `DELETE /api/v1/suppliers/{id}` - Delete supplier

**Inventory Items**

- `GET /api/v1/items` - List all items
- `POST /api/v1/items` - Create item
- `GET /api/v1/items/{id}` - Get item details
- `PUT /api/v1/items/{id}` - Update item
- `DELETE /api/v1/items/{id}` - Delete item

**Item Additions** (Stock In)

- `GET /api/v1/additions` - List additions
- `POST /api/v1/additions` - Record new addition
- `GET /api/v1/additions?startDate=2024-01-01&endDate=2024-01-31` - Query by date

**Item Issues** (Stock Out)

- `GET /api/v1/issues` - List issues
- `POST /api/v1/issues` - Record new issue
- `GET /api/v1/issues?startDate=2024-01-01&endDate=2024-01-31` - Query by date

---

## 🛠️ Environment Variables

### Required

```bash
# Database
MYSQL_PASSWORD=your-database-password
MYSQL_URL=jdbc:mysql://localhost:3306/dbfms?useSSL=true&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=false
MYSQL_USER=root

# JWT (CRITICAL - Must be 32+ characters)
JWT_SECRET=your-super-secret-minimum-32-characters-very-strong
```

### Optional

```bash
JWT_EXPIRATION=86400000              # Token valid for 24 hours
JWT_REFRESH_EXPIRATION=604800000     # Refresh token valid for 7 days
LOGGING_LEVEL_ROOT=INFO              # For production, use INFO or WARN
```

---

## 🧪 Testing with Different Tools

### Using cURL in Git Bash

```bash
# Login
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Extract token (requires jq)
TOKEN=$(curl -s -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.accessToken')

# Use token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8082/api/v1/suppliers
```

### Using PowerShell

```powershell
# Login and store response
$response = Invoke-WebRequest -Uri "http://localhost:8082/api/v1/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"admin","password":"admin"}'

# Extract token from response
$token = ($response.Content | ConvertFrom-Json).accessToken

# Use token to access API
Invoke-WebRequest -Uri "http://localhost:8082/api/v1/suppliers" `
  -Method GET `
  -Headers @{"Authorization"="Bearer $token"}
```

### Using JavaScript (Node.js)

```javascript
// Login
const loginResponse = await fetch("http://localhost:8082/api/v1/auth/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ username: "admin", password: "admin" }),
});

const { accessToken } = await loginResponse.json();
console.log("Token:", accessToken);

// Use token
const apiResponse = await fetch("http://localhost:8082/api/v1/suppliers", {
  method: "GET",
  headers: { Authorization: `Bearer ${accessToken}` },
});

const suppliers = await apiResponse.json();
console.log("Suppliers:", suppliers);
```

---

## ⚠️ Troubleshooting

### Problem: "JWT_SECRET environment variable is not configured"

**Solution**:

```powershell
$env:JWT_SECRET="your-strong-secret-minimum-32-characters"
java -jar target/inventory-service-1.0.0.jar
```

### Problem: "Cannot connect to database"

**Solution**: Verify MYSQL_PASSWORD is set correctly:

```powershell
$env:MYSQL_PASSWORD="Kanil12Mysql22_"
```

### Problem: "401 Unauthorized" on login

**Solution**: Verify username and password are correct:

```bash
# Username: admin
# Password: admin (by default, unless changed)
```

### Problem: Token expires too quickly

**Solution**: Increase JWT_EXPIRATION (default 24 hours = 86400000 ms):

```powershell
$env:JWT_EXPIRATION="604800000"  # 7 days
java -jar target/inventory-service-1.0.0.jar
```

---

## 📚 Complete Reference

For detailed information, see:

- [JWT_IMPLEMENTATION_SUMMARY.md](./JWT_IMPLEMENTATION_SUMMARY.md) - Full technical documentation
- [ENVIRONMENT_VARIABLES.md](./ENVIRONMENT_VARIABLES.md) - Environment variables guide
- [README.md](./README.md) - Project overview

---

## ✅ Checklist: Ready for Production?

- [ ] JWT_SECRET is set to a strong random value (32+ characters)
- [ ] MYSQL_PASSWORD is correct and security hardened
- [ ] HTTPS/SSL is configured (after obtaining certificate)
- [ ] All protected endpoints require JWT tokens
- [ ] Token expiration is appropriate for your use case
- [ ] Logging level is set to INFO (not DEBUG)
- [ ] Database connection uses SSL (useSSL=true)
- [ ] API is being called with correct Authorization header format

---

**Status**: ✅ JWT Authentication is fully implemented and ready to use!
