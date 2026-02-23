-- Funeral Management System - Inventory Database Setup Script
-- Database: dbfms
-- Prefix: inv_
-- Created: January 24, 2026

-- Create Database (if not exists)
CREATE DATABASE IF NOT EXISTS dbfms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dbfms;

-- Note: With Spring Boot JPA set to ddl-auto=update,
-- the tables will be auto-created. This script is for manual setup if needed.

-- ============================================
-- 1. SUPPLIER TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS inv_supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(20),
    zip_code VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. INVENTORY ITEM TABLE
-- ============================================
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
    INDEX idx_quantity (quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. ITEM ISSUE TABLE (Outflows)
-- ============================================
CREATE TABLE IF NOT EXISTS inv_item_issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    issued_to VARCHAR(100),
    reason TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE CASCADE,
    INDEX idx_item (item_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 4. ITEM ADDITION TABLE (Inflows)
-- ============================================
CREATE TABLE IF NOT EXISTS inv_item_addition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    supplier_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES inv_supplier(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- SAMPLE DATA INSERTION (Optional)
-- ============================================

-- Insert Sample Supplier
INSERT INTO inv_supplier (name, contact_person, phone_number, email, address, city, zip_code, notes)
VALUES (
    'Premium Casket Supplies',
    'John Smith',
    '+94-11-2345678',
    'john@premiumcaskets.lk',
    '123 Business Street, Colombo',
    'Colombo',
    '00700',
    'Primary supplier for premium caskets and accessories'
);

INSERT INTO inv_supplier (name, contact_person, phone_number, email, address, city, zip_code, notes)
VALUES (
    'Garment & Fabric Imports',
    'Maria Fernando',
    '+94-11-7654321',
    'maria@garmentfabric.lk',
    '456 Fashion Lane, Colombo',
    'Colombo',
    '00700',
    'Supplier for sarees, shirts, and funeral wear'
);

INSERT INTO inv_supplier (name, contact_person, phone_number, email, address, city, zip_code, notes)
VALUES (
    'Medical & Embalming Supplies',
    'Dr. Ahmed Hassan',
    '+94-11-5555555',
    'dr.ahmed@medicalembalming.lk',
    '789 Medical Plaza, Colombo',
    'Colombo',
    '00700',
    'Supplier for embalming materials and medical supplies'
);

-- Insert Sample Items
INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES ('Full Kit Large', 'WEAR-FULL-L-001', 'Wear Items', 15, 5, 5000.00, 1, 'pieces', 'Complete funeral wear set in Large size');

INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES ('Saree XL', 'WEAR-SAREE-XL-001', 'Wear Items', 20, 5, 3500.00, 2, 'pieces', 'Traditional saree for funeral services');

INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES ('Premium Casket - Mahogany', 'CASKET-MAH-001', 'Casket Items', 8, 2, 150000.00, 1, 'pieces', 'High-quality mahogany casket');

INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES ('Cotton Rolls (Pack)', 'EMBALM-COTTON-001', 'Embalming Items', 50, 10, 1200.00, 3, 'packs', 'Medical-grade cotton rolls for embalming');

INSERT INTO inv_item (name, sku, category, quantity, min_quantity, price, supplier_id, unit, description)
VALUES ('Latex Gloves Box', 'EMBALM-GLOVES-001', 'Embalming Items', 30, 5, 800.00, 3, 'boxes', 'Disposable latex gloves - Box of 100');

-- Insert Sample Transactions
INSERT INTO inv_item_addition (item_id, quantity, supplier_id, notes, created_at)
VALUES (1, 10, 1, 'Initial stock received', NOW());

INSERT INTO inv_item_addition (item_id, quantity, supplier_id, notes, created_at)
VALUES (2, 20, 2, 'Bulk order for upcoming season', DATE_SUB(NOW(), INTERVAL 7 DAY));

INSERT INTO inv_item_issue (item_id, quantity, issued_to, reason, notes, created_at)
VALUES (1, 2, 'Service Team A', 'Funeral Service - Wedding Engagement', 'Premium client - Special request', DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO inv_item_issue (item_id, quantity, issued_to, reason, notes, created_at)
VALUES (4, 5, 'Embalming Lab', 'Regular embalming operations', 'Daily operations', DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Run these to verify setup:

-- Count of suppliers
SELECT COUNT(*) as supplier_count FROM inv_supplier;

-- Count of items
SELECT COUNT(*) as item_count FROM inv_item;

-- All items with suppliers
SELECT 
    i.name, 
    i.sku, 
    i.category, 
    i.quantity, 
    i.min_quantity, 
    i.price,
    s.name as supplier_name
FROM inv_item i
LEFT JOIN inv_supplier s ON i.supplier_id = s.id;

-- Recent additions
SELECT 
    a.id,
    i.name as item_name,
    a.quantity,
    s.name as supplier_name,
    a.created_at
FROM inv_item_addition a
LEFT JOIN inv_item i ON a.item_id = i.id
LEFT JOIN inv_supplier s ON a.supplier_id = s.id
ORDER BY a.created_at DESC;

-- Recent issues
SELECT 
    iss.id,
    i.name as item_name,
    iss.quantity,
    iss.issued_to,
    iss.reason,
    iss.created_at
FROM inv_item_issue iss
LEFT JOIN inv_item i ON iss.item_id = i.id
ORDER BY iss.created_at DESC;

-- Low stock items
SELECT 
    name, 
    sku, 
    quantity, 
    min_quantity,
    (min_quantity - quantity) as shortage
FROM inv_item
WHERE quantity <= min_quantity
ORDER BY shortage DESC;

-- Total inventory value
SELECT 
    SUM(quantity * price) as total_value,
    COUNT(*) as item_count,
    ROUND(AVG(price), 2) as avg_price
FROM inv_item;

-- ============================================
-- CLEANUP (If needed to reset)
-- ============================================
-- UNCOMMENT TO DELETE ALL DATA (USE WITH CAUTION)
-- DELETE FROM inv_item_issue;
-- DELETE FROM inv_item_addition;
-- DELETE FROM inv_item;
-- DELETE FROM inv_supplier;
-- ALTER TABLE inv_item_issue AUTO_INCREMENT = 1;
-- ALTER TABLE inv_item_addition AUTO_INCREMENT = 1;
-- ALTER TABLE inv_item AUTO_INCREMENT = 1;
-- ALTER TABLE inv_supplier AUTO_INCREMENT = 1;
