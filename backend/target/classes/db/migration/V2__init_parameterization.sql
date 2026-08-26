CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO branches (id, code, name, address, city, country, created_at)
VALUES (DEFAULT, '0001','Main Branch', 'Av. Amazonas and Colón', 'Quito', 'Ecuador', NOW());



-- =========================================================
-- 1. MODULO: CONFIGURATION (Nivel 1)
-- =========================================================
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'CONFIGURATION', NULL, 'MODULE', 'CONF', 'Configuration', '⚙️', 1, false);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'CORE', NULL, 'MODULE', 'CORE', 'Core Banking', '💼', 2, false);

-- =========================================================
-- 2. SUBMODULOS DE CONFIGURATION (Nivel 2)
-- =========================================================

-- CONTROL SYSTEM
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'CONTROL_SYSTEM', (SELECT id FROM functionalities WHERE name='CONFIGURATION' AND type='MODULE'), 'SUBMODULE', 'CTRL', 'Control System', '🖥️', 1, false);

-- FUNCTIONALITIES
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'FUNCTIONALITIES', (SELECT id FROM functionalities WHERE name='CONFIGURATION' AND type='MODULE'), 'SUBMODULE', 'FUNC', 'Functionalities', '✨', 2, false);

-- USERS
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'USERS', (SELECT id FROM functionalities WHERE name='CONFIGURATION' AND type='MODULE'), 'SUBMODULE', 'USER', 'Users', '👥', 3, false);

-- BRANCHES
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'BRANCHES', (SELECT id FROM functionalities WHERE name='CONFIGURATION'), 'SUBMODULE', 'BRAN', 'Branches', '💸', 4, false);

-- PRODUCTS
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'PRODUCTS', (SELECT id FROM functionalities WHERE name='CONFIGURATION'), 'SUBMODULE', 'PROD', 'Products', '📊', 5, false);

-- SUBPRODUCTS
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'SUBPRODUCTS', (SELECT id FROM functionalities WHERE name='CONFIGURATION'), 'SUBMODULE', 'SPRO', 'Subproducts', '📈', 6, false);

--CUSTOMERS
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'CUSTOMERS', (SELECT id FROM functionalities WHERE name='CORE'), 'SUBMODULE', 'CUST', 'Customers', '👥', 7, false);

--ACCOUNTS
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'ACCOUNTS', (SELECT id FROM functionalities WHERE name='CORE'), 'SUBMODULE', 'ACCO', 'Accounts', '📈', 8, false);

-- =========================================================
-- 3. ACCIONES DE CONTROL SYSTEM (Nivel 3)
-- =========================================================
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Query CtrlSys', (SELECT id FROM functionalities WHERE name='CONTROL_SYSTEM' AND type='SUBMODULE'), 'ACTION', 'CTRL_QUERY', 'Query System', '🔍', 1, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Update CtrlSys', (SELECT id FROM functionalities WHERE name='CONTROL_SYSTEM' AND type='SUBMODULE'), 'ACTION', 'CTRL_UPDATE', 'Update System', '📝', 2, true);

-- =========================================================
-- 4. ACCIONES DE FUNCTIONALITIES (Nivel 3)
-- =========================================================
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Query Funct', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_QUERY', 'Query Functionalities', '🔍', 1, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Funct', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_CREATE', 'Create Functionalities', '➕', 2, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Query Rol', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_QUERY_ROL', 'Query Roles', '🔍', 3, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Rol', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_CREATE_ROL', 'Create Roles', '➕', 4, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Query Permits', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_QUERY_PERM', 'Query Permits', '🔍', 5, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Update Permits', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_UPDATE_PERM', 'Update Permits', '✏️', 6, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Permits', (SELECT id FROM functionalities WHERE name='FUNCTIONALITIES' AND type='SUBMODULE'), 'ACTION', 'FUNC_CREATE_PERM', 'Create Permits', '➕', 7, true);

