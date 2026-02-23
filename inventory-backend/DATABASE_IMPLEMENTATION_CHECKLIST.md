// ============================================================================
// DATABASE SETUP AND IMPLEMENTATION CHECKLIST
// ============================================================================
// Project: Funeral Management System - Inventory Hub
// Date: February 15, 2026
// Version: 2.0
// ============================================================================

STEP-BY-STEP IMPLEMENTATION GUIDE
==================================

## PHASE 1: PRE-EXECUTION VERIFICATION ✓

### Prerequisites Check
- [ ] MySQL 8.0+ Server is installed and running
- [ ] Administrative access (root or equivalent) available
- [ ] Database creation privileges confirmed
- [ ] Network connection to MySQL server verified
- [ ] At least 100 MB free disk space available

### Test Connection (before running scripts)
```bash
# Test MySQL connectivity
mysql -h localhost -u root -p

# Verify version
SELECT VERSION();

# Check current databases
SHOW DATABASES;
```

---

## PHASE 2: DATABASE SETUP EXECUTION

### Step 1: Choose Setup Method

#### Option A: ENHANCED SETUP (Recommended - v2.0)
**File:** `DATABASE_SETUP_ENHANCED.sql`
**Includes:** User management, audit trail, complete sample data, optimized indexes
**Recommended For:** Production deployment

```bash
# Execute using MySQL CLI
mysql -h localhost -u root -p < DATABASE_SETUP_ENHANCED.sql

# Or using MySQL Workbench
# 1. Open MySQL Workbench
# 2. File → Open SQL Script
# 3. Select DATABASE_SETUP_ENHANCED.sql
# 4. Execute (Ctrl+Shift+Enter)
```

#### Option B: ORIGINAL SETUP (Legacy)
**File:** `DATABASE_SETUP.sql`
**Includes:** Core 4 tables, basic sample data
**Recommended For:** Backward compatibility only

```bash
mysql -h localhost -u root -p < DATABASE_SETUP.sql
```

### Step 2: Verify Setup Success

Run verification queries after setup:

```sql
-- Query 1: Count all records
SELECT 
    'inv_user' as table_name, COUNT(*) as count FROM inv_user
UNION ALL
SELECT 'inv_supplier', COUNT(*) FROM inv_supplier
UNION ALL
SELECT 'inv_item', COUNT(*) FROM inv_item
UNION ALL
SELECT 'inv_item_addition', COUNT(*) FROM inv_item_addition
UNION ALL
SELECT 'inv_item_issue', COUNT(*) FROM inv_item_issue
ORDER BY table_name;

-- Query 2: Display table structure
DESCRIBE inv_user;
DESCRIBE inv_supplier;
DESCRIBE inv_item;
DESCRIBE inv_item_addition;
DESCRIBE inv_item_issue;

-- Query 3: List all indexes
SHOW INDEXES FROM inv_item;
SHOW INDEXES FROM inv_supplier;

-- Query 4: Check foreign keys
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'dbfms' AND REFERENCED_TABLE_SCHEMA IS NOT NULL;

-- Query 5: Sample data retrieval
SELECT COUNT(*) as users FROM inv_user;
SELECT COUNT(*) as suppliers FROM inv_supplier;
SELECT COUNT(*) as items FROM inv_item;
SELECT SUM(quantity * price) as inventory_value FROM inv_item;
```

---

## PHASE 3: APPLICATION CONFIGURATION

### Step 3: Update Spring Boot Configuration

**File:** `inventory-backend/src/main/resources/application.properties`

```properties
# Verify these settings match your MySQL setup:
spring.datasource.url=jdbc:mysql://localhost:3306/dbfms?useSSL=false&serverTimezone=Asia/Colombo
spring.datasource.username=root
spring.datasource.password=Kanil12Mysql22_
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### Step 4: Build and Test Backend

```bash
# Navigate to backend directory
cd inventory-backend

# Clean build
mvn clean install

# Run Spring Boot application
mvn spring-boot:run

# Expected output:
# - Started InventoryServiceApplication in X.XXX seconds
# - No database errors in log output
# - API endpoints are accessible
```

### Step 5: Verify API Connectivity

```bash
# Test endpoints (assuming backend runs on port 8082)

# Get all suppliers
curl http://localhost:8082/api/suppliers

# Get all items
curl http://localhost:8082/api/items

# Get all users (if endpoint exposed)
curl http://localhost:8082/api/users

# Expected: JSON responses with sample data
```

---

## PHASE 4: FRONTEND INTEGRATION

### Step 6: Ensure Frontend is Connected

**File:** `inventory-hub/vite.config.ts`

Verify API base URL is correct:
```typescript
// Check that API calls point to correct backend
const API_BASE_URL = 'http://localhost:8082/api';
```

```bash
# Start frontend
cd inventory-hub
npm run dev

