# Quick Start Guide - Inventory Backend

## 1. Prerequisites

- Java 17+ installed
- Maven 3.8+ installed
- MySQL Server running
- Git (optional)

## 2. Database Setup (Required First)

### Create Database

```bash
# Open MySQL command line or MySQL Workbench
mysql -u root -p

# In MySQL prompt:
CREATE DATABASE IF NOT EXISTS dbfms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dbfms;

# The tables will be auto-created by Hibernate on first run
```

### Update Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dbfms?useSSL=false&serverTimezone=Asia/Colombo
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

## 3. Build & Run

### Option 1: Maven

```bash
cd inventory-backend
mvn clean install
mvn spring-boot:run
```

### Option 2: Maven Package + Java

```bash
cd inventory-backend
mvn clean package
java -jar target/inventory-service-1.0.0.jar
```

### Option 3: IDE (IntelliJ/Eclipse)

1. Open project in IDE
2. Right-click `InventoryServiceApplication.java`
3. Select "Run"

## 4. Verify It's Running

Visit: `http://localhost:8082`

You should see Spring Boot homepage or check logs for:

```
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8082
```

## 5. Test API Endpoints

### Using Postman

1. Create Supplier (POST):

```
URL: http://localhost:8082/api/v1/suppliers
Method: POST
Body (JSON):
{
  "name": "Test Supplier",
  "phoneNumber": "0112345678",
  "email": "supplier@example.com",
  "address": "123 Main St",
  "city": "Colombo"
}
```

2. Get All Suppliers (GET):

```
URL: http://localhost:8082/api/v1/suppliers
Method: GET
```

3. Create Item (POST):

```
URL: http://localhost:8082/api/v1/items
Method: POST
Body (JSON):
{
  "name": "Saree XL",
  "sku": "SAREE-XL-001",
  "category": "Wear Items",
  "quantity": 15,
  "minQuantity": 5,
  "price": 2500.00,
  "supplierId": 1,
  "unit": "pieces",
  "description": "Quality saree for funeral services"
}
```

## 6. Common Issues

### Issue: "Connection refused" to MySQL

**Solution**:

- Start MySQL service
- Check credentials in application.properties
- Verify dbfms database exists

### Issue: "Port 8082 already in use"

**Solution**:

- Change port in application.properties
- Or kill process: `lsof -ti:8082 | xargs kill -9`

### Issue: Tables not created

**Solution**:

- Check `spring.jpa.hibernate.ddl-auto=update` in properties
- Check logs for SQL errors
- Manually create dbfms database if not exists

## 7. Frontend Integration

Connect your React frontend to API:

```javascript
// .env or config file
REACT_APP_API_URL=http://localhost:8082/api/v1

// Usage in components
const response = await fetch(
  `${process.env.REACT_APP_API_URL}/items`
);
const data = await response.json();
```

## 8. Project Layout

```
inventory-backend/
├── pom.xml                          # Maven dependencies
├── src/main/
│   ├── java/com/supremefuneralx/
│   │   ├── entity/                  # JPA Entities
│   │   ├── repository/              # Data access
│   │   ├── service/                 # Business logic
│   │   ├── controller/              # REST endpoints
│   │   ├── dto/                     # Transfer objects
│   │   └── exception/               # Error handling
│   └── resources/
│       └── application.properties   # Configuration
├── README.md                        # Full documentation
└── QUICKSTART.md                   # This file
```

## 9. API Overview

**Base URL**: `http://localhost:8082/api/v1`

### Suppliers

- `POST /suppliers` - Create supplier
- `GET /suppliers` - List suppliers
- `GET /suppliers/{id}` - Get supplier
- `PUT /suppliers/{id}` - Update
- `DELETE /suppliers/{id}` - Delete
- `GET /suppliers/search?searchTerm=...` - Search

### Items

- `POST /items` - Create item
- `GET /items` - List items
- `GET /items/{id}` - Get item
- `GET /items/category/{category}` - By category
- `GET /items/low-stock` - Low stock items
- `PUT /items/{id}` - Update
- `PATCH /items/{id}/quantity?adjustment=5` - Adjust qty
- `DELETE /items/{id}` - Delete
- `GET /items/search?searchTerm=...` - Search

### Issues (Outflows)

- `POST /issues` - Issue item (decrease stock)
- `GET /issues` - List issues
- `GET /issues/recent` - Recent issues
- `GET /issues/item/{itemId}` - Issues for item
- `GET /issues/range?startDate=...&endDate=...` - Date range

### Additions (Inflows)

- `POST /additions` - Add stock (increase)
- `GET /additions` - List additions
- `GET /additions/recent` - Recent additions
- `GET /additions/item/{itemId}` - Additions for item
- `GET /additions/range?startDate=...&endDate=...` - Date range

## 10. Sample Data Setup Script

Create initial data with these API calls:

```bash
#!/bin/bash

API="http://localhost:8082/api/v1"

# Create Supplier
SUPPLIER=$(curl -X POST "$API/suppliers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Supplies Ltd",
    "contactPerson": "John Doe",
    "phoneNumber": "+94-11-2345678",
    "email": "john@premiumsupplies.com",
    "address": "123 Business St, Colombo",
    "city": "Colombo",
    "zipCode": "00700"
  }')

echo "Created Supplier: $SUPPLIER"

# Extract supplier ID (assuming it's returned in response)
# Then use it to create items
```

## 11. Next Steps

1. **Set up Frontend**: Configure React app to connect to this API
2. **Add Authentication**: Implement JWT with Spring Security
3. **Add More Modules**: Create Job Card, Staff modules
4. **Deploy**: Package and deploy to production server
5. **Monitor**: Set up logging and monitoring

## 12. Get Help

- Check logs: Look for error messages in console
- Read README.md: Full API documentation
- Check Spring Boot docs: https://spring.io/projects/spring-boot
- MySQL documentation: https://dev.mysql.com/doc/

---

**Happy Coding!** 🚀

For detailed API documentation, see README.md