-- =========================================================
-- 5. ACCIONES DE USERS (Nivel 3)
-- =========================================================
INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Query User', (SELECT id FROM functionalities WHERE name='USERS' AND type='SUBMODULE'), 'ACTION', 'USER_QUERY', 'Query by ID', '🆔', 1, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Update User', (SELECT id FROM functionalities WHERE name='USERS' AND type='SUBMODULE'), 'ACTION', 'USER_UPDATE', 'Update User', '✏️', 2, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create User', (SELECT id FROM functionalities WHERE name='USERS' AND type='SUBMODULE'), 'ACTION', 'USER_CREATE', 'Create User', '➕', 3, true);

-- =========================================================
-- 6. ACCIONES DE BRANCHES (Nivel 3)
-- =========================================================

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Branch', (SELECT id FROM functionalities WHERE name='BRANCHES'), 'ACTION', 'BRAN_QUERY', 'Query Branch', '🔍', 1, false);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Update Branch', (SELECT id FROM functionalities WHERE name='BRANCHES'), 'ACTION', 'BRAN_UPDATE', 'Update Branch', '✏️', 2, true);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Create Branch', (SELECT id FROM functionalities WHERE name='BRANCHES'), 'ACTION','BRAN_CREATE', 'Create Branch', '➕', 3, true);

-- =========================================================
-- 7. ACCIONES DE PRODUCTS (Nivel 3)
-- =========================================================

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Product', (SELECT id FROM functionalities WHERE name='PRODUCTS'), 'ACTION', 'PROD_QUERY', 'Query Products', '🔍', 1, false);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Create Product', (SELECT id FROM functionalities WHERE name='PRODUCTS'), 'ACTION','PROD_CREATE', 'Create Products', '➕', 2, true);

-- =========================================================
-- 8. ACCIONES DE SUBPRODUCTS (Nivel 3)
-- =========================================================

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Subproduct', (SELECT id FROM functionalities WHERE name='SUBPRODUCTS'), 'ACTION', 'SPRO_QUERY', 'Query Subproduct', '🔍', 1, false);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Create Subproduct', (SELECT id FROM functionalities WHERE name='SUBPRODUCTS'), 'ACTION','SPRO_CREATE', 'Create Subproducts', '➕', 2, true);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Create Interest Subproduct', (SELECT id FROM functionalities WHERE name='SUBPRODUCTS'), 'ACTION','SPRO_CREATE_INT', 'Create Interest', '💰', 3, true);

-- =========================================================
-- 9. ACCIONES DE CUSTOMERS (Nivel 3)
-- =========================================================

INSERT INTO functionalities (id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Customer', (SELECT id FROM functionalities WHERE name='CUSTOMERS'), 'ACTION', 'CUST_QUERY', 'Query Customers', '🔍', 1, false);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Update Customer', (SELECT id FROM functionalities WHERE name='CUSTOMERS'), 'ACTION', 'CUST_UPDATE', 'Update Customers', '✏️', 2, true);

INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Create Customer', (SELECT id FROM functionalities WHERE name='CUSTOMERS'), 'ACTION','CUST_CREATE', 'Create Customers', '➕', 3, true);

-- =========================================================
-- 10. ACTIONS ACCOUNTS (Level 3)
-- =========================================================
INSERT INTO functionalities (id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Account', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_QUERY', 'Query Accounts', '🔍', 1, false);


INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Account', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_CREATE', 'Create Account', '➕', 2, true);

INSERT INTO functionalities (id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Hold', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_QUERY_HOLD', 'Query Holds', '🔍', 3, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Hold', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_CREATE_HOLD', 'Create Hold', '🔒', 4, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Release Hold', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_RELEASE_HOLD', 'Release Hold', '🔓', 5, true);

INSERT INTO functionalities (id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'Query Restriction', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_QUERY_RESTRICTION', 'Query Restriction', '🔍', 6, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Restriction', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_CREATE_RESTRICTION', 'Create Restriction', '🔒', 7, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Release Restriction', (SELECT id FROM functionalities WHERE name='ACCOUNTS' AND type='SUBMODULE'), 'ACTION', 'ACCO_RELEASE_RESTRICTION', 'Release Restriction', '🔓', 8, true);

