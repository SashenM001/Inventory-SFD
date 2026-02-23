# Inventory Management Service - Spring Boot Backend

## Overview

This is the REST API backend for the Supreme Funeral Directors Inventory Management System. It handles all inventory operations including items, suppliers, stock adjustments, and transaction tracking.

## Architecture

### Tech Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: MySQL
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven
- **Security**: Spring Security (JWT Ready)

### Database Design

All tables use `inv_` prefix for organization in shared `dbfms` database:

```
inv_supplier - Supplier information
inv_item - Inventory items with categories
inv_item_issue - Track item outflows
inv_item_addition - Track item inflows
```

## Project Structure

```
src/main/java/com/supremefuneralx/
├── InventoryServiceApplication.java      # Main Spring Boot entry point
├── controller/                           # REST API Controllers
│   ├── SupplierController.java
│   ├── InventoryItemController.java
│   ├── ItemIssueController.java
│   └── ItemAdditionController.java
├── service/                              # Business logic services
│   ├── SupplierService.java
│   ├── InventoryItemService.java
│   ├── ItemIssueService.java
│   └── ItemAdditionService.java
├── repository/                           # Data access layer (JPA)
│   ├── SupplierRepository.java
│   ├── InventoryItemRepository.java
│   ├── ItemIssueRepository.java
│   └── ItemAdditionRepository.java
├── entity/                               # JPA Entities
│   ├── Supplier.java
│   ├── InventoryItem.java
│   ├── ItemIssue.java
│   └── ItemAddition.java
├── dto/                                  # Data Transfer Objects
│   ├── SupplierDTO.java
│   ├── InventoryItemDTO.java
│   ├── ItemIssueDTO.java
│   └── ItemAdditionDTO.java
└── exception/                            # Exception handlers
    └── GlobalExceptionHandler.java

resources/
└── application.properties                # Configuration file
```

## Setup Instructions

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Database Setup

1. Create the database:

```sql
CREATE DATABASE IF NOT EXISTS dbfms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dbfms;
```

2. The application will automatically create tables on startup (ddl-auto=update)

### Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dbfms
spring.datasource.username=root
spring.datasource.password=your_password
app.jwt.secret=your-secure-secret-key
```

### Running the Application

1. Navigate to project directory:

```bash
cd inventory-backend
```

2. Build and run:

```bash
mvn clean install
mvn spring-boot:run
```

Or directly with Java:

```bash
mvn clean package
java -jar target/inventory-service-1.0.0.jar
```

The API will run on: `http://localhost:8082`

## API Endpoints

### Base URL

```
http://localhost:8082/api/v1
```

### Suppliers

| Method | Endpoint                           | Description        |
| ------ | ---------------------------------- | ------------------ |
| POST   | `/suppliers`                       | Create supplier    |
| GET    | `/suppliers/{id}`                  | Get supplier by ID |
| GET    | `/suppliers`                       | Get all suppliers  |
| GET    | `/suppliers/search?searchTerm=...` | Search suppliers   |
| PUT    | `/suppliers/{id}`                  | Update supplier    |
| DELETE | `/suppliers/{id}`                  | Delete supplier    |

### Inventory Items

| Method | Endpoint                                             | Description              |
| ------ | ---------------------------------------------------- | ------------------------ |
| POST   | `/items`                                             | Create item              |
| GET    | `/items/{id}`                                        | Get item by ID           |
| GET    | `/items`                                             | Get all items            |
| GET    | `/items/category/{category}`                         | Get items by category    |
| GET    | `/items/search?searchTerm=...`                       | Search items             |
| GET    | `/items/search/category?category=...&searchTerm=...` | Search items by category |
| GET    | `/items/low-stock`                                   | Get low stock items      |
| PUT    | `/items/{id}`                                        | Update item              |
| PATCH  | `/items/{id}/quantity?adjustment=...`                | Adjust quantity          |
| DELETE | `/items/{id}`                                        | Delete item              |

### Item Issues (Outflows)

