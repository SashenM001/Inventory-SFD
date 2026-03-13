# Railway Deployment Checklist

Complete this checklist before and after deploying to Railway.

## Before Deployment

- [ ] Application builds locally: `mvn clean package -DskipTests` ✅
- [ ] JAR file exists: `target/inventory-service-1.0.0.jar`
- [ ] Latest code pushed to main branch: `git log` shows your commits
- [ ] Dockerfile exists at: `inventory-backend/Dockerfile`
- [ ] Java 21 specified in Dockerfile: `FROM eclipse-temurin:21-jre-alpine`
- [ ] JWT_SECRET generated (32+ characters): Generate with `openssl rand -base64 32`

## Railway Setup

### Step 1: Create MySQL Service

- [ ] Go to https://railway.app and select your project
- [ ] Click "Add Service" → "Add from Marketplace" → "MySQL"
- [ ] Wait for MySQL to initialize (green checkmark indicates ready)

### Step 2: Get MySQL Credentials

- [ ] Click on MySQL service box in project canvas
- [ ] Go to "Variables" tab
- [ ] Record these values:
  - `MYSQL_HOST` = ___________________
  - `MYSQL_PORT` = ___________________
  - `MYSQL_DATABASE` = ___________________
  - `MYSQL_USER` = ___________________  
  - `MYSQL_PASSWORD` = ___________________ (⚠️ COPY EXACTLY)

### Step 3: Configure App Service

- [ ] Click on your App service (or create new service from GitHub repo)
- [ ] Go to "Variables" tab
- [ ] Add these environment variables:

```
MYSQL_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
MYSQL_USER=root
MYSQL_PASSWORD=<PASTE_VALUE_FROM_STEP_2>
JWT_SECRET=<YOUR_32_CHARACTER_SECRET>
PORT=8082
```

- [ ] Verify each variable is set correctly:
  - ✅ MYSQL_URL includes `serverTimezone=UTC`
  - ✅ MYSQL_USER matches Step 2 value
  - ✅ MYSQL_PASSWORD matches EXACTLY from Step 2
  - ✅ JWT_SECRET is 32+ characters
  - ✅ PORT is set to 8082

### Step 4: Deploy

- [ ] Make sure latest code is on main branch: `git push origin main`
- [ ] In Railway, click "Deploy" → select latest commit
- [ ] Wait for deployment to complete (usually 2-3 minutes)
- [ ] Watch the logs - you should see "Started InventoryServiceApplication"

## After Deployment ✅

### Health Checks

- [ ] Go to App service → "Logs" tab
- [ ] Look for this message: `Started InventoryServiceApplication in X seconds`
- [ ] Should NOT see: `Access denied` or `Unable to connect`
- [ ] Port should be exposed: Check App settings → Port 8082

### Test the API

- [ ] Get your Railway app URL: Find in App service settings
- [ ] Test login endpoint:
```bash
curl -X POST https://your-app-url.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "password"}'
```

- [ ] Should return: JSON with `accessToken` field
- [ ] Token should start with: `eyJ0eXAi...` (JWT format)

### Database Connection

- [ ] Go to MySQL service → inspect logs
- [ ] Should see client connection messages
- [ ] No "Access denied" errors

## Troubleshooting

If you see **"Access denied for user 'root'..."**:

1. ❗ **MOST LIKELY CAUSE:** MYSQL_PASSWORD doesn't match
   - Go back to MySQL service Variables
   - Copy the MYSQL_PASSWORD value again
   - Update your App service MYSQL_PASSWORD to match EXACTLY
   - Redeploy

2. Check MySQL service is running:
   - Should have green checkmark
   - Check MySQL logs for any errors

3. Verify variables in App service:
   - All 4 variables present: MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD, JWT_SECRET
   - No typos or extra spaces

4. Redeploy after fixing:
   - Variable changes don't take effect until redeploy
   - Click Deploy → select latest commit

---

**Need help?** See [RAILWAY_MySQL_TROUBLESHOOTING.md](./RAILWAY_MySQL_TROUBLESHOOTING.md) for detailed troubleshooting steps.