-- ACTIONS TRANSACTIONS (Level 3)
INSERT INTO functionalities
(id, "name", parent_id, "type", code, label, icon, sort_order, block_batch)
VALUES(gen_random_uuid(), 'TRANSACTIONS', (SELECT id FROM functionalities WHERE name='CORE'), 'SUBMODULE', 'TRANS', 'Transactions', '💸', 8, false);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Create Movements', (SELECT id FROM functionalities WHERE name='TRANSACTIONS' AND type='SUBMODULE'), 'ACTION', 'TRANS_CREATE', 'Create Movements', '➕', 1, true);

INSERT INTO functionalities (id, name, parent_id, type, code, label, icon, sort_order, block_batch)
VALUES (gen_random_uuid(), 'Transfer Accounts', (SELECT id FROM functionalities WHERE name='TRANSACTIONS' AND type='SUBMODULE'), 'ACTION', 'TRANS_CREATETRACC', 'Transfer Accountss', '➕', 2, true);


-- CREATE ROLE
INSERT INTO roles (id, name, description) VALUES
(gen_random_uuid(), 'ADMINISTRATOR', 'Full system administrator with access to all functions');

INSERT INTO roles (id, name, description) VALUES
(gen_random_uuid(), 'AUDITOR', 'Authorized to perform read-only inquiries and audit logs');

-- ROLE & FUNCTIONALITIES
INSERT INTO roles_functionalities (role_id, functionality_id)
SELECT
    (SELECT id FROM roles WHERE name = 'ADMINISTRATOR'),
    id
FROM functionalities;

INSERT INTO roles_functionalities (role_id, functionality_id)
SELECT
    (SELECT id FROM roles WHERE name = 'AUDITOR'),
    id
FROM functionalities
WHERE code IN ('CONF', 'USER', 'USER_QUERY');

-- Insert ROL ADMIN
INSERT INTO users (id, first_name, last_name, login, email, "password", active, branch_id, created_at)
VALUES(gen_random_uuid(), 'Christian', 'Padilla', 'cpadilla', 'christian.padilla@fintech.com', '$2a$10$KrenAcsWDNPJYgnuMDaEk.5Pq1R.Vl9ol.xqKIat9ivg0GTshIoV.', true, (SELECT id FROM branches WHERE code = '0001'), NOW());

-- Insert users_roles
INSERT INTO users_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE login = 'cpadilla'), (SELECT id FROM roles WHERE name = 'ADMINISTRATOR'));

-- =============================================================================
-- INSERT DE PRODUCTO MACRO (AHORROS)
-- =============================================================================
INSERT INTO product (id, code, name, status, created_at)
VALUES (DEFAULT, 'SAV', 'Savings Accounts', 'ACTIVE', CURRENT_TIMESTAMP);

--**************************************************************************************
-- =============================================================================
-- 1. CREACIÓN DEL GRUPO DE TASAS (ESTRATEGIA DE INTERÉS)
-- =============================================================================
-- Definimos el contenedor de la lógica de intereses
INSERT INTO interest_group (id, code, name, description, status, created_at)
VALUES (DEFAULT, 'INT-SAV', 'Interest strategy for savings accounts', 'Tiered rates for savings products with increasing balances', 'ACTIVE', CURRENT_TIMESTAMP);

-- =============================================================================
-- 2. CONFIGURACIÓN DE LOS RANGOS (ASOCIADOS AL GRUPO)
-- =============================================================================

-- Rango 1: 100.00 a 1,000.00 -> 2% (0.0200)
INSERT INTO interest_range (id, group_id, range_name, rate_value, rate_type, min_amount, max_amount, status, created_at)
VALUES (DEFAULT, (SELECT id FROM interest_group WHERE code = 'INT-SAV'), 'Rango Inicial', 0.0200, 'EA', 100.0000, 1000.0000, 'ACTIVE', CURRENT_TIMESTAMP);