# Expected: 
# - Frontend runs on port 8085
# - No API connection errors in console
# - Dashboard loads with sample data
```

### Step 7: Test Full Integration

```
Frontend → Backend → Database Flow:
1. Open browser: http://localhost:8085
2. Login with:
   - Email: admin1@sfd.com
   - Password: Admin111_
3. Navigate to Inventory page
4. Verify items load from database
5. Navigate to Suppliers page
6. Verify suppliers load from database
7. Check Summary page
8. Verify transaction history displays
```

---

## PHASE 5: DATA VALIDATION

### Step 8: Validate Sample Data

```sql
-- Check user accounts created
SELECT full_name, email, role FROM inv_user;

-- Verify suppliers
SELECT name, contact_person, email FROM inv_supplier;

-- List all inventory items with values
SELECT 
    i.name,
    i.sku,
    i.category,
    i.quantity,
    i.price,
    (i.quantity * i.price) as total_value,
    s.name as supplier
FROM inv_item i
LEFT JOIN inv_supplier s ON i.supplier_id = s.id
ORDER BY i.category, i.name;

-- Check recent transactions
SELECT 
    'Addition' as type,
    ia.created_at,
    i.name,
    ia.quantity
FROM inv_item_addition ia
LEFT JOIN inv_item i ON ia.item_id = i.id
ORDER BY ia.created_at DESC
LIMIT 5;

SELECT 
    'Issue' as type,
    ii.created_at,
    i.name,
    ii.quantity
FROM inv_item_issue ii
LEFT JOIN inv_item i ON ii.item_id = i.id
ORDER BY ii.created_at DESC
LIMIT 5;

-- Financial summary
SELECT 
    COUNT(*) as total_items,
    SUM(quantity) as total_units,
    SUM(quantity * price) as total_value
FROM inv_item;
```

### Step 9: Verify Data Integrity

```sql
-- Check for orphaned records (should return nothing)
SELECT * FROM inv_item WHERE supplier_id NOT IN (SELECT id FROM inv_supplier);
SELECT * FROM inv_item_addition WHERE item_id NOT IN (SELECT id FROM inv_item);
SELECT * FROM inv_item_issue WHERE item_id NOT IN (SELECT id FROM inv_item);

-- Check unique constraints
SELECT COUNT(*) as duplicate_count FROM inv_item GROUP BY sku HAVING COUNT(*) > 1;
SELECT COUNT(*) as duplicate_count FROM inv_user GROUP BY email HAVING COUNT(*) > 1;

-- Verify all created_at and updated_at timestamps
SELECT MIN(created_at) as earliest, MAX(created_at) as latest FROM inv_item;
SELECT MIN(created_at) as earliest, MAX(created_at) as latest FROM inv_item_addition;
```

---

## PHASE 6: PERFORMANCE OPTIMIZATION

### Step 10: Optimize Database

```sql
-- Analyze table statistics
ANALYZE TABLE inv_user;
ANALYZE TABLE inv_supplier;
ANALYZE TABLE inv_item;
ANALYZE TABLE inv_item_addition;
ANALYZE TABLE inv_item_issue;

-- Optimize table space
OPTIMIZE TABLE inv_user;
OPTIMIZE TABLE inv_supplier;
OPTIMIZE TABLE inv_item;
OPTIMIZE TABLE inv_item_addition;
OPTIMIZE TABLE inv_item_issue;

-- Check table status
SHOW TABLE STATUS FROM dbfms;
```

### Step 11: Enable Query Monitoring

```sql
-- Enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- Enable general log (be careful - high overhead)
-- SET GLOBAL general_log = 'ON';

-- Check current queries
SHOW PROCESSLIST;

-- View slow queries after some activity
SELECT * FROM mysql.slow_log LIMIT 10;
```

---

## PHASE 7: SECURITY HARDENING

### Step 12: Create Database User (Production Only)

```sql
-- Create dedicated application user
CREATE USER 'inventory_app'@'localhost' IDENTIFIED BY 'Strong_Password_123!';

-- Grant necessary permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON dbfms.* TO 'inventory_app'@'localhost';

-- Verify permissions
SHOW GRANTS FOR 'inventory_app'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;
```

### Step 13: Update Application Configuration

```properties
# Update application.properties with new user
spring.datasource.username=inventory_app
spring.datasource.password=Strong_Password_123!
```

### Step 14: Secure Root User

```sql
-- Change root password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'New_Root_Password_123!';

-- Disable remote root login
DELETE FROM mysql.user WHERE user = 'root' AND host != 'localhost';

