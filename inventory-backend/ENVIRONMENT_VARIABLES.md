# Environment Variables Configuration

## Overview

This document describes all required and optional environment variables for the Inventory Service. All sensitive credentials must be configured via environment variables (no hardcoding in source code).

---

## Database Configuration

### Required Variables

| Variable         | Description                                        | Example                                                                                                   |
| ---------------- | -------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| `MYSQL_URL`      | MySQL database connection URL                      | `jdbc:mysql://localhost:3306/dbfms?useSSL=true&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=false` |
| `MYSQL_USER`     | Database username (never use `root` in production) | `inventory_user`                                                                                          |
| `MYSQL_PASSWORD` | Database password (minimum 12 characters)          | `SecurePassword123!@#`                                                                                    |

### Example Configuration

```properties
spring.datasource.url=${MYSQL_URL:jdbc:mysql://localhost:3306/dbfms?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false}
spring.datasource.username=${MYSQL_USER:root}
spring.datasource.password=${MYSQL_PASSWORD:Kanil12Mysql22_}
```

### Railway MySQL Setup (⚠️ CRITICAL: Follow These Steps Exactly)

**⚠️ IMPORTANT:** Railway generates a unique MySQL password automatically. You MUST extract it and set it as an environment variable, or your app will get "Access denied" errors.

#### Step-by-Step: Get MySQL Credentials from Railway

**STEP 1: Go to Railway Dashboard**
1. Open https://railway.app
2. Select your project
3. Look for the **MySQL service** in your project canvas

**STEP 2: View MySQL Connection Details**
1. Click on the **MySQL box/service** in your project
2. Go to the **"Variables"** tab (or **"Settings"** → **"Environment"**)
3. You'll see auto-generated variables:
   - `MYSQL_HOST` = Database hostname (e.g., `mysql.railway.internal` or IP)
   - `MYSQL_PORT` = Port (usually `3306`)
   - `MYSQL_DATABASE` = Database name (usually `railway`)
   - `MYSQL_USER` = Username (usually `root`)
   - `MYSQL_PASSWORD` = **← COPY THIS VALUE** (this is YOUR unique password)

**IMPORTANT:** The `MYSQL_PASSWORD` Railroad generates is DIFFERENT from the local default!

**STEP 3: Set Variables in Your App Service**
1. Go back to your **App service** (not MySQL)
2. Click **"Variables"** tab
3. Add these 3 variables (MUST match the values from Step 2):

```
MYSQL_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
MYSQL_USER=root
MYSQL_PASSWORD=<PASTE_THE_VALUE_YOU_COPIED_FROM_RAILWAY_MYSQL>
JWT_SECRET=<your-32-character-secret-key>
```

