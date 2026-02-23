-- ============================================================================
-- FUNERAL MANAGEMENT SYSTEM - DATABASE SETUP (VERIFIED VERSION)
-- ============================================================================
-- Database: dbfms
-- Version: 2.0 - Simplified & Tested
-- Date: February 15, 2026
-- ============================================================================

-- STEP 1: Drop existing database if needed (OPTIONAL - comment out to keep existing data)
-- DROP DATABASE IF EXISTS dbfms;

-- STEP 2: Create Database
CREATE DATABASE IF NOT EXISTS dbfms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dbfms;

-- STEP 3: Create Users Table
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
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 4: Create Suppliers Table
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
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 5: Create Items Table
CREATE TABLE IF NOT EXISTS inv_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    min_quantity INT DEFAULT 5,
    current_quantity INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_sku (sku),
    INDEX idx_name (item_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 6: Create Stock Addition Table
CREATE TABLE IF NOT EXISTS inv_item_addition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    supplier_id BIGINT NOT NULL,
    cost_per_unit DECIMAL(10, 2),
    created_by_user BIGINT,
    status VARCHAR(50) DEFAULT 'added',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE RESTRICT,
    FOREIGN KEY (supplier_id) REFERENCES inv_supplier(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by_user) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 7: Create Stock Issue Table
CREATE TABLE IF NOT EXISTS inv_item_issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    issued_to VARCHAR(100),
    created_by_user BIGINT,
    status VARCHAR(50) DEFAULT 'issued',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by_user) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 8: Create Stock Audit Table
CREATE TABLE IF NOT EXISTS inv_stock_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    old_quantity INT,
    new_quantity INT,
    transaction_type VARCHAR(50),
    transaction_id BIGINT,
    notes TEXT,
    created_by_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inv_item(id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by_user_id) REFERENCES inv_user(id) ON DELETE SET NULL,
    INDEX idx_item (item_id),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- STEP 9: INSERT SAMPLE DATA - USERS
-- ============================================================================
INSERT IGNORE INTO inv_user (id, full_name, email, password, role, is_active) VALUES
(1, 'Admin User', 'admin1@sfd.com', 'Admin111_', 'admin', TRUE),
(2, 'Store Keeper', 'storekeeper1@sfd.com', 'StoreKeeper111', 'storekeeper', TRUE);

-- ============================================================================
-- STEP 10: INSERT SAMPLE DATA - SUPPLIERS
-- ============================================================================
INSERT IGNORE INTO inv_supplier (id, name, contact_person, phone_number, email, address, city, zip_code) VALUES
(1, 'TechPro Supplies', 'John Smith', '+1-800-TECH-101', 'contact@techpro.com', '123 Tech Drive', 'Silicon Valley', 'CA 94015'),
(2, 'Global Hardware Inc.', 'Mary Johnson', '+1-800-HARD-123', 'sales@globalhw.com', '456 Industrial Ave', 'Detroit', 'MI 48201'),
(3, 'Office Essentials Co.', 'David Brown', '+1-800-OFFICE-1', 'support@officeess.com', '789 Business Blvd', 'New York', 'NY 10001');

-- ============================================================================
-- STEP 11: INSERT SAMPLE DATA - ITEMS
-- ============================================================================
INSERT IGNORE INTO inv_item (id, item_name, category, sku, description, price_per_unit, min_quantity, current_quantity) VALUES
(1, 'Full Kit – L', 'wear', 'SKU-WEAR-001', 'Complete wear set, Large size', 2500.00, 5, 25),
(2, 'Full Kit – M', 'wear', 'SKU-WEAR-002', 'Complete wear set, Medium size', 2500.00, 5, 30),
(3, 'Full Kit – S', 'wear', 'SKU-WEAR-003', 'Complete wear set, Small size', 2500.00, 5, 20),
(4, 'Pant & Shirt Combo – L', 'wear', 'SKU-WEAR-004', 'Pants and shirt combination, Large', 1500.00, 10, 45),
(5, 'Pant & Shirt Combo – M', 'wear', 'SKU-WEAR-005', 'Pants and shirt combination, Medium', 1500.00, 10, 35),
(6, 'Sil Dress – S', 'wear', 'SKU-WEAR-006', 'Silk dress, Small size', 1200.00, 8, 18),
(7, 'Osari – XXL', 'wear', 'SKU-WEAR-007', 'Osari garment, XXL size', 1600.00, 5, 12),
(8, 'Satin – Silver', 'casket', 'SKU-CASKET-001', 'Satin casket lining, Silver color', 3500.00, 3, 8),
(9, 'Bar Set – Design', 'casket', 'SKU-CASKET-002', 'Decorative bar set for casket', 2200.00, 5, 15),
(10, 'Casket Hooks', 'casket', 'SKU-CASKET-003', 'Metal hooks for casket', 800.00, 10, 42),
(11, 'Cotton Rolls', 'embalm', 'SKU-EMBALM-001', 'Cotton rolls for embalming', 450.00, 20, 85),
(12, 'Cotton Gloves', 'embalm', 'SKU-EMBALM-002', 'Medical grade cotton gloves, Pack of 100', 250.00, 10, 62);