-- Rango 2: 1,000.01 a Infinito -> 4% (0.0400)
INSERT INTO interest_range (id, group_id, range_name, rate_value, rate_type, min_amount, max_amount, status, created_at)
VALUES (DEFAULT, (SELECT id FROM interest_group WHERE code = 'INT-SAV'), 'Rango Premium', 0.0400, 'EA', 1000.0100, 999999999999.9999, 'ACTIVE', CURRENT_TIMESTAMP);

-- =============================================================================
-- 3. INSERT DE SUBPRODUCTO (APUNTANDO AL GRUPO DE TASAS)
-- =============================================================================
INSERT INTO subproduct (id, product_id, interest_group_id, code, name, description, status, generates_interest, statement_frequency, created_at, account_sequence_id)
VALUES (DEFAULT, (SELECT id FROM product WHERE code = 'SAV' LIMIT 1), (SELECT id FROM interest_group WHERE code = 'INT-SAV'), 'SAV01', 'Scheduled Savings Plus', 'Account linked to tiered rate strategy INT-SAV', 'ACTIVE', TRUE, 'MONTHLY', CURRENT_TIMESTAMP, 'SAV_GLOBAL');


-- 1. INSERTAR LOS CATÁLOGOS PADRE (LAS CATEGORÍAS)
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'CUSTOMER_STATUS', 'Possible states for a client in the system');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'IDENTIFICATION_TYPE', 'Types of identity documents allowed');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'ADDRESS_TYPE', 'Types of home locations');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'EMAIL_TYPE', 'Types of contact emails');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'PHONE_TYPE', 'Types of contact numbers');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'GENDER', 'Available gender in the system');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'PRODUCT_STATUS', 'Status for financial products');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'SUBPRODUCT_STATUS', 'Status for subproducts');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'MARITAL_STATUS', 'Available marital statuses');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'NATIONALITY', 'Nationalities');

-- 2. INSERTAR DETALLES DE CATÁLOGO (LOS VALORES REALES)

-- Estados del Cliente
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'CUSTOMER_STATUS'), 'ACT', 'ACTIVE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'CUSTOMER_STATUS'), 'INA', 'INACTIVE', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'CUSTOMER_STATUS'), 'PND', 'Pending Verification', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'CUSTOMER_STATUS'), 'BLK', 'Blocked for Security', 4);

-- Tipos de Identificación
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'IDENTIFICATION_TYPE'), 'DNI', 'National Identity Card', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'IDENTIFICATION_TYPE'), 'PAS', 'Passport', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'IDENTIFICATION_TYPE'), 'CED', 'Residence Card', 3);

-- Tipos de Dirección
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ADDRESS_TYPE'), 'HOME', 'Residence / Home', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ADDRESS_TYPE'), 'WORK', 'Office / Work', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ADDRESS_TYPE'), 'MAILING', 'Mailing Address', 3);

-- Tipos de Contacto (Para Teléfonos y Emails)
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'EMAIL_TYPE'), 'PERS', 'Personal', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'EMAIL_TYPE'), 'CORP', 'Corporate / Employment', 2);

-- Tipos de Contacto (Para Teléfonos)
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'PHONE_TYPE'), 'MOVIL', 'Personal', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'PHONE_TYPE'), 'HOUSE', 'House', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'PHONE_TYPE'), 'OFFICE', 'Office', 3);

-- 2. INSERTAR DETALLES PARA ESTADOS DE PRODUCTO (PRODUCT_STATUS)
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'PRODUCT_STATUS'), 'ACT', 'ACTIVE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'PRODUCT_STATUS'), 'INA', 'INACTIVE', 2);

-- 3. INSERTAR DETALLES PARA ESTADOS DE SUBPRODUCTO (SUBPRODUCT_STATUS)
-- Nota A veces los subproductos tienen estados más específicos como Piloto o Restringido

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'SUBPRODUCT_STATUS'), 'ACT', 'ACTIVE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'SUBPRODUCT_STATUS'), 'INA', 'INACTIVE', 2);

