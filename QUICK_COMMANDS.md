# Quick Commands Reference

## 🚀 Getting Started

### 1. Create Database (One-time Setup)

```bash
mysql -u root -p
CREATE DATABASE dbfms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 2. Configure Database Credentials

```bash
# Edit this file and update your MySQL password
code inventory-backend/src/main/resources/application.properties

# Or just open it in your text editor:
# spring.datasource.password=YOUR_PASSWORD
```

### 3. Build & Run Backend

```bash
cd inventory-backend
mvn clean install
mvn spring-boot:run
```

**Expected Output:**

```
o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8082
```

### 4. Test API is Running

```bash
# Option 1: Browser
http://localhost:8082/api/v1/suppliers

# Option 2: curl
curl http://localhost:8082/api/v1/suppliers

# Option 3: PowerShell
Invoke-WebRequest -Uri http://localhost:8082/api/v1/suppliers
```

---

## 🛠️ Common Tasks

### Build Project

```bash
cd inventory-backend
mvn clean install
```

### Run Application

```bash
# Method 1: Via Maven
mvn spring-boot:run

# Method 2: Run JAR directly
mvn clean package
java -jar target/inventory-service-1.0.0.jar

# Method 3: From IDE
Open InventoryServiceApplication.java and click Run
```

### Check Logs

```bash
# View logs in real-time while running
tail -f application.log

# Or Windows
Get-Content application.log -Wait
```

### Stop Application

```bash
# Press Ctrl+C in terminal
# Or if running in background:
kill -9 [PID]

# Windows:
taskkill /PID [PID] /F
```

---

## 📋 API Testing Commands

### 1. Get All Suppliers

```bash
curl -X GET http://localhost:8082/api/v1/suppliers
```

### 2. Create Supplier

```bash
curl -X POST http://localhost:8082/api/v1/suppliers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Supplier",
    "contactPerson": "John Doe",
    "phoneNumber": "0112345678",
    "email": "john@example.com",
    "address": "123 Main St",
    "city": "Colombo",
    "zipCode": "00700"
  }'
```

### 3. Create Item

```bash
curl -X POST http://localhost:8082/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Casket",
    "sku": "CASKET-001",
    "category": "Casket Items",
    "quantity": 10,
    "minQuantity": 3,
    "price": 150000.00,
    "supplierId": 1,
    "unit": "pieces",
    "description": "High-quality casket"
  }'
```

### 4. Issue Item

```bash
curl -X POST http://localhost:8082/api/v1/issues \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "quantity": 2,
    "issuedTo": "Service Team",
    "reason": "Funeral service",
    "notes": "Premium client"
  }'
```

### 5. Add Stock

```bash
curl -X POST http://localhost:8082/api/v1/additions \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "quantity": 5,
    "supplierId": 1,
    "notes": "Restocking"
  }'
```

### 6. Get Low Stock Items

```bash
curl -X GET http://localhost:8082/api/v1/items/low-stock
```

### 7. Search Items

```bash
curl -X GET "http://localhost:8082/api/v1/items/search?searchTerm=casket"
```

### 8. Get Items by Category

```bash
curl -X GET "http://localhost:8082/api/v1/items/category/Wear Items"
```

### 9. Recent Issues

```bash
curl -X GET http://localhost:8082/api/v1/issues/recent
```

### 10. Recent Additions

```bash
curl -X GET http://localhost:8082/api/v1/additions/recent
```

---

## 🗄️ Database Commands

### Connect to MySQL

```bash
mysql -u root -p
```

### View All Databases

```sql
SHOW DATABASES;
```

### Select Inventory Database

```sql
USE dbfms;
```

### View All Tables

```sql
SHOW TABLES;
```

### Check Table Structure

```sql
DESCRIBE inv_item;
DESCRIBE inv_supplier;
DESCRIBE inv_item_issue;
DESCRIBE inv_item_addition;
```

### Count Records

```sql
SELECT COUNT(*) FROM inv_item;
SELECT COUNT(*) FROM inv_supplier;
SELECT COUNT(*) FROM inv_item_issue;
SELECT COUNT(*) FROM inv_item_addition;
```

### View All Items with Suppliers

```sql
SELECT i.name, i.sku, i.quantity, s.name as supplier
FROM inv_item i
LEFT JOIN inv_supplier s ON i.supplier_id = s.id;
```

### View Low Stock Items

```sql
SELECT name, quantity, min_quantity
FROM inv_item
WHERE quantity <= min_quantity;
```

### View Recent Transactions

```sql
SELECT i.name, iss.quantity, iss.issued_to, iss.created_at
FROM inv_item_issue iss
LEFT JOIN inv_item i ON iss.item_id = i.id
ORDER BY iss.created_at DESC LIMIT 10;
```

### Delete All Data (Reset)

```sql
DELETE FROM inv_item_issue;
DELETE FROM inv_item_addition;
DELETE FROM inv_item;
DELETE FROM inv_supplier;
```

---

## 🔧 Troubleshooting Commands

### Check if Port 8082 is in Use

```bash
# Windows
netstat -ano | findstr :8082

