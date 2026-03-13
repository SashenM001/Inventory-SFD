# Railway MySQL Connection Troubleshooting

## Error: "Access denied for user 'root'@'...' (using password: YES)"

This error means your app is trying to connect to MySQL but **the password is wrong**.

---

## ⚠️ CRITICAL ISSUE

**The Issue:** Railway generates a unique MySQL password automatically when you create a MySQL service. This password is NOT the local default (`Kanil12Mysql22_`). You MUST find it and set it correctly.

**Why it fails:**
- ❌ Local app.properties has: `spring.datasource.password=Kanil12Mysql22_`
- ✅ Railway MySQL actually has: `MYSQL_PASSWORD=SomeRandomString123456`
- Result: Connection rejected because password doesn't match

---

## 🔧 QUICK FIX (3 Steps)

### Step 1: Find Your Railway MySQL Password

```
1. Go to https://railway.app
2. Open your project
3. Click on the MySQL service box
4. Go to "Variables" tab
5. Look for MYSQL_PASSWORD variable
6. Copy the VALUE (the long string)
```

Example what you're looking for:
```
MYSQL_PASSWORD = dGh1c2lzQWZha2VQYXNzd29yZExsb28=
```

### Step 2: Update Your App Service Variables

```
1. In railway.app, click on your APP SERVICE (not MySQL)
2. Go to "Variables" tab
3. Find or add: MYSQL_PASSWORD
4. Paste the value you copied from Step 1
5. Click Save
```

### Step 3: Redeploy

```
1. Go to your App service
2. Click "Deploy" and select latest commit
3. Wait for deployment to complete
4. Check the logs for "started" message
```

---

## 🔍 Verify Your Setup

After the fix, verify these 5 things in Railway:

| Check | Where to Find | Expected Value |
|-------|--------------|-----------------|
| **MySQL Service Exists** | Project canvas | Should see MySQL box |
| **MySQL is Running** | MySQL service status | Green checkmark |
| **App Service Variables** | App Variables tab | See MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD |
| **Variables Match** | App Variables vs MySQL Variables | MYSQL_PASSWORD values should match exactly |
| **Port is Exposed** | App settings | Should have Port 8082 exposed |

---

## 🧪 Test After Fixing

Once deployed, test your endpoints:

```bash
# Get a JWT token
curl -X POST http://your-railway-app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "your_password"}'

# Should return: { "accessToken": "eyJ0eXAi...", ... }
```

If you get a response with a token, you're connected! ✅

---

## 📋 All Required Variables in App Service

Make sure your **App Service** (not MySQL) has these variables:

```
MYSQL_URL=jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
MYSQL_USER=root
MYSQL_PASSWORD=<FROM_RAILWAY_MYSQL_SERVICE>
JWT_SECRET=<YOUR_32_CHARACTER_SECRET_KEY>
```

---

## ❓ Still Getting "Access denied"?

**Check these:**

1. **Did you copy the full MYSQL_PASSWORD value?**
   - It should be a long string (often 20+ characters)
   - Make sure you didn't leave off the last character

2. **Did you check MySQL service is healthy?**
   - Go to MySQL service in Railway
   - Look for green checkmark
   - Check "Logs" tab for any MySQL startup errors

3. **Did you redeploy after changing variables?**
   - Just setting variables won't help
   - Must trigger a new deployment
   - Go to App → Deploy → Select latest commit

4. **Is the database name correct?**
   - Default is usually `railway`
   - If you created a different database, use that name instead
   - Update MYSQL_URL accordingly

5. **Check the app logs:**
   - Go to App service → Logs
   - Should see: "Successfully initialized..."
   - If still shows "Access denied", the password is still wrong

---

## 🆘 If Nothing Works

Last resort troubleshooting:

1. **Delete MySQL service and recreate it:**
   - This will generate a new password
   - Copy the new password to your app variables
   - Redeploy

2. **Check MySQL connectivity directly:**
   ```bash
   # From your Railway MySQL service logs
   # You should see connection accepted messages
   ```

3. **Verify JDBC URL format:**
   ```
   ✅ Correct: jdbc:mysql://mysql.railway.internal:3306/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=false
   ❌ Wrong: jdbc:mysql://localhost:3306/railway
   ❌ Wrong: jdbc:mysql://10.216.72.187:3306/railway (hardcoded IP)
   ```

---

## 📝 Remember

- **Local development:** Uses defaults from `application.properties`
- **Railway production:** MUST use environment variables set in Railway dashboard
- **Most common mistake:** Forgetting to copy the EXACT MYSQL_PASSWORD value from Railway MySQL service
- **Always redeploy:** Variable changes don't take effect until you redeploy your app