**CRITICAL:** Make sure:
- ✅ `MYSQL_PASSWORD` matches EXACTLY what Railway MySQL generated
- ✅ `MYSQL_USER` is `root` (or whatever Railway shows)
- ✅ `MYSQL_URL` uses `mysql.railway.internal` (Railway's internal hostname)
- ✅ Database name is `railway` (unless you created a different one)

#### Example (REAL VALUES FROM RAILWAY)

If your Railway MySQL variables show:
```
MYSQL_HOST = mysql.railway.internal
MYSQL_PORT = 3306
MYSQL_DATABASE = railway
MYSQL_USER = root
MYSQL_PASSWORD = AbC123xYz789defG012HiJk
```

Then set in your App service:
```
MYSQL_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
MYSQL_USER=root
MYSQL_PASSWORD=AbC123xYz789defG012HiJk
JWT_SECRET=MySecretKey123456789012345678901
```

#### Troubleshooting "Access denied" Error

**Error:** `Access denied for user 'root'@'...' (using password: YES)`

**Solution:**
1. Go to Railway MySQL service → Variables tab
2. Copy the EXACT `MYSQL_PASSWORD` value
3. Go to your App service → Variables tab
4. Update `MYSQL_PASSWORD` to match exactly (character-for-character)
5. Save and redeploy

---

## JWT (JSON Web Token) Configuration

### Required Variables

| Variable     | Description                                                             | Example                                    | Minimum Length                     |
| ------------ | ----------------------------------------------------------------------- | ------------------------------------------ | ---------------------------------- |
| `JWT_SECRET` | Secret key for signing JWT tokens (CRITICAL: must be strong and secret) | `your-256-bit-secret-key-minimum-32-chars` | 32 characters (256 bits for HS256) |

### Optional Variables

| Variable                 | Description                                   | Default Value         | Value Format |
| ------------------------ | --------------------------------------------- | --------------------- | ------------ |
| `JWT_EXPIRATION`         | Access token expiration time in milliseconds  | `86400000` (24 hours) | Milliseconds |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration time in milliseconds | `604800000` (7 days)  | Milliseconds |

### Token Expiration Presets

- **1 hour**: `3600000`
- **6 hours**: `21600000`
- **24 hours**: `86400000`
- **7 days**: `604800000`
- **30 days**: `2592000000`

### JWT_SECRET Generation

#### Using OpenSSL (Linux/Mac/Windows Git Bash)

```bash
openssl rand -base64 32
```

#### Using Python

```python
import secrets
import base64
base64.b64encode(secrets.token_bytes(32)).decode()
```

#### Using Online Tools

- https://randomkeygen.com/ (get a 256-bit key)

**IMPORTANT**: Store JWT_SECRET securely:

- Never commit to version control
- Use CI/CD secrets manager (GitHub Secrets, GitLab CI/CD variables, etc.)
- Rotate periodically (every 6-12 months recommended)

---

## Server Configuration

### SSL/TLS (HTTPS) - Production Only

| Variable                        | Description                      | Example                 |
| ------------------------------- | -------------------------------- | ----------------------- |
| `SERVER_SSL_ENABLED`            | Enable HTTPS (true/false)        | `true`                  |
| `SERVER_SSL_KEY_STORE`          | Path to SSL certificate keystore | `/etc/ssl/keystore.p12` |
| `SERVER_SSL_KEY_STORE_PASSWORD` | Password for keystore            | `keystore-password`     |
| `SERVER_SSL_KEY_STORE_TYPE`     | Keystore format                  | `PKCS12`                |

### Port Configuration

| Variable      | Description             | Default Value |
| ------------- | ----------------------- | ------------- |
| `SERVER_PORT` | Application server port | `8082`        |

---

## Logging Configuration

| Variable                    | Description                        | Default Value |
| --------------------------- | ---------------------------------- | ------------- |
| `LOGGING_LEVEL_ROOT`        | Root logging level                 | `INFO`        |
| `LOGGING_LEVEL_APPLICATION` | Application-specific logging level | `INFO`        |

**Allowed values**: `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`

---

## Complete Environment Variable Template

### Development Environment (.env file)

```properties
# Database Configuration
MYSQL_URL=jdbc:mysql://localhost:3306/dbfms?useSSL=true&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=false
MYSQL_USER=root
MYSQL_PASSWORD=your-local-password

# JWT Configuration
JWT_SECRET=your-dev-secret-key-minimum-32-characters-long
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server Configuration
SERVER_PORT=8082
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APPLICATION=DEBUG
```

### Production Environment

```properties
# Database Configuration (MUST use non-root user and strong password)
MYSQL_URL=jdbc:mysql://prod-db-host:3306/dbfms_prod?useSSL=true&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=false
MYSQL_USER=inventory_service_prod
MYSQL_PASSWORD=<STRONG_PASSWORD_FROM_SECRETS_MANAGER>

# JWT Configuration (Use strong random secret)
JWT_SECRET=<GENERATED_SECRET_FROM_KEYGEN_TOOL>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Server Configuration (Enable HTTPS)
SERVER_PORT=8082
SERVER_SSL_ENABLED=true
SERVER_SSL_KEY_STORE=/etc/ssl/private/keystore.p12
SERVER_SSL_KEY_STORE_PASSWORD=<PASSWORD_FROM_SECRETS_MANAGER>
SERVER_SSL_KEY_STORE_TYPE=PKCS12

# Logging Configuration (PRODUCTION MUST BE INFO OR HIGHER)
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_APPLICATION=INFO
```

---

## How to Set Environment Variables

### Windows Command Prompt

```batch
set MYSQL_PASSWORD=your-password
set JWT_SECRET=your-secret-key
java -jar inventory-service-1.0.0.jar
```

### Windows PowerShell

```powershell
$env:MYSQL_PASSWORD="your-password"
$env:JWT_SECRET="your-secret-key"
java -jar inventory-service-1.0.0.jar
```

### Linux/Mac Bash

```bash
export MYSQL_PASSWORD="your-password"
export JWT_SECRET="your-secret-key"
java -jar inventory-service-1.0.0.jar
```

### Docker

```dockerfile
ENV MYSQL_URL=jdbc:mysql://db:3306/dbfms?useSSL=true
ENV MYSQL_USER=inventory_user
ENV MYSQL_PASSWORD=your-password
ENV JWT_SECRET=your-secret-key
```

### Docker Compose

```yaml
environment:
  MYSQL_URL: jdbc:mysql://db:3306/dbfms?useSSL=true
  MYSQL_USER: inventory_user
  MYSQL_PASSWORD: your-password
  JWT_SECRET: your-secret-key
  JWT_EXPIRATION: "86400000"
```

### CI/CD Pipeline (GitHub Actions Example)

```yaml
- name: Run Backend Tests
  env:
    MYSQL_PASSWORD: ${{ secrets.DB_PASSWORD }}
    JWT_SECRET: ${{ secrets.JWT_SECRET }}
  run: mvn clean test
```

---

## API Authentication

### Obtaining a JWT Token

**Endpoint**: `POST /api/v1/auth/login`

**Request**:

```json
{
  "username": "admin",
  "password": "your-password"
}
```

**Response** (Success):

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

### Using JWT Token in API Requests

Include the token in the `Authorization` header with `Bearer` prefix:

```bash
curl -H "Authorization: Bearer <your-jwt-token>" \
  http://localhost:8082/api/v1/suppliers
```

---

## Security Checklist

- [x] JWT_SECRET is at least 32 characters long
- [x] JWT_SECRET is stored securely (not in source code)
- [x] MYSQL_PASSWORD is strong (minimum 12 characters, uppercase, lowercase, numbers, symbols)
- [x] MYSQL_USER is not 'root' in production
- [x] SSL/TLS is enabled for database connections (useSSL=true)
- [x] HTTPS/SSL is enabled for production server (SERVER_SSL_ENABLED=true)
- [x] Logging level is INFO or higher in production (never DEBUG)
- [x] All environment variables are set before starting the application

---

## Troubleshooting

### Error: "JWT_SECRET environment variable is not configured"

**Cause**: JWT_SECRET is not set in environment variables  
**Solution**: Set JWT_SECRET before starting the application:

```bash
export JWT_SECRET="your-32+ character secret"
java -jar inventory-service-1.0.0.jar
```

### Error: "Access denied for user 'root'@'localhost'"

**Cause**: MYSQL_PASSWORD is incorrect or not set  
**Solution**: Verify MYSQL_USER and MYSQL_PASSWORD are correct:

```bash
export MYSQL_PASSWORD="your-actual-password"
java -jar inventory-service-1.0.0.jar
```

### Error: "Connections could not be acquired from the underlying database"

**Cause**: Database URL, username, or password is incorrect  
**Solution**: Test database connectivity:

```bash
mysql -h localhost -u inventory_user -p
```

---

## References

- [Spring Boot Environment Variables](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [JWT Introduction](https://jwt.io/introduction)
- [MySQL SSL Connection](https://dev.mysql.com/doc/mysql-shell/8.0/en/mysql-shell-encrypted-connections.html)