| Method | Endpoint                                  | Description              |
| ------ | ----------------------------------------- | ------------------------ |
| POST   | `/issues`                                 | Issue item               |
| GET    | `/issues/{id}`                            | Get issue by ID          |
| GET    | `/issues`                                 | Get all issues           |
| GET    | `/issues/item/{itemId}`                   | Get issues for item      |
| GET    | `/issues/recent`                          | Get recent issues        |
| GET    | `/issues/range?startDate=...&endDate=...` | Get issues by date range |

### Item Additions (Inflows)

| Method | Endpoint                                     | Description                 |
| ------ | -------------------------------------------- | --------------------------- |
| POST   | `/additions`                                 | Add stock                   |
| GET    | `/additions/{id}`                            | Get addition by ID          |
| GET    | `/additions`                                 | Get all additions           |
| GET    | `/additions/item/{itemId}`                   | Get additions for item      |
| GET    | `/additions/supplier/{supplierId}`           | Get additions from supplier |
| GET    | `/additions/recent`                          | Get recent additions        |
| GET    | `/additions/range?startDate=...&endDate=...` | Get additions by date range |

## API Request/Response Examples

### Create Supplier

```bash
POST /api/v1/suppliers
Content-Type: application/json

{
  "name": "Premium Casket Supplies",
  "contactPerson": "John Smith",
  "phoneNumber": "+94-11-2345678",
  "email": "john@premiumcaskets.com",
  "address": "123 Business Street",
  "city": "Colombo",
  "zipCode": "00700",
  "notes": "Preferred supplier for premium items"
}

Response: 201 Created
{
  "id": 1,
  "name": "Premium Casket Supplies",
  "contactPerson": "John Smith",
  "phoneNumber": "+94-11-2345678",
  "email": "john@premiumcaskets.com",
  "address": "123 Business Street",
  "city": "Colombo",
  "zipCode": "00700",
  "notes": "Preferred supplier for premium items",
  "createdAt": "2026-01-24T10:30:00",
  "updatedAt": "2026-01-24T10:30:00"
}
```

### Create Inventory Item

```bash
POST /api/v1/items
Content-Type: application/json

{
  "name": "Premium Casket - Mahogany",
  "sku": "CASKET-001",
  "category": "Casket Items",
  "quantity": 10,
  "minQuantity": 3,
  "price": 150000.00,
  "supplierId": 1,
  "unit": "pieces",
  "description": "High-quality mahogany casket"
}

Response: 201 Created
{
  "id": 1,
  "name": "Premium Casket - Mahogany",
  "sku": "CASKET-001",
  "category": "Casket Items",
  "quantity": 10,
  "minQuantity": 3,
  "price": 150000.00,
  "supplierId": 1,
  "supplierName": "Premium Casket Supplies",
  "unit": "pieces",
  "description": "High-quality mahogany casket",
  "createdAt": "2026-01-24T10:35:00",
  "updatedAt": "2026-01-24T10:35:00"
}
```

### Issue Item

```bash
POST /api/v1/issues
Content-Type: application/json

{
  "itemId": 1,
  "quantity": 2,
  "issuedTo": "Service Team A",
  "reason": "Funeral service - Engagement",
  "notes": "Premium casket for VIP client"
}

Response: 201 Created
{
  "id": 1,
  "itemId": 1,
  "itemName": "Premium Casket - Mahogany",
  "quantity": 2,
  "issuedTo": "Service Team A",
  "reason": "Funeral service - Engagement",
  "notes": "Premium casket for VIP client",
  "createdAt": "2026-01-24T11:00:00"
}
```

### Add Stock

```bash
POST /api/v1/additions
Content-Type: application/json

{
  "itemId": 1,
  "quantity": 5,
  "supplierId": 1,
  "notes": "Restocking after client delivery"
}

Response: 201 Created
{
  "id": 1,
  "itemId": 1,
  "itemName": "Premium Casket - Mahogany",
  "quantity": 5,
  "supplierId": 1,
  "supplierName": "Premium Casket Supplies",
  "notes": "Restocking after client delivery",
  "createdAt": "2026-01-24T11:05:00"
}
```

## Item Categories

Default categories for funeral management:

