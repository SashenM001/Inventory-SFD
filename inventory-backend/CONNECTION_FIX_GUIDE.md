## 🔧 DATABASE CONNECTION FIX - STEP-BY-STEP GUIDE

**Problem:** Tables not appearing in MySQL Workbench  
**Solution:** Follow these exact steps to fix the connection and setup

---

## ✅ STEP 1: TEST MYSQL CONNECTION (2 minutes)

### Test 1: Verify MySQL is Running

**Windows:**
```powershell
# Open PowerShell as Administrator and run:
Get-Service | Where-Object {$_.Name -like "*MySQL*"} | Select-Object Status, Name

# Should show: Running    MySQL80
```

If NOT running, start it:
```powershell
Start-Service MySQL80
```

---

### Test 2: Test Connection from Command Line

```bash
# Open Command Prompt or PowerShell and run:
mysql -u root -p

# Enter password: Kanil12Mysql22_

# If successful, you'll see:
# mysql>
```

If you get "Access Denied" error → **Wrong password**
If you get "Connection refused" → **MySQL not running**

---

## ✅ STEP 2: REFRESH MYSQL WORKBENCH CONNECTION

1. **Open MySQL Workbench**
2. **Find your connection** (usually "Local instance MySQL80" or similar)
3. **Right-click** → **Edit Connection**
4. **Verify these settings:**
   ```
   Connection Method: Standard (TCP/IP)
   Hostname: 127.0.0.1
   Port: 3306
   Username: root
   Password: Kanil12Mysql22_
   ```
5. **Click "Test Connection"**
6. Should show: ✅ **Successfully made the MySQL connection**
7. **Click OK**

If it fails:
- Check if MySQL Service is running
- Check if port 3306 is available
- Verify password

---

## ✅ STEP 3: EXECUTE DATABASE SETUP (5 minutes)

### Option A: Using MySQL Workbench (Recommended)

1. **In MySQL Workbench, click "File"** → **"New Query Tab"**

2. **Copy the ENTIRE contents of:**
   ```
   DATABASE_SETUP_VERIFIED.sql
   ```
   (Use this file, NOT DATABASE_SETUP_ENHANCED.sql)

3. **Paste into the query editor**

4. **Press Ctrl+A** to select all

5. **Press Ctrl+Enter** to execute

6. **Watch the Output Panel** - should show:
   ```
   Query 1 CREATE DATABASE IF NOT EXISTS dbfms ... OK, 0 rows affected
   Query 2 USE dbfms ... OK
   Query 3 CREATE TABLE IF NOT EXISTS inv_user ... OK, 0 rows affected
   ... (more tables) ...
   ```

7. **Scroll down to see verification results:**
   ```
   Current Database: dbfms
   Total Tables: 6
   TABLE_NAME
   inv_user
   inv_supplier
   inv_item
   inv_item_addition
   inv_item_issue
   inv_stock_audit
   ```

✅ **If you see all 6 tables - SUCCESS!**

---

### Option B: Using Command Line (Alternative)

```bash
# Open Command Prompt in the inventory-backend directory

cd "d:\FMS Project\Inventory_SFD - Copy\inventory-backend"

# Execute the SQL file
mysql -u root -p"Kanil12Mysql22_" < DATABASE_SETUP_VERIFIED.sql

# You should see:
# Query OK...
# Query OK...
# (multiple confirmations)
```

---

## ✅ STEP 4: VERIFY SETUP IN MYSQL WORKBENCH (3 minutes)

After execution, verify in MySQL Workbench:

### Check 1: Verify Database Exists
```sql
SHOW DATABASES;
```
Should see: **dbfms** in the list

### Check 2: Verify All Tables Created
```sql
USE dbfms;
SHOW TABLES;
```
Should see all 6 tables:
- inv_user
- inv_supplier
- inv_item
- inv_item_addition
- inv_item_issue
- inv_stock_audit

### Check 3: Verify Data is Loaded
```sql
USE dbfms;
SELECT COUNT(*) as user_count FROM inv_user;
SELECT COUNT(*) as supplier_count FROM inv_supplier;
SELECT COUNT(*) as item_count FROM inv_item;
SELECT COUNT(*) as addition_count FROM inv_item_addition;
SELECT COUNT(*) as issue_count FROM inv_item_issue;
```

Expected results:
```
user_count: 2
supplier_count: 3
item_count: 12
addition_count: 12
issue_count: 10
```

### Check 4: View User Accounts
```sql
SELECT id, full_name, email, role FROM inv_user;
```
Should show:
```
1 | Admin User | admin1@sfd.com | admin
2 | Store Keeper | storekeeper1@sfd.com | storekeeper
```

✅ **If you see all this data - DATABASE IS WORKING!**

---

## ✅ STEP 5: FIX BACKEND CONNECTION (2 minutes)

Verify your backend's `application.properties` file:

**File:** `inventory-backend/src/main/resources/application.properties`