-- 1. INSERTAR LOS CATÁLOGOS PADRE
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'USER_STATUS', 'System User Status');
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'BRANCH_STATUS', 'Operational status of physical branches');

-- 2. INSERTAR DETALLES PARA ESTADOS DE USUARIO (USER_STATUS)
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'USER_STATUS'), 'ACT', 'ACTIVE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'USER_STATUS'), 'INA', 'INACTIVE', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'USER_STATUS'), 'LKD', 'Blocked Due to Failed Attempts', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'USER_STATUS'), 'EXP', 'Password Expired', 4);

-- 3. INSERTAR DETALLES PARA ESTADOS DE SUCURSAL (BRANCH_STATUS)
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'BRANCH_STATUS'), 'OPR', 'Operational', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'BRANCH_STATUS'), 'CLO', 'Temporarily Closed', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'BRANCH_STATUS'), 'MNT', 'Under Maintenance', 3);

--GENDER
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'GENDER'), 'MALE', 'MALE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'GENDER'), 'FEMALE', 'FEMALE', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'GENDER'), 'NO SPECIFIED', 'NO SPECIFIED', 3);

--MARITAL STATUS
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'MARITAL_STATUS'), 'SINGLE', 'SINGLE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'MARITAL_STATUS'), 'MARRIED', 'MARRIED', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'MARITAL_STATUS'), 'DIVORCED', 'DIVORCED', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'MARITAL_STATUS'), 'COMMON-LAW MARRIAGE', 'COMMON-LAW MARRIAGE', 4);

--NATIONALITY
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'AMERICAN', 'AMERICAN', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'ARGENTINIAN', 'ARGENTINIAN', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'BRAZILIAN', 'BRAZILIAN', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'CANADIAN', 'CANADIAN', 4);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'COLOMBIAN', 'COLOMBIAN', 5);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'ECUADORIAN', 'ECUADORIAN', 6);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'MEXICAN', 'MEXICAN', 7);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'PERUVIAN', 'PERUVIAN', 8);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'NATIONALITY'), 'SPAIN', 'SPAIN', 9);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'COUNTRY', 'Countries');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'UNITED STATES', 'UNITED STATES', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'ARGENTINA', 'ARGENTINA', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'BRAZIL', 'BRAZIL', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'CANADA', 'CANADA', 4);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'COLOMBIA', 'COLOMBIA', 5);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'ECUADOR', 'ECUADOR', 6);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'MEXICO', 'MEXICO', 7);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'PERU', 'PERU', 8);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'COUNTRY'), 'SPAIN', 'SPAIN', 9);

-- OWNERSHIP
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'OWNERSHIP', 'Ownership');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'OWNERSHIP'), 'INDIVIDUAL', 'INDIVIDUAL', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'OWNERSHIP'), 'JOINT', 'JOINT', 2);

-- ROLES HOLDER
INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'ROLES_HOLDER', 'Roles Holder');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ROLES_HOLDER'), 'OWNER', 'OWNER', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ROLES_HOLDER'), 'CO-OWNER', 'CO-OWNER', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ROLES_HOLDER'), 'SIGNATORY', 'SIGNATORY', 3);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'CURRENCY', 'Currency');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'CURRENCY'), 'USD', 'USD', 1);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'ACCOUNT_STATUS', 'Account Status');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'ACCOUNT_STATUS'), 'ACTIVE', 'ACTIVE', 1);
--CHANNEL
INSERT INTO catalog (id, name, description) VALUES
    (DEFAULT, 'CHANNEL', 'Channel');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
    (DEFAULT, (SELECT id FROM catalog WHERE name = 'CHANNEL'), 'ONLINE', 'ONLINE', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
    (DEFAULT, (SELECT id FROM catalog WHERE name = 'CHANNEL'), 'BATCH', 'BATCH', 2);

--TRANSACTIONS
INSERT INTO catalog (id, name, description) VALUES
    (DEFAULT, 'TRANSACTION', 'Transaction');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'TRANSACTION'), 'DB', 'Debit', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'TRANSACTION'), 'CR', 'Credit', 2);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'HOLD_STATUS', 'Hold Status');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_STATUS'), 'ACTIVE', 'ACTIVE', 1);

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_STATUS'), 'RELEASED', 'RELEASED', 2);

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_STATUS'), 'EXPIRED', 'EXPIRED', 3);

