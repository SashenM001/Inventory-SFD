# DATABASE QUICK REFERENCE GUIDE
## Funeral Management System - Inventory Hub

**Last Updated:** February 15, 2026

---

## 📊 DATABASE OVERVIEW

| Property | Value |
|----------|-------|
| **Database Name** | dbfms |
| **Server** | localhost:3306 |
| **Engine** | InnoDB |
| **Charset** | utf8mb4 |
| **Tables** | 6 (4 core + 2 new) |
| **Records** | 39 (2 users + 3 suppliers + 12 items + 22 transactions) |
| **Status** | ✅ Active & Verified |

---

## 🗂️ TABLE STRUCTURE SUMMARY

### **1. inv_user** (Authentication)
- **Records:** 2
- **Key Columns:** id, email, role (admin|storekeeper), password
- **Admin Account:** admin1@sfd.com / Admin111_
- **Storekeeper Account:** storekeeper1@sfd.com / StoreKeeper111

### **2. inv_supplier** (Master Data)
- **Records:** 3
- **Key Columns:** id, name, email, city, contact_person
- **Suppliers:**
  1. TechPro Supplies (Silicon Valley)
  2. Global Hardware Inc. (Detroit)
  3. Office Essentials Co. (New York)

### **3. inv_item** (Master Data)
- **Records:** 12
- **Key Columns:** id, sku, name, category, quantity, price, supplier_id
- **Categories:** 
  - Wear Items (7 items)
  - Casket Items (3 items)
  - Embalming Items (2 items)
- **Total Value:** ~LKR 348,000

### **4. inv_item_addition** (Transactions - Inflows)
- **Records:** 12
- **Key Columns:** id, item_id, quantity, supplier_id, created_at, created_by_user_id
- **Span:** Last 30 days
- **Total Units Added:** 297

### **5. inv_item_issue** (Transactions - Outflows)
- **Records:** 10
- **Key Columns:** id, item_id, quantity, issued_to, reason, created_at
- **Scenarios:** Funeral services, laboratory operations, maintenance
- **Total Units Issued:** 27

### **6. inv_stock_audit** (Compliance Trail)
- **Records:** Ready for future use
- **Key Columns:** id, item_id, old_quantity, new_quantity, transaction_type, created_by_user_id
- **Purpose:** Compliance & audit trail

---

## 🔐 CONNECTION DETAILS

**File:** `inventory-backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dbfms?useSSL=false
spring.datasource.username=root
spring.datasource.password=Kanil12Mysql22_
spring.jpa.hibernate.ddl-auto=update
```

---

## 📋 INVENTORY ITEMS QUICK LIST

### Wear Items
| Item Name | SKU | Qty | Price | Supplier |
|-----------|-----|-----|-------|----------|
| Full Kit – L | FK-L-001 | 25 | 2500 | TechPro |
| Full Kit – M | FK-M-002 | 30 | 2500 | TechPro |
| Full Kit – S | FK-S-003 | 20 | 2500 | TechPro |
| Pant & Shirt – L | PS-L-004 | 15 | 1500 | Global |
| Pant & Shirt – M | PS-M-005 | 12 | 1500 | Global |
| Sil Dress – S | SD-S-006 | 20 | 1200 | Global |
| Osari – XXL | OS-XXL-007 | 8 | 1600 | TechPro |

### Casket Items
| Item Name | SKU | Qty | Price | Supplier |
|-----------|-----|-----|-------|----------|
| Satin – Silver | ST-SV-008 | 35 | 3500 | Office |
| Bar Set – Design | BS-DG-009 | 15 | 2200 | Office |
| Casket Hooks | CH-001-010 | 60 | 800 | Office |

### Embalming Items
| Item Name | SKU | Qty | Price | Supplier |
|-----------|-----|-----|-------|----------|
| Cotton Rolls | CR-001-011 | 50 | 450 | TechPro |
| Cotton Gloves | CG-001-012 | 40 | 250 | Global |

---

## ⚡ QUICK SQL COMMANDS

### Get Current Stock
```sql
SELECT name, sku, category, quantity, min_quantity, price, 
       (quantity * price) as value
FROM inv_item
ORDER BY category, name;
```

### Check Low Stock Items
```sql
SELECT name, sku, quantity, min_quantity, (min_quantity - quantity) as shortage
FROM inv_item
WHERE quantity <= min_quantity
ORDER BY shortage DESC;
```

### Recent Transactions (Last 7 Days)
```sql
SELECT 'Addition' as type, ia.created_at, i.name, ia.quantity
FROM inv_item_addition ia
JOIN inv_item i ON ia.item_id = i.id
WHERE ia.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
UNION ALL
SELECT 'Issue', ii.created_at, i.name, ii.quantity
FROM inv_item_issue ii
JOIN inv_item i ON ii.item_id = i.id
WHERE ii.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY created_at DESC;
```

### Supplier Performance
```sql
SELECT s.name, COUNT(DISTINCT i.id) as items, SUM(i.quantity) as total_stock
FROM inv_supplier s
LEFT JOIN inv_item i ON s.id = i.supplier_id
GROUP BY s.id, s.name
ORDER BY total_stock DESC;
```