# Linux/Mac
lsof -i :8082
```

### Kill Process on Port 8082

```bash
# Windows
taskkill /PID [PID_NUMBER] /F

# Linux/Mac
kill -9 [PID_NUMBER]
```

### Verify MySQL is Running

```bash
# Windows
Get-Service | findstr MySQL

# Linux
systemctl status mysql

# Mac
brew services list
```

### Start MySQL Service

```bash
# Windows
net start MySQL80

# Linux
sudo systemctl start mysql

# Mac
brew services start mysql
```

### Check Maven Installation

```bash
mvn --version
```

### Check Java Installation

```bash
java -version
```

### Clean Maven Cache

```bash
mvn clean
rm -rf ~/.m2/repository
mvn install
```

---

## 📁 File Locations

### Important Files

```
inventory-backend/
├── pom.xml                                    # Maven dependencies
├── src/main/resources/application.properties  # Database config
├── README.md                                  # API docs
├── QUICKSTART.md                             # Setup guide
└── DATABASE_SETUP.sql                        # SQL script
```

### Java Source Files

```
src/main/java/com/supremefuneralx/
├── InventoryServiceApplication.java
├── controller/                  # REST endpoints
├── service/                     # Business logic
├── repository/                  # Data access
├── entity/                      # Database models
├── dto/                         # API contracts
└── exception/                   # Error handling
```

---

## 🔍 Debugging Tips

### Enable Debug Logging

Add to application.properties:

```properties
logging.level.root=DEBUG
logging.level.com.supremefuneralx=DEBUG
```

### View SQL Queries

Add to application.properties:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Check Application Properties

```bash
# View current config
cat src/main/resources/application.properties
```

### Verify Entities are Mapped

Look for messages like:

```
HHH000412: Hibernate is building a SessionFactory using the JpaTransactionManager
```

### Check Table Creation

After starting app, run:

```sql
SHOW TABLES LIKE 'inv_%';
```

---

## 📊 Development Workflow

### 1. Start Backend

```bash
cd inventory-backend
mvn spring-boot:run
# Leave running in separate terminal
```

### 2. Start Frontend (In another terminal)

```bash
cd inventory-hub
npm run dev
```

### 3. Open in Browser

```
Frontend: http://localhost:8081
Backend API: http://localhost:8082/api/v1
```

### 4. Test Integration

- Open React app
- Try creating a supplier
- Check backend logs for SQL queries
- Verify data appears in MySQL

---

## 🚀 Deployment

### Build for Production

```bash
mvn clean package
```

### Run JAR

```bash
java -jar target/inventory-service-1.0.0.jar \
  --spring.datasource.password=prod_password \
  --server.port=8082
```

### Run with Different Port

```bash
java -jar target/inventory-service-1.0.0.jar \
  --server.port=9000
```

### Run with Docker

```bash
# Build image
docker build -t inventory-service:1.0 .

# Run container
docker run -d -p 8082:8082 \
  --name inventory-service \
  -e SPRING_DATASOURCE_PASSWORD=password \
  inventory-service:1.0
```

---

## 📞 Quick Help

### Get API Version

```bash
curl http://localhost:8082/api/v1/suppliers | head -c 100
```

### Check if Backend is Responsive

```bash
curl -X OPTIONS http://localhost:8082/api/v1/items
```

### View All Endpoints (Spring Actuator)

```bash
# Add to pom.xml:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

# Then access:
curl http://localhost:8082/actuator
```

---

## 🎯 Useful URLs

### Frontend

- App: http://localhost:8081
- Inventory: http://localhost:8081/inventory
- Suppliers: http://localhost:8081/suppliers
- Summary: http://localhost:8081/suppliers/summary

### Backend

- Suppliers: http://localhost:8082/api/v1/suppliers
- Items: http://localhost:8082/api/v1/items
- Issues: http://localhost:8082/api/v1/issues
- Additions: http://localhost:8082/api/v1/additions

### Documentation

- API Docs: `README.md`
- Setup Guide: `QUICKSTART.md`
- Database: `DATABASE_SETUP.sql`

---

**Quick Ref Version**: 1.0  
**Last Updated**: January 24, 2026