```properties
# Should have these exact settings:
spring.datasource.url=jdbc:mysql://localhost:3306/dbfms?useSSL=false&serverTimezone=Asia/Colombo
spring.datasource.username=root
spring.datasource.password=Kanil12Mysql22_
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

⚠️ **IMPORTANT:** If you changed the password, update it here too!

---

## ✅ STEP 6: TEST BACKEND CONNECTION (2 minutes)

1. **Open terminal** in `inventory-backend` folder

2. **Run:**
   ```bash
   mvn clean install
   ```

3. **Watch for:**
   ```
   INFO: HikariPool-1 - Starting...
   INFO: HikariPool-1 - Start completed.
   ```

4. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

5. **Open browser** and test API:
   ```
   http://localhost:8082/api/suppliers
   ```

6. **Should return JSON with 3 suppliers:**
   ```json
   [
     {
       "id": 1,
       "name": "TechPro Supplies",
       "contactPerson": "John Smith",
       ...
     },
     ...
   ]
   ```

✅ **If you see supplier data - BACKEND IS CONNECTED!**

---

## 🚨 TROUBLESHOOTING

### Problem 1: "Access Denied for user 'root'"

**Solution:**
```bash
# Reset password (Windows)
mysql -u root -p
# Enter WRONG password, then type:
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Kanil12Mysql22_';
FLUSH PRIVILEGES;
```

---

### Problem 2: "Cannot connect to MySQL Server"

**Solution:**
```powershell
# Check if MySQL is running
Get-Service MySQL80

# If not running, start it
Start-Service MySQL80

# Verify it's listening on port 3306
netstat -an | findstr 3306
```

---

### Problem 3: "Database 'dbfms' doesn't exist"

**Solution:**
- Re-run the entire DATABASE_SETUP_VERIFIED.sql script
- Make sure you see "Query OK" messages
- Verify with `SHOW DATABASES;`

---

### Problem 4: "Tables exist but data is empty"

**Solution:**
```sql
-- Run this to see if data was inserted
USE dbfms;
SELECT COUNT(*) FROM inv_item;

-- If count is 0, re-run the INSERT statements from STEP 11-13
-- Copy the INSERT statements from DATABASE_SETUP_VERIFIED.sql and re-execute
```

---

### Problem 5: "Java Connection Error in Backend"

**Solution:**
1. Check password in `application.properties` is correct
2. Verify MySQL is running: `Get-Service MySQL80`
3. Test connection: `mysql -u root -p -h 127.0.0.1`
4. Check if database exists: `SHOW DATABASES;`
5. Restart backend after making changes

---

## ✅ COMPLETE VERIFICATION CHECKLIST

Run this checklist to confirm everything is working:

- [ ] MySQL service is running (`Get-Service MySQL80` shows "Running")
- [ ] MySQL Workbench connects successfully
- [ ] Database `dbfms` exists
- [ ] All 6 tables exist (verified with `SHOW TABLES;`)
- [ ] inv_user has 2 records
- [ ] inv_supplier has 3 records
- [ ] inv_item has 12 records
- [ ] inv_item_addition has 12 records
- [ ] inv_item_issue has 10 records
- [ ] admin1@sfd.com account exists
- [ ] storekeeper1@sfd.com account exists
- [ ] Backend application.properties has correct password
- [ ] Backend starts with "HikariPool-1 - Start completed" message
- [ ] API endpoint `/api/suppliers` returns 3 suppliers as JSON
- [ ] Total inventory value is ~LKR 348,000

✅ **If ALL checked - YOUR DATABASE IS WORKING PERFECTLY!**

---

## 🎯 QUICK SUMMARY

| Step | Action | Time | Status |
|------|--------|------|--------|
| 1 | Test MySQL connection | 2 min | ✓ |
| 2 | Refresh Workbench connection | 2 min | ✓ |
| 3 | Execute DATABASE_SETUP_VERIFIED.sql | 5 min | ✓ |
| 4 | Verify setup with test queries | 3 min | ✓ |
| 5 | Fix backend properties | 2 min | ✓ |
| 6 | Test API endpoints | 2 min | ✓ |
| **TOTAL** | **Complete Database Connection** | **~16 min** | **✓** |

---

## 📝 IF SETUP STILL FAILS

**Collect this information and check:**

1. **MySQL Version:**
   ```sql
   SELECT VERSION();
   ```

2. **Current User:**
   ```sql
   SELECT USER();
   ```

3. **Database Exists:**
   ```sql
   SHOW DATABASES LIKE 'dbfms';
   ```

4. **Tables Created:**
   ```sql
   USE dbfms;
   SHOW TABLES;
   ```

5. **Error Message:** Take a screenshot of any error

---

## 💡 PRO TIPS

**Tip 1:** Always use `DATABASE_SETUP_VERIFIED.sql` (not the Enhanced version)  
**Tip 2:** If things go wrong, delete the database and start fresh:
```sql
DROP DATABASE dbfms;
-- Then re-run DATABASE_SETUP_VERIFIED.sql
```

**Tip 3:** Bookmark this guide for future reference  
**Tip 4:** Keep the password `Kanil12Mysql22_` consistent across files  

---

**Last Updated:** February 15, 2026  
**Status:** Ready to Execute  
**Expected Time:** 15-20 minutes  

**Start with Step 1 now! ✅**