- **Wear Items**: Full Kit, Saree, Pant Shirt, Osari, Sil Dress, Pants, National Kit
- **Casket Items**: Various casket styles, satin linings, hardware
- **Embalming Items**: Cotton rolls, gloves, socks, razor
- **Other Items**: Taffeta cloth, glue, twine, cologne, varnish, etc.

## Key Features

### 1. Inventory Management

- Full CRUD operations for items
- Category-based organization
- Search functionality
- Low stock alerts

### 2. Supplier Management

- Maintain supplier information
- Track supplier contacts
- Search suppliers

### 3. Stock Tracking

- **Item Addition**: Track stock inflows with supplier info
- **Item Issue**: Track stock outflows with recipient and reason
- Automatic quantity updates

### 4. Reporting

- Date-range queries for transaction history
- Recent transactions view
- Low stock items

### 5. Future-Ready

- JWT authentication ready (add Spring Security)
- CORS configured for frontend integration
- RESTful API design
- Exception handling

## Integration with Frontend

The frontend (React app running on `http://localhost:8081`) can integrate with this API:

```javascript
// Example fetch call
const response = await fetch("http://localhost:8082/api/v1/items", {
  method: "GET",
  headers: {
    "Content-Type": "application/json",
  },
});
const items = await response.json();
```

CORS is enabled for `localhost:8081` and `localhost:3000`.

## Database Schema

### inv_supplier

```sql
- id (PK, AUTO_INCREMENT)
- name (VARCHAR 100)
- contact_person (VARCHAR 100)
- phone_number (VARCHAR 20)
- email (VARCHAR 100)
- address (TEXT)
- city (VARCHAR 20)
- zip_code (VARCHAR 20)
- notes (TEXT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### inv_item

```sql
- id (PK, AUTO_INCREMENT)
- name (VARCHAR 100)
- sku (VARCHAR 50, UNIQUE)
- category (VARCHAR 50)
- quantity (INT)
- min_quantity (INT)
- price (DECIMAL 10,2)
- supplier_id (FK)
- description (TEXT)
- unit (VARCHAR 50)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### inv_item_issue

```sql
- id (PK, AUTO_INCREMENT)
- item_id (FK)
- quantity (INT)
- issued_to (VARCHAR 100)
- reason (TEXT)
- notes (TEXT)
- created_at (TIMESTAMP)
```

### inv_item_addition

```sql
- id (PK, AUTO_INCREMENT)
- item_id (FK)
- quantity (INT)
- supplier_id (FK, nullable)
- notes (TEXT)
- created_at (TIMESTAMP)
```

## Security Considerations

1. **CORS**: Configured for development. Update for production.
2. **JWT**: Ready to implement with Spring Security
3. **Validation**: Add @Valid annotations on request bodies
4. **SQL Injection**: Protected via parameterized queries (JPA)
5. **Rate Limiting**: Can be added with Spring Cloud

## Next Steps

1. **Implement JWT Authentication**
   - Add Spring Security configuration
   - Create AuthController and JwtTokenProvider
   - Protect endpoints with @PreAuthorize

2. **Add Audit Logging**
   - Track who made changes
   - Add user information to entities

3. **Implement Pagination**
   - Add Spring Data Pagination to list endpoints
   - Return page objects with metadata

4. **Add Input Validation**
   - Use @Valid and custom validators
   - Better error messages

5. **Create Additional Modules**
   - Staff Management (stf\_ prefix)
   - Job Card Management (jc\_ prefix)
   - Financial Management (fin\_ prefix)

## Troubleshooting

### Database Connection Error

```
Check MySQL is running
Verify credentials in application.properties
Ensure dbfms database exists
```

### Port Already in Use

```
Change server.port in application.properties
Or kill process on port 8082: lsof -ti:8082 | xargs kill -9
```

### JPA Mapping Errors

```
Check entity annotations
Verify table names match @Table definition
Check column names in @Column annotations
```

## Support

For issues or questions, refer to:

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- MySQL Documentation: https://dev.mysql.com/doc/

---

**Project**: Supreme Funeral Directors - Inventory Management System
**Version**: 1.0.0
**Last Updated**: January 24, 2026