-- Flush privileges
FLUSH PRIVILEGES;
```

---

## PHASE 8: BACKUP SETUP

### Step 15: Create Backup Scripts

#### Daily Backup Script (backup_dbfms_daily.sh)
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/dbfms_$DATE.sql"

# Create directory if not exists
mkdir -p "$BACKUP_DIR"

# Perform backup
mysqldump -u root -p'password' dbfms > "$BACKUP_FILE"

# Compress backup (optional)
gzip "$BACKUP_FILE"

# Log backup
echo "Backup completed: $BACKUP_FILE.gz" >> $BACKUP_DIR/backup.log

# Keep only last 30 days
find "$BACKUP_DIR" -name "dbfms_*.sql.gz" -mtime +30 -delete
```

#### Weekly Backup Script
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/mysql/weekly"
DATE=$(date +%Y_Week_%U)
BACKUP_FILE="$BACKUP_DIR/dbfms_$DATE.sql"

mkdir -p "$BACKUP_DIR"
mysqldump -u root -p'password' --single-transaction -q dbfms > "$BACKUP_FILE"
gzip "$BACKUP_FILE"
```

### Step 16: Schedule Backups (Linux Cron)

```bash
# Edit crontab
crontab -e

# Add lines:
# Daily backup at 2 AM
0 2 * * * /scripts/backup_dbfms_daily.sh

# Weekly backup every Sunday at 3 AM
0 3 * * 0 /scripts/backup_dbfms_weekly.sh
```

---

## PHASE 9: TESTING & VALIDATION

### Step 17: Run Test Scenarios

#### Scenario 1: User Authentication
```
[ ] Admin login successful
[ ] StoreKeeper login successful  
[ ] Invalid credentials rejected
[ ] Session persists on page reload
```

#### Scenario 2: Inventory Management
```
[ ] View all items displays 12 items
[ ] Add item dialog works
[ ] Edit item updates database
[ ] Delete item removes from database
[ ] Low stock alert triggers correctly
```

#### Scenario 3: Supplier Management
```
[ ] View suppliers displays 3 suppliers
[ ] Add supplier works
[ ] Edit supplier updates
[ ] Delete supplier works (with constraint check)
[ ] Supplier-item relationship maintained
```

#### Scenario 4: Transaction Tracking
```
[ ] Add stock transaction creates record
[ ] Issue stock transaction creates record
[ ] Summary shows correct totals
[ ] Transaction history displays
[ ] Date filtering works correctly
```

#### Scenario 5: Role-Based Access
```
[ ] Admin can access Manage Employees
[ ] StoreKeeper cannot access Manage Employees
[ ] Admin can create new employees
[ ] Logout clears session
```

### Step 18: Load Testing

```bash
# Use Apache JMeter or similar tool to test database under load

# Test scenarios:
# - 100 concurrent users accessing inventory
# - 1000 database queries per second
# - Response time should be < 500ms

# Monitor:
# - CPU usage < 70%
# - Memory usage < 80%
# - Disk I/O steady
# - No query timeouts
```

---

## PHASE 10: DOCUMENTATION & MAINTENANCE

### Step 19: Create Documentation

- [ ] Database diagram created (use MySQL Workbench)
- [ ] Data dictionary compiled
- [ ] API documentation updated
- [ ] User manual updated with database info
- [ ] Troubleshooting guide completed
- [ ] Runbooks created for common operations

### Step 20: Setup Monitoring

```sql
-- Create monitoring view
CREATE VIEW v_inventory_summary AS
SELECT 
    COUNT(*) as total_items,
    SUM(quantity) as total_units,
    SUM(quantity * price) as total_value,
    SUM(CASE WHEN quantity <= min_quantity THEN 1 ELSE 0 END) as low_stock_items,
    SUM(CASE WHEN quantity = 0 THEN 1 ELSE 0 END) as out_of_stock_items
FROM inv_item;