-- ============================================================================
-- STEP 12: INSERT SAMPLE DATA - STOCK ADDITIONS
-- ============================================================================
INSERT IGNORE INTO inv_item_addition (id, item_id, quantity, supplier_id, cost_per_unit, created_by_user, status, created_at) VALUES
(1, 1, 25, 1, 2500.00, 1, 'added', '2026-01-16 09:15:00'),
(2, 2, 30, 1, 2500.00, 1, 'added', '2026-01-16 10:30:00'),
(3, 3, 20, 1, 2500.00, 1, 'added', '2026-01-17 11:45:00'),
(4, 4, 45, 2, 1500.00, 2, 'added', '2026-01-18 08:20:00'),
(5, 5, 35, 2, 1500.00, 2, 'added', '2026-01-18 09:00:00'),
(6, 6, 18, 2, 1200.00, 1, 'added', '2026-01-19 14:10:00'),
(7, 7, 12, 3, 1600.00, 2, 'added', '2026-01-20 10:00:00'),
(8, 8, 8, 1, 3500.00, 1, 'added', '2026-01-21 13:30:00'),
(9, 9, 15, 2, 2200.00, 2, 'added', '2026-01-22 11:15:00'),
(10, 10, 42, 3, 800.00, 1, 'added', '2026-01-23 09:45:00'),
(11, 11, 85, 1, 450.00, 2, 'added', '2026-01-24 10:20:00'),
(12, 12, 62, 2, 250.00, 1, 'added', '2026-01-25 15:00:00');

-- ============================================================================
-- STEP 13: INSERT SAMPLE DATA - STOCK ISSUES
-- ============================================================================
INSERT IGNORE INTO inv_item_issue (id, item_id, quantity, issued_to, created_by_user, status, notes, created_at) VALUES
(1, 1, 2, 'Service 001', 1, 'issued', 'Funeral service', '2026-01-26 08:00:00'),
(2, 2, 3, 'Service 002', 2, 'issued', 'Funeral service', '2026-01-26 10:30:00'),
(3, 4, 5, 'Service 003', 1, 'issued', 'Funeral service', '2026-01-27 09:15:00'),
(4, 8, 1, 'Service 004', 2, 'issued', 'Funeral service', '2026-01-27 14:00:00'),
(5, 10, 3, 'Service 005', 1, 'issued', 'Funeral service', '2026-01-28 11:45:00'),
(6, 11, 8, 'Service 006', 2, 'issued', 'Embalming', '2026-01-28 13:20:00'),
(7, 3, 1, 'Service 007', 1, 'issued', 'Funeral service', '2026-01-29 10:10:00'),
(8, 5, 2, 'Service 008', 2, 'issued', 'Funeral service', '2026-01-29 15:30:00'),
(9, 12, 4, 'Service 009', 1, 'issued', 'Embalming', '2026-02-01 09:00:00'),
(10, 9, 1, 'Service 010', 2, 'issued', 'Funeral service', '2026-02-02 12:00:00');

-- ============================================================================
-- VERIFICATION QUERIES - RUN THESE TO VERIFY SETUP
-- ============================================================================

-- Verify database was created
SELECT DATABASE() as 'Current Database';

-- Check table count
SELECT COUNT(*) as 'Total Tables' FROM information_schema.tables WHERE table_schema = 'dbfms';

-- List all tables
SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = 'dbfms';

-- Verify Users
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM inv_user;

-- Verify Suppliers
SELECT 'Suppliers' as Table_Name, COUNT(*) as Record_Count FROM inv_supplier;

-- Verify Items
SELECT 'Items' as Table_Name, COUNT(*) as Record_Count FROM inv_item;

-- Verify Stock Additions
SELECT 'Stock Additions' as Table_Name, COUNT(*) as Record_Count FROM inv_item_addition;

-- Verify Stock Issues
SELECT 'Stock Issues' as Table_Name, COUNT(*) as Record_Count FROM inv_item_issue;

-- Verify Audit Table
SELECT 'Stock Audit' as Table_Name, COUNT(*) as Record_Count FROM inv_stock_audit;

-- Show user accounts
SELECT id, full_name, email, role FROM inv_user;

-- Show all suppliers
SELECT id, name, city FROM inv_supplier;

-- Show all items with current stock
SELECT id, item_name, category, current_quantity, price_per_unit FROM inv_item;

-- Show total inventory statistics
SELECT 
    COUNT(*) as Total_Items,
    SUM(current_quantity) as Total_Units,
    ROUND(SUM(price_per_unit * current_quantity), 2) as Total_Value
FROM inv_item;