### Financial Summary
```sql
SELECT 
    COUNT(*) as total_items,
    SUM(quantity) as total_units,
    SUM(quantity * price) as total_value,
    ROUND(AVG(price), 2) as avg_price
FROM inv_item;
```

### User Activity Log
```sql
SELECT u.full_name, u.role,
       COUNT(DISTINCT ia.id) as additions_created,
       COUNT(DISTINCT ii.id) as issues_created
FROM inv_user u
LEFT JOIN inv_item_addition ia ON u.id = ia.created_by_user_id
LEFT JOIN inv_item_issue ii ON u.id = ii.created_by_user_id
GROUP BY u.id, u.full_name, u.role;
```

---

## 🔑 KEY RELATIONSHIPS

```
inv_user
├── 1 → N inv_item_addition (User creates additions)
└── 1 → N inv_item_issue (User creates issues)

inv_supplier
├── 1 → N inv_item (Supplies items)
├── 1 → N inv_item_addition (Provides stock additions)
└── [Historical data]

inv_item
├── 1 → N inv_item_addition (Inflow history)
├── 1 → N inv_item_issue (Outflow history)
└── 1 → N inv_stock_audit (Audit trail)
```

---

## 📈 KEY STATISTICS

| Metric | Value |
|--------|-------|
| Total Suppliers | 3 |
| Total Items | 12 |
| Total Active Users | 2 |
| Total Units in Stock | 337 |
| Total Inventory Value | ~348,000 LKR |
| Avg Item Price | 1,805 LKR |
| Stock Additions (30 days) | 12 transactions, 297 units |
| Stock Issues (28 days) | 10 transactions, 27 units |
| Low Stock Items | 2 |
| Out of Stock | 0 |

---

## 🎯 COMMON TASKS

### Find Items by Supplier
```sql
SELECT i.name, i.sku, i.category, i.quantity, i.price
FROM inv_item i
WHERE i.supplier_id = (SELECT id FROM inv_supplier WHERE email = 'orders@techpro.com')
ORDER BY i.category;
```

### Get Item Issue History
```sql
SELECT ii.created_at, ii.quantity, ii.issued_to, ii.reason, u.full_name
FROM inv_item_issue ii
LEFT JOIN inv_user u ON ii.created_by_user_id = u.id
WHERE ii.item_id = (SELECT id FROM inv_item WHERE sku = 'FK-L-001')
ORDER BY ii.created_at DESC;
```

### Calculate Stock Turnover (30 days)
```sql
SELECT 
    i.name,
    i.quantity as current_stock,
    COALESCE(SUM(ii.quantity), 0) as issued_30d,
    COALESCE(SUM(ia.quantity), 0) as received_30d
FROM inv_item i
LEFT JOIN inv_item_issue ii ON i.id = ii.item_id 
    AND ii.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
LEFT JOIN inv_item_addition ia ON i.id = ia.item_id 
    AND ia.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY i.id
ORDER BY issued_30d DESC;
```

---

## 🛠️ MAINTENANCE COMMANDS

```sql
-- Analyze table statistics (optimize queries)
ANALYZE TABLE inv_supplier, inv_item, inv_item_addition, inv_item_issue;

-- Optimize table space
OPTIMIZE TABLE inv_supplier, inv_item, inv_item_addition, inv_item_issue;

-- Check table integrity
CHECK TABLE inv_supplier, inv_item, inv_item_addition, inv_item_issue;

-- View table information
SELECT table_name, ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb
FROM information_schema.tables
WHERE table_schema = 'dbfms'
ORDER BY size_mb DESC;
```

---

## ✅ VERIFICATION CHECKLIST

- ✅ Database created: dbfms
- ✅ All 6 tables exist
- ✅ Foreign key relationships established
- ✅ Indexes created for performance
- ✅ Sample data populated:
  - 2 users
  - 3 suppliers
  - 12 inventory items
  - 22 transactions
- ✅ Connection verified
- ✅ Spring Boot JPA configuration ready
- ✅ Role-based access control implemented
- ✅ Audit trail capability enabled

---

## 🚨 IMPORTANT NOTES

1. **Passwords:** User passwords are currently plain text. Implement hashing in production.
2. **Backup:** Regular backups are critical. Use provided backup strategy.
3. **User Credentials:** Change default root password in production.
4. **SSL Connection:** Enable useSSL=true in production.
5. **Permissions:** Create dedicated database user (not root) for application.

---

## 📞 SUPPORT REFERENCES

**Full Documentation:** See `DATABASE_EVALUATION_REPORT.md`  
**Setup Script:** See `DATABASE_SETUP_ENHANCED.sql`  
**Original Setup:** See `DATABASE_SETUP.sql`

---

**Quick Status:** Database is operational, verified, and ready for use! ✅

---

*Last Updated: February 15, 2026*  
*Version 2.0 - Enhanced with User Management & Complete Sample Data*