INSERT INTO catalog (id, name, description) VALUES
    (DEFAULT, 'HOLD_RELEASE', 'Hold Release');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
    (DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_RELEASE'), 'MANUAL', 'MANUAL', 1);

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
    (DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_RELEASE'), 'BATCH_EOD', 'BATCH_EOD', 2);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'HOLD_TYPE', 'Hold Type');

INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_TYPE'), 'JUDICIAL_LIEN', 'JUDICIAL_LIEN', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_TYPE'), 'TAX_LEVY', 'TAX_LEVY', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_TYPE'), 'CHILD_SUPPORT', 'CHILD_SUPPORT', 3);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'HOLD_TYPE'), 'LOAN_COLLATERAL', 'LOAN_COLLATERAL', 4);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'RESTRICTION_TYPE', 'Restriction Type');
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_TYPE'), 'DEBIT', 'DEBIT', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_TYPE'), 'CREDIT', 'CREDIT', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_TYPE'), 'TOTAL', 'TOTAL', 3);

INSERT INTO catalog (id, name, description) VALUES
(DEFAULT, 'RESTRICTION_CAT', 'Restriction Type');
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_CAT'), 'USER_REQUEST', 'USER_REQUEST', 1);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_CAT'), 'BANK_REQUEST', 'BANK_REQUEST', 2);
INSERT INTO catalog_detail (id, catalog_id, code, name, sort_order) VALUES
(DEFAULT, (SELECT id FROM catalog WHERE name = 'RESTRICTION_CAT'), 'LEGAL', 'LEGAL', 3);

-- =============================================================================
-- Inserción inicial (Punto de partida del sistema) control_system
-- =============================================================================
INSERT INTO control_system (id, business_date, before_business_date, after_business_date, status, version, current_job_execution_id, eod_start_at, is_month_end)
VALUES (1,'2026-03-16','2026-03-15','2026-03-17','ACTIVE',0,NULL,NULL, FALSE );

INSERT INTO account_sequences (sequence_id, prefix, current_value, length, description) VALUES
('SAV_GLOBAL', '10', 0, 8, 'Global Sequence for Savings Accounts');

INSERT INTO account_sequences (sequence_id, prefix, current_value, length, description) VALUES
('CHK_GLOBAL', '20', 0, 8, 'Global Sequence for Checking Accounts');

-- ==========================================================
-- SEED DATA: TRANSACTION TYPES (tr_types)
-- ==========================================================
INSERT INTO tr_types (type_id, code, name) VALUES
(1, 'CR', 'CREDIT NOTE'),
(2, 'DB', 'DEBIT NOTE');
-- ==========================================================
-- SEED DATA: TRANSACTION REASONS (tr_reasons)
-- ==========================================================

-- 10 CREDIT REASONS (NC - Money In)
INSERT INTO tr_reasons (reason_id, type_id, name) VALUES
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'ATM CASH DEPOSIT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'LOCAL BRANCH DEPOSIT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'INCOMING WIRE TRANSFER'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'INTEREST PAYOUT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'SALARY DEPOSIT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'TAX REFUND'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'LOAN DISBURSEMENT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'MERCHANT REVERSAL'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'PROMOTIONAL BONUS'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'CASHBACK REWARD');

