-- ============================================================================
-- FUNERAL MANAGEMENT SYSTEM - COMPREHENSIVE DATABASE SETUP
-- ============================================================================
-- Database: dbfms
-- System: Inventory Management Hub
-- Created: February 15, 2026
-- Version: 2.0 - Enhanced with full sample data and user management
-- ============================================================================

-- Create Database
CREATE DATABASE IF NOT EXISTS dbfms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dbfms;

-- ============================================================================
-- 1. USER MANAGEMENT TABLE (For Authentication System)
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'storekeeper') NOT NULL DEFAULT 'storekeeper',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='User accounts for system authentication';

-- ============================================================================
-- 2. SUPPLIER TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    zip_code VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_email (email),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Supplier information and contact details';

-- ============================================================================
-- 3. INVENTORY ITEM TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    min_quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL,
    supplier_id BIGINT NOT NULL,
    description TEXT,
    unit VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES inv_supplier(id) ON DELETE RESTRICT,
    INDEX idx_sku (sku),
    INDEX idx_category (category),
    INDEX idx_supplier (supplier_id),
    INDEX idx_quantity (quantity),
    INDEX idx_min_quantity (min_quantity),
    FULLTEXT INDEX ft_name_description (name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Core inventory items with stock levels';

-- ============================================================================
-- 4. ITEM ADDITION TABLE (Stock Inflows/Receipts)
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_item_addition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    supplier_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id BIGINT,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES inv_supplier(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by_user_id) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_created (created_at),
    INDEX idx_created_by (created_by_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stock additions - incoming inventory transactions';

-- ============================================================================
-- 5. ITEM ISSUE TABLE (Stock Outflows/Consumptions)
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_item_issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    issued_to VARCHAR(100),
    reason TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by_user_id BIGINT,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_created (created_at),
    INDEX idx_issued_to (issued_to),
    INDEX idx_created_by (created_by_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Stock issues - outgoing inventory transactions';

-- ============================================================================
-- 6. STOCK AUDIT LOG TABLE (For compliance and tracking)
-- ============================================================================
CREATE TABLE IF NOT EXISTS inv_stock_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    old_quantity INT,
    new_quantity INT,
    transaction_type ENUM('addition', 'issue', 'adjustment', 'cycle_count') NOT NULL,
    transaction_id BIGINT,
    notes TEXT,
    created_by_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_type (transaction_type),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Audit trail for all inventory stock changes';

-- ============================================================================
-- DATA INSERTION SECTION
-- ============================================================================

-- ============================================================================
-- STEP 1: USER DATA (Authentication System)
-- ============================================================================
INSERT INTO inv_user (full_name, email, password, role, is_active)
VALUES 
    ('Admin User', 'admin1@sfd.com', 'Admin111_', 'admin', TRUE),
    ('Store Keeper', 'storekeeper1@sfd.com', 'StoreKeeper111', 'storekeeper', TRUE);

-- ============================================================================
-- STEP 2: SUPPLIER DATA (from frontend)
-- ============================================================================
INSERT INTO inv_supplier (name, contact_person, phone_number, email, address, city, zip_code, notes)
VALUES
    ('TechPro Supplies', 
     'James Wilson', 
     '+1 (555) 123-4567', 
     'orders@techpro.com', 
     '123 Tech Boulevard, Silicon Valley, CA 94025', 
     'Silicon Valley', 
     '94025', 
     'Reliable supplier for technical and general inventory items'),
    ('Global Hardware Inc.', 
     'Robert Chen', 
     '+1 (555) 987-6543', 
     'sales@globalhardware.com', 
     '456 Industrial Park, Detroit, MI 48201', 
     'Detroit', 
     '48201', 
     'Industrial supplier with competitive pricing'),
    ('Office Essentials Co.', 
     'Sarah Martinez', 
     '+1 (555) 456-7890', 
     'supply@officeessentials.com', 
     '789 Commerce Street, New York, NY 10001', 
     'New York', 
     '10001', 
     'Comprehensive office and specialty supplier');

-- ============================================================================
-- STEP 3: INVENTORY ITEMS DATA (from frontend - funeral items)
-- ============================================================================
INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES
    -- Wear Items
    ('Full Kit – L', 'FK-L-001', 'Wear Items', 25, 5, 2500.00, 1, 'pieces', 'Complete funeral wear set size Large'),
    ('Full Kit – M', 'FK-M-002', 'Wear Items', 30, 5, 2500.00, 1, 'pieces', 'Complete funeral wear set size Medium'),
    ('Full Kit – S', 'FK-S-003', 'Wear Items', 20, 5, 2500.00, 1, 'pieces', 'Complete funeral wear set size Small'),
    ('Pant and Shirt Combo – L', 'PS-L-004', 'Wear Items', 15, 5, 1500.00, 2, 'pieces', 'Pant and shirt combo size Large'),
    ('Pant and Shirt Combo – M', 'PS-M-005', 'Wear Items', 12, 5, 1500.00, 2, 'pieces', 'Pant and shirt combo size Medium'),
    ('Sil Dress – S', 'SD-S-006', 'Wear Items', 20, 5, 1200.00, 2, 'pieces', 'Sil dress size Small'),
    ('Osari – XXL', 'OS-XXL-007', 'Wear Items', 8, 3, 1600.00, 1, 'pieces', 'Osari size XXL'),
    
    -- Casket Items
    ('Satin – Silver', 'ST-SV-008', 'Casket Items', 35, 10, 3500.00, 3, 'pieces', 'Silver satin casket lining'),
    ('Bar Set – Design', 'BS-DG-009', 'Casket Items', 15, 5, 2200.00, 3, 'pieces', 'Designer casket bar set'),
    ('Casket Hooks', 'CH-001-010', 'Casket Items', 60, 20, 800.00, 3, 'sets', 'Premium casket hooks set'),
    
    -- Embalming Items
    ('Cotton Rolls', 'CR-001-011', 'Embalming Items', 50, 20, 450.00, 1, 'packs', 'High quality cotton rolls for embalming'),
    ('Cotton Gloves', 'CG-001-012', 'Embalming Items', 40, 15, 250.00, 2, 'boxes', 'Sterile cotton gloves per pack');

-- ============================================================================
-- STEP 4: SAMPLE TRANSACTIONS DATA
-- ============================================================================

-- Stock Additions (Inflows)
INSERT INTO inv_item_addition (item_id, quantity, supplier_id, notes, created_at, created_by_user_id)
VALUES
    (1, 20, 1, 'Initial stock received', DATE_SUB(NOW(), INTERVAL 30 DAY), 1),
    (2, 25, 1, 'Restock order', DATE_SUB(NOW(), INTERVAL 25 DAY), 1),
    (3, 15, 1, 'Bulk purchase', DATE_SUB(NOW(), INTERVAL 20 DAY), 2),
    (4, 10, 2, 'Monthly replenishment', DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
    (5, 8, 2, 'Stock adjustment', DATE_SUB(NOW(), INTERVAL 10 DAY), 2),
    (6, 18, 2, 'Seasonal stock', DATE_SUB(NOW(), INTERVAL 7 DAY), 1),
    (7, 5, 1, 'Special order fulfillment', DATE_SUB(NOW(), INTERVAL 5 DAY), 2),
    (8, 30, 3, 'Casket lining stock', DATE_SUB(NOW(), INTERVAL 4 DAY), 1),
    (9, 12, 3, 'Hardware received', DATE_SUB(NOW(), INTERVAL 3 DAY), 2),
    (10, 50, 3, 'Bulk hardware order', DATE_SUB(NOW(), INTERVAL 2 DAY), 1),
    (11, 40, 1, 'Embalming supplies', DATE_SUB(NOW(), INTERVAL 1 DAY), 1),
    (12, 35, 2, 'PPE supplies', NOW(), 2);

-- Stock Issues (Outflows)
INSERT INTO inv_item_issue (item_id, quantity, issued_to, reason, notes, created_at, created_by_user_id)
VALUES
    (1, 3, 'Service Team A', 'Funeral Service #001', 'Standard service', DATE_SUB(NOW(), INTERVAL 28 DAY), 1),
    (2, 2, 'Service Team B', 'Funeral Service #002', 'Standard service', DATE_SUB(NOW(), INTERVAL 24 DAY), 2),
    (4, 1, 'Service Team A', 'Funeral Service #003', 'VIP client service', DATE_SUB(NOW(), INTERVAL 22 DAY), 1),
    (8, 2, 'Decorations Team', 'Casket decoration', 'Premium finish', DATE_SUB(NOW(), INTERVAL 18 DAY), 2),
    (11, 5, 'Embalming Lab', 'Regular operations', 'Daily operations', DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
    (12, 3, 'Embalming Lab', 'Regular operations', 'Daily operations', DATE_SUB(NOW(), INTERVAL 12 DAY), 2),
    (1, 4, 'Service Team C', 'Funeral Service #004', 'Large group service', DATE_SUB(NOW(), INTERVAL 8 DAY), 1),
    (3, 2, 'Service Team B', 'Funeral Service #005', 'Standard service', DATE_SUB(NOW(), INTERVAL 5 DAY), 2),
    (9, 1, 'Decorations Team', 'Casket hardware', 'Installation', DATE_SUB(NOW(), INTERVAL 3 DAY), 1),
    (10, 4, 'Maintenance', 'Stock check and repair', 'Quarterly maintenance', DATE_SUB(NOW(), INTERVAL 1 DAY), 2);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Run these to verify the setup was successful:

-- Total Records Count
SELECT 
    'Users' as table_name, COUNT(*) as record_count FROM inv_user
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM inv_supplier
UNION ALL
SELECT 'Inventory Items', COUNT(*) FROM inv_item
UNION ALL
SELECT 'Stock Additions', COUNT(*) FROM inv_item_addition
UNION ALL
SELECT 'Stock Issues', COUNT(*) FROM inv_item_issue
ORDER BY table_name;

-- Inventory Status Summary
SELECT 
    i.name,
    i.sku,
    i.category,
    i.quantity,
    i.min_quantity,
    CASE 
        WHEN i.quantity <= i.min_quantity THEN 'LOW STOCK'
        WHEN i.quantity = 0 THEN 'OUT OF STOCK'
        ELSE 'IN STOCK'
    END as stock_status,
    i.price,
    (i.quantity * i.price) as total_value,
    s.name as supplier_name
FROM inv_item i
LEFT JOIN inv_supplier s ON i.supplier_id = s.id
ORDER BY i.category, i.name;

-- Financial Summary
SELECT 
    SUM(quantity * price) as total_inventory_value,
    COUNT(*) as total_items,
    ROUND(AVG(price), 2) as average_item_price,
    MAX(price) as highest_price,
    MIN(price) as lowest_price
FROM inv_item;

-- Recent Transactions (Last 30 Days)
SELECT 
    'Addition' as transaction_type,
    ia.created_at,
    i.name as item_name,
    ia.quantity,
    s.name as supplier_name,
    u.full_name as created_by
FROM inv_item_addition ia
LEFT JOIN inv_item i ON ia.item_id = i.id
LEFT JOIN inv_supplier s ON ia.supplier_id = s.id
LEFT JOIN inv_user u ON ia.created_by_user_id = u.id
WHERE ia.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
UNION ALL
SELECT 
    'Issue',
    ii.created_at,
    i.name,
    ii.quantity,
    ii.issued_to,
    u.full_name
FROM inv_item_issue ii
LEFT JOIN inv_item i ON ii.item_id = i.id
LEFT JOIN inv_user u ON ii.created_by_user_id = u.id
WHERE ii.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
ORDER BY created_at DESC;

-- Low Stock Alert Items
SELECT 
    i.name,
    i.sku,
    i.quantity,
    i.min_quantity,
    (i.min_quantity - i.quantity) as shortage_amount,
    s.name as supplier_name,
    s.contact_person,
    s.phone_number
FROM inv_item i
LEFT JOIN inv_supplier s ON i.supplier_id = s.id
WHERE i.quantity <= i.min_quantity
ORDER BY shortage_amount DESC;

-- Items by Category
SELECT 
    category,
    COUNT(*) as item_count,
    SUM(quantity) as total_quantity,
    SUM(quantity * price) as category_value,
    ROUND(AVG(price), 2) as avg_price
FROM inv_item
GROUP BY category
ORDER BY category_value DESC;

-- Supplier Performance (by items supplied)
SELECT 
    s.name,
    COUNT(DISTINCT i.id) as items_supplied,
    SUM(i.quantity) as total_stock,
    SUM(i.quantity * i.price) as supplier_value,
    SUM(ia.quantity) as total_received,
    COUNT(DISTINCT ia.id) as delivery_count
FROM inv_supplier s
LEFT JOIN inv_item i ON s.id = i.supplier_id
LEFT JOIN inv_item_addition ia ON s.id = ia.supplier_id
GROUP BY s.id, s.name
ORDER BY supplier_value DESC;

-- User Activity Log
SELECT 
    u.full_name,
    u.email,
    u.role,
    COUNT(DISTINCT ia.id) as additions_created,
    COUNT(DISTINCT ii.id) as issues_created,
    MAX(GREATEST(MAX(ia.created_at), MAX(ii.created_at))) as last_activity
FROM inv_user u
LEFT JOIN inv_item_addition ia ON u.id = ia.created_by_user_id
LEFT JOIN inv_item_issue ii ON u.id = ii.created_by_user_id
GROUP BY u.id, u.full_name, u.email, u.role;

-- ============================================================================
-- INDEXES AND OPTIMIZATION
-- ============================================================================
-- Additional composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_item_category_quantity ON inv_item(category, quantity);
CREATE INDEX IF NOT EXISTS idx_item_addition_item_date ON inv_item_addition(item_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_item_issue_item_date ON inv_item_issue(item_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_supplier_item ON inv_supplier(id) 
    COMMENT 'Support for supplier lookups';

-- ============================================================================
-- TABLE STATISTICS (For query optimizer)
-- ============================================================================
ANALYZE TABLE inv_user;
ANALYZE TABLE inv_supplier;
ANALYZE TABLE inv_item;
ANALYZE TABLE inv_item_addition;
ANALYZE TABLE inv_item_issue;
ANALYZE TABLE inv_stock_audit;

-- ============================================================================
-- DATABASE INFORMATION QUERIES
-- ============================================================================
-- Check table sizes
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as size_mb
FROM information_schema.tables
WHERE table_schema = 'dbfms'
ORDER BY size_mb DESC;

-- Check foreign key constraints
SELECT 
    constraint_name,
    table_name,
    column_name,
    referenced_table_name,
    referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'dbfms' AND referenced_table_schema IS NOT NULL;

-- ============================================================================
-- END OF SETUP SCRIPT
-- ============================================================================