-- Query to monitor view
SELECT * FROM v_inventory_summary;
```

---

## VERIFICATION CHECKLIST

### Database Structure
- [ ] All 6 tables created
- [ ] All columns present and correct data types
- [ ] Primary keys assigned
- [ ] Foreign keys established and working
- [ ] Indexes created for performance
- [ ] NOT NULL constraints applied
- [ ] UNIQUE constraints enforced

### Sample Data
- [ ] 2 users created
- [ ] 3 suppliers populated
- [ ] 12 inventory items loaded
- [ ] Transaction history populated (22 records)
- [ ] All relationships intact
- [ ] Data types match expected values

### Configuration
- [ ] application.properties updated correctly
- [ ] Database connection string valid
- [ ] Username and password correct
- [ ] JPA/Hibernate configuration active
- [ ] Spring Boot recognizes tables

### Functionality
- [ ] Frontend loads data successfully
- [ ] Backend API returns correct responses
- [ ] Authentication system works
- [ ] Role-based access control functional
- [ ] All CRUD operations work
- [ ] Filtering and sorting work
- [ ] Transactions log correctly

### Performance
- [ ] Queries execute in < 500ms
- [ ] Indexes being used (EXPLAIN analysis)
- [ ] No N+1 query problems
- [ ] Database connection pooling active
- [ ] Memory usage stable

### Security
- [ ] Root password changed
- [ ] Dedicated application user created
- [ ] Appropriate permissions granted
- [ ] Password hashing implemented (frontend)
- [ ] SQL injection protection active (parameterized queries)

### Backup
- [ ] Backup script created
- [ ] Cron job scheduled
- [ ] Test restore successful
- [ ] Backup location accessible
- [ ] Backup size monitored

---

## TROUBLESHOOTING GUIDE

### Issue: "Access Denied" when running script
```
Solution:
1. Verify MySQL is running: mysqld status
2. Check credentials in connection string
3. Verify user permissions: SHOW GRANTS FOR 'user'@'localhost';
4. Use correct password (special chars need escaping)
```

### Issue: "Table already exists" error
```
Solution:
Option 1: Drop existing database
  DROP DATABASE IF EXISTS dbfms;
  Then re-run setup script

Option 2: Skip table creation
  Edit script to remove CREATE TABLE IF NOT EXISTS... lines
  Or modify to use: CREATE TABLE IF NOT EXISTS...
```

### Issue: Foreign key constraint error
```
Solution:
1. Verify parent table records exist:
   SELECT * FROM inv_supplier WHERE id = X;
2. Check foreign key definition:
   SHOW CREATE TABLE inv_item;
3. Temporarily disable foreign keys for data import:
   SET FOREIGN_KEY_CHECKS=0;
   [Run import]
   SET FOREIGN_KEY_CHECKS=1;
```

### Issue: Slow query performance
```
Solution:
1. Analyze query: EXPLAIN SELECT ...;
2. Check if indexes are used
3. Run table optimization: OPTIMIZE TABLE inv_item;
4. Analyze statistics: ANALYZE TABLE inv_item;
5. Check slow query log: SHOW VARIABLES LIKE 'slow%';
```

### Issue: Application cannot connect to database
```
Solution:
1. Verify MySQL service running
2. Check connection string in application.properties
3. Test manual connection:
   mysql -h localhost -u root -p -e "USE dbfms; SHOW TABLES;"
4. Check firewall rules (port 3306 open)
5. Verify database user permissions
```

---

## SUCCESS CRITERIA

To consider setup complete and successful: ✅

- [x] Database created without errors
- [x] All 6 tables exist with correct structure
- [x] Sample data loaded (39+ records)
- [x] All relationships verified
- [x] Backend application starts without errors
- [x] Frontend successfully loads data
- [x] Authentication system functional
- [x] All CRUD operations work
- [x] Performance acceptable (< 500ms queries)
- [x] Backup strategy in place
- [x] Documentation complete
- [x] Team trained on operations

---

## NEXT STEPS

1. **Week 1:** Execute database setup and verification
2. **Week 1:** Configure backup procedures
3. **Week 2:** Load testing and performance optimization
4. **Week 2:** Security hardening (new user, passwords, SSL)
5. **Week 3:** Production deployment
6. **Week 3:** Team training
7. **Week 4:** Monitoring setup
8. **Month 2:** Quarterly maintenance review

---

## SUPPORT & ESCALATION

**For Setup Issues:**
- [ ] Check logs: `mysql.log`, `application.log`
- [ ] Run verification queries (listed above)
- [ ] Review troubleshooting section
- [ ] Contact: Database Administration Team

**For Performance Issues:**
- [ ] Run: `SHOW PROCESSLIST;`
- [ ] Check: `EXPLAIN [query];`
- [ ] Analyze: `ANALYZE TABLE ...;`
- [ ] Review: MySQL slow query log

**For Security Issues:**
- [ ] Immediately: Revoke suspicious credentials
- [ ] Check: SHOW GRANTS FOR users
- [ ] Review: MySQL access logs
- [ ] Contact: Security Team

---

## SIGN-OFF

**Implementation Date:** _______________  
**Implemented By:** _______________  
**Verified By:** _______________  
**Approved By:** _______________  

**Database Status After Implementation:**
- [ ] All tables created ✓
- [ ] Sample data loaded ✓
- [ ] Connections verified ✓
- [ ] Performance optimized ✓
- [ ] Backups configured ✓
- [ ] Security hardened ✓
- [ ] Documentation complete ✓
- [ ] Team trained ✓

**System Ready for Production:** _______________

---

*Database Setup Checklist - Version 2.0*  
*Generated: February 15, 2026*  
*Funeral Management System - Inventory Hub*

---