-- 10 DEBIT REASONS (ND - Money Out)
INSERT INTO tr_reasons (reason_id, type_id, name) VALUES
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'ATM CASH WITHDRAWAL'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'POINT OF SALE PURCHASE'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'OUTGOING WIRE TRANSFER'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'MONTHLY MAINTENANCE FEE'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'ATM SURCHARGE'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'LOAN INSTALLMENT PAYMENT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'UTILITY BILL PAYMENT'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'OVERDRAFT FEE'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'FOREIGN EXCHANGE COMMISSION'),
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'DB'),'INTERNAL TRANSFER DEBIT');

INSERT INTO tr_reasons (reason_id, type_id, name) VALUES
(DEFAULT, (SELECT type_id FROM tr_types WHERE code = 'CR'),'INTERNAL TRANSFER CREDIT');

-- NC CONFIGURATIONS (type_id = 1)
INSERT INTO tr_configs (type_id, reason_id, mnemonic) VALUES
((SELECT type_id FROM tr_types WHERE code = 'CR'), 1, 'ATM_DEP'),     -- ATM CASH DEPOSIT
((SELECT type_id FROM tr_types WHERE code = 'CR'), 2, 'LOC_DEP'),     -- LOCAL BRANCH DEPOSIT
((SELECT type_id FROM tr_types WHERE code = 'CR'), 3, 'INC_WIRE'),    -- INCOMING WIRE TRANSFER
((SELECT type_id FROM tr_types WHERE code = 'CR'), 4, 'INT_PAY'),     -- INTEREST PAYOUT
((SELECT type_id FROM tr_types WHERE code = 'CR'), 5, 'SALARY'),      -- SALARY DEPOSIT
((SELECT type_id FROM tr_types WHERE code = 'CR'), 6, 'TAX_REF'),     -- TAX REFUND
((SELECT type_id FROM tr_types WHERE code = 'CR'), 7, 'LOAN_DISB'),   -- LOAN DISBURSEMENT
((SELECT type_id FROM tr_types WHERE code = 'CR'), 8, 'MERCH_REV'),   -- MERCHANT REVERSAL
((SELECT type_id FROM tr_types WHERE code = 'CR'), 9, 'PROM_BON'),    -- PROMOTIONAL BONUS
((SELECT type_id FROM tr_types WHERE code = 'CR'), 10, 'CASH_REW'),   -- CASHBACK REWARD
((SELECT type_id FROM tr_types WHERE code = 'CR'), 11, 'ITRCR');      -- INTEREST PAY
-- ND CONFIGURATIONS (type_id = 2)
INSERT INTO tr_configs (type_id, reason_id, mnemonic) VALUES
((SELECT type_id FROM tr_types WHERE code = 'DB'), 12, 'ATM_WTH'),    -- ATM CASH WITHDRAWAL
((SELECT type_id FROM tr_types WHERE code = 'DB'), 13, 'OUT_WIRE'),   -- OUTGOING WIRE TRANSFER
((SELECT type_id FROM tr_types WHERE code = 'DB'), 14, 'MAINT_FEE'),  -- MONTHLY MAINTENANCE FEE
((SELECT type_id FROM tr_types WHERE code = 'DB'), 15, 'ATM_SUR'),    -- ATM SURCHARGE
((SELECT type_id FROM tr_types WHERE code = 'DB'), 16, 'LOAN_PMT'),   -- LOAN INSTALLMENT PAYMENT
((SELECT type_id FROM tr_types WHERE code = 'DB'), 17, 'UTIL_PMT'),   -- UTILITY BILL PAYMENT
((SELECT type_id FROM tr_types WHERE code = 'DB'), 18, 'OVR_FEE'),    -- OVERDRAFT FEE
((SELECT type_id FROM tr_types WHERE code = 'DB'), 19, 'FX_COMM'),    -- FOREIGN EXCHANGE COMMISSION
((SELECT type_id FROM tr_types WHERE code = 'DB'), 20, 'POS_PUR'),    -- POINT OF SALE PURCHASE
((SELECT type_id FROM tr_types WHERE code = 'DB'), 21, 'INT_DEB');    -- INTERNAL TRANSFER DEBIT



