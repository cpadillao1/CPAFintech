-- =============================================================================
-- Tabla de Control Central del Sistema - control_system
-- =============================================================================
CREATE TABLE control_system (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Forzamos a que solo exista el registro con ID 1
    business_date DATE NOT NULL,           -- T: Fecha actual de operación
    before_business_date DATE NOT NULL,    -- T-1: Día anterior (Auditoría/Históricos)
    after_business_date DATE NOT NULL,     -- T+1: Siguiente día hábil (Proyecciones)
    status VARCHAR(20) NOT NULL,           -- 'ACTIVE', 'IN_CLOSING', 'BATCH_RUNNING'
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    current_job_execution_id BIGINT,
    eod_start_at TIMESTAMP,
    is_month_end BOOLEAN,
    -- Validación de integridad de fechas
    CONSTRAINT chk_dates_sequence CHECK (
        before_business_date < business_date AND
        business_date < after_business_date
        ),
    -- Validación de estados permitidos
    CONSTRAINT chk_status_values CHECK (
        status IN ('ACTIVE', 'IN_CLOSING', 'BATCH_RUNNING', 'CLOSED')
        )
);

-- Branches table
CREATE TABLE branches (
    id SERIAL PRIMARY KEY,
    code VARCHAR(4) UNIQUE NOT NULL,
    name VARCHAR(60) NOT NULL,
    address VARCHAR(200) NOT NULL ,
    city VARCHAR(60) NOT NULL,
    country VARCHAR(60) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- Permissions table
CREATE TABLE permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200)
);

-- Functionalities table
CREATE TABLE functionalities (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_id UUID REFERENCES functionalities(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL, -- e.g. MODULE, SUBMODULE, ACTION
    code VARCHAR(30) UNIQUE,
    label VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    block_batch BOOLEAN NOT NULL DEFAULT FALSE
);

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    login VARCHAR(30) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    branch_id INT REFERENCES branches(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    status VARCHAR(10) NOT NULL,      -- 'SUCCESS' o 'ERROR'
    detail TEXT,
    ip_address VARCHAR(45),
    audit_date DATE NOT NULL DEFAULT CURRENT_DATE, -- Optimizado para reportes diarios
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_status ON audit_logs(status);
CREATE INDEX idx_audit_date ON audit_logs(audit_date);
CREATE INDEX idx_audit_composite ON audit_logs(audit_date, status); -- Para reportes de "Errores de hoy"
/*
-- Many-to-many relationship between roles and permissions
CREATE TABLE roles_permissions (
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
*/
-- Many-to-many relationship between roles and functionalities
CREATE TABLE roles_functionalities (
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    functionality_id UUID REFERENCES functionalities(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, functionality_id)
);

-- Many-to-many relationship between users and roles
CREATE TABLE users_roles (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- =============================================================================
-- 1. MÓDULO DE PRODUCTOS (CATÁLOGO)
-- =============================================================================

-- Tabla de Productos (Macro: Ahorros, Corrientes, Plazo Fijo)
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    code VARCHAR(15) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =============================================================================
-- 1. ESTRUCTURA DE PRODUCTOS Y SUBPRODUCTOS
-- =============================================================================

-- Tabla de Productos (Nivel Macro: Ahorros, Corriente, DPF)
-- (Asumo que ya existe, solo asegúrate de que el ID sea UUID)

-- Tabla de Grupos de Tasas (Estrategias de Interés)
-- Aquí defines el "paquete" de tasas (Ej: "Tasa Ahorro Premium 2026")
CREATE TABLE interest_group (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL, -- Ej: INT-SAV-PREM
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Tabla de Rangos de Interés (Tiering)
-- Aquí defines los escalones para cada grupo
CREATE TABLE interest_range (
    id SERIAL PRIMARY KEY,
    group_id INT NOT NULL, -- Relación con el Grupo, NO con el subproducto
    range_name VARCHAR(100) NOT NULL, -- Ej: Rango Inicial, Rango Oro
    rate_value DECIMAL(10, 6) NOT NULL, -- Ej: 0.020000 (2%)
    rate_type VARCHAR(20) NOT NULL,     -- EA, NOM
    min_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    max_amount DECIMAL(19, 4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_range_group FOREIGN KEY (group_id)
        REFERENCES interest_group (id) ON DELETE CASCADE,
    CONSTRAINT chk_range_amounts CHECK (max_amount > min_amount)
);

-- Tabla de Subproductos Actualizada
CREATE TABLE subproduct (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    interest_group_id INT,              -- Relación numérica con la estrategia de tasas
    code VARCHAR(15) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    generates_interest BOOLEAN NOT NULL DEFAULT FALSE,
    statement_frequency VARCHAR(10) NOT NULL DEFAULT 'MONTHLY',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    account_sequence_id VARCHAR(20),
    CONSTRAINT fk_subproduct_product FOREIGN KEY (product_id)
        REFERENCES product (id),
    CONSTRAINT fk_subproduct_interest_group FOREIGN KEY (interest_group_id)
        REFERENCES interest_group (id)
);

-- =============================================================================
-- ÍNDICES PARA PERFORMANCE BANCARIA (OPTIMIZADOS)
-- =============================================================================
-- Búsqueda de subproductos por producto padre
CREATE INDEX idx_subproduct_product ON subproduct(product_id);

-- El índice más importante para el EOD: Buscar la tasa según el saldo
-- Un índice compuesto sobre group_id y los montos acelera el cálculo de intereses
CREATE INDEX idx_interest_range_lookup
    ON interest_range(group_id, min_amount, max_amount)
    WHERE status = 'ACTIVE';

-- Búsqueda por código de grupo (Usado en parametrización)
CREATE INDEX idx_interest_group_code ON interest_group(code);

-- Relación Subproducto -> Grupo de Interés
CREATE INDEX idx_subproduct_interest_group ON subproduct(interest_group_id);

-- 1. TABLA CATÁLOGO (Padre de Parametría)
-- =============================================================================
-- REFACTORIZACIÓN DE CATÁLOGOS: LA BASE DEL RENDIMIENTO
-- =============================================================================

-- 1. TABLA MAESTRA DE CATÁLOGOS (CATALOG)
CREATE TABLE catalog (
     id SERIAL PRIMARY KEY,              -- Refactorizado a SERIAL (4 bytes)
     name VARCHAR(100) NOT NULL UNIQUE,  -- Ej: CUSTOMER_STATUS, DOC_TYPE
     description VARCHAR(255),
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP
);

-- 2. TABLA DETALLE DE CATÁLOGO (CATALOG_DETAIL)
CREATE TABLE catalog_detail (
    id BIGSERIAL PRIMARY KEY,              -- Refactorizado a SERIAL
    catalog_id INT NOT NULL,            -- Relación INT
    code VARCHAR(50) NOT NULL,          -- Ej: ACT, INA, PND
    name VARCHAR(100) NOT NULL,         -- Ej: Activo, Inactivo, Pendiente
    description VARCHAR(255),
    sort_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_catalog_detail_catalog FOREIGN KEY (catalog_id)
        REFERENCES catalog (id) ON DELETE CASCADE,
    CONSTRAINT uq_catalog_code UNIQUE (catalog_id, code)
);

-- ÍNDICES PARA BÚSQUEDA DINÁMICA
-- Este índice es vital para cargar combos en el Frontend rápidamente
CREATE INDEX idx_catalog_detail_lookup ON catalog_detail(catalog_id, is_active, sort_order);

-- Índice para búsquedas directas por código (muy usado en lógica de negocio/Backend)
CREATE INDEX idx_catalog_detail_code ON catalog_detail(code);

-- 1. TABLA CUSTOMER
CREATE TABLE customer (
      id BIGSERIAL PRIMARY KEY,
      first_name VARCHAR(100) NOT NULL,
      last_name VARCHAR(100) NOT NULL,
      document_type_id BIGINT NOT NULL,       -- Refactorizado a INT
      document_number VARCHAR(50) NOT NULL UNIQUE,
      birth_date DATE,
      gender_id BIGINT,                       -- Refactorizado a INT
      marital_status_id BIGINT,               -- Refactorizado a INT
      nationality_id INT,                  -- Refactorizado a INT
      status_id BIGINT NOT NULL,              -- Refactorizado a INT
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      created_by VARCHAR(30) NOT NULL,
      updated_at TIMESTAMP,
      updated_by VARCHAR(30),
      version BIGINT DEFAULT 0,
      CONSTRAINT fk_customer_status FOREIGN KEY (status_id) REFERENCES catalog_detail (id),
      CONSTRAINT fk_customer_doc_type FOREIGN KEY (document_type_id) REFERENCES catalog_detail (id),
      CONSTRAINT fk_customer_gender FOREIGN KEY (gender_id) REFERENCES catalog_detail (id),
      CONSTRAINT fk_customer_marital FOREIGN KEY (marital_status_id) REFERENCES catalog_detail (id)
);

-- 2. TABLA DIRECCIONES (customer_address)
CREATE TABLE customer_address (
      id BIGSERIAL PRIMARY KEY,
      customer_id BIGINT NOT NULL,         -- Relación BIGINT
      address_line VARCHAR(255) NOT NULL,
      city VARCHAR(100) NOT NULL,
      state VARCHAR(100),
      country VARCHAR(100) NOT NULL,
      postal_code VARCHAR(20),
      address_type_id BIGINT NOT NULL,        -- Refactorizado a INT
      is_primary BOOLEAN DEFAULT FALSE,
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      updated_at TIMESTAMP,
      CONSTRAINT fk_customer_address_customer FOREIGN KEY (customer_id)
          REFERENCES customer (id) ON DELETE CASCADE,
      CONSTRAINT fk_address_type FOREIGN KEY (address_type_id)
          REFERENCES catalog_detail (id)
);

-- 3. TABLA TELÉFONOS (customer_phone)
CREATE TABLE customer_phone (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,         -- Relación BIGINT
    phone_number VARCHAR(20) NOT NULL,
    phone_type_id BIGINT NOT NULL,          -- Refactorizado a INT
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_customer_phone_customer FOREIGN KEY (customer_id)
        REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_phone_type FOREIGN KEY (phone_type_id)
        REFERENCES catalog_detail (id)
);

-- 4. TABLA EMAILS (customer_email)
CREATE TABLE customer_email (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,         -- Relación BIGINT
    email VARCHAR(150) NOT NULL UNIQUE,
    email_type_id BIGINT NOT NULL,          -- Refactorizado a INT
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_customer_email_customer FOREIGN KEY (customer_id)
        REFERENCES customer (id) ON DELETE CASCADE,
    CONSTRAINT fk_email_type FOREIGN KEY (email_type_id)
        REFERENCES catalog_detail (id)
);

-- ÍNDICES ESTRATÉGICOS OPTIMIZADOS
-- Búsqueda por documento (Es el índice más usado en el FRONT)
CREATE INDEX idx_customer_doc_search ON customer(document_type_id, document_number);

-- Búsqueda por email (Para login o validaciones)
CREATE INDEX idx_customer_email_search ON customer_email(email);

-- Índice para búsquedas rápidas de datos de contacto por cliente
CREATE INDEX idx_phone_customer_id ON customer_phone(customer_id);
CREATE INDEX idx_address_customer_id ON customer_address(customer_id);

-- 1. TABLA DE CUENTAS (ACCOUNTS)
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,           -- Único ID de referencia
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    subproduct_id INT NOT NULL,
    branch_id INT NOT NULL,
    currency_id BIGINT NOT NULL,
    account_type VARCHAR(3) NOT NULL,   -- SAV / CHK
    ownership_type_id BIGINT NOT NULL,
    opening_date DATE NOT NULL,
    -- Saldos y Control (DECIMAL 19,4)
    available_balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    balance_today DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    balance_yesterday DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    amount_hold DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    -- Campos de Control EoD
    generates_interest BOOLEAN NOT NULL DEFAULT FALSE,
    last_processed_date DATE NOT NULL,
    accrued_interest_month DECIMAL(19, 10) DEFAULT 0.0,
    interest_remainder DECIMAL(19, 10) DEFAULT 0.0,
    -- Acumulados del día
    amount_nd_today DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    amount_nd_yesterday DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    last_date_nd TIMESTAMP,
    amount_nc_today DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    amount_nc_yesterday DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    last_date_nc TIMESTAMP,
    -- Configuración y Estado
    periodicity INT,
    months_accumulated INT,
    movement_restriction SMALLINT DEFAULT 0,
    status_id BIGINT NOT NULL,
    -- Auditoría y Concurrencia
    version INTEGER NOT NULL DEFAULT 0,
    last_movement_date TIMESTAMP,
    created_by VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    -- Restricciones de Integridad
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_accounts_subproduct FOREIGN KEY (subproduct_id) REFERENCES subproduct(id),
    CONSTRAINT fk_accounts_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_accounts_currency FOREIGN KEY (currency_id) REFERENCES catalog_detail (id),
    CONSTRAINT fk_customer_status FOREIGN KEY (status_id) REFERENCES catalog_detail (id),
    CONSTRAINT fk_accounts_ownership FOREIGN KEY (ownership_type_id) REFERENCES catalog_detail (id),
    CONSTRAINT ck_account_type CHECK (account_type IN ('SAV', 'CHK'))
);

-- INDICES OPTIMIZADOS PARA EOD
-- Indispensable para el barrido de intereses masivo
CREATE INDEX idx_accounts_eod_perfect_scan
    ON accounts (last_processed_date, status_id, generates_interest, id)
    INCLUDE (
        balance_today,
        interest_remainder,
        accrued_interest_month,
        available_balance,
        amount_hold,
        subproduct_id,
        periodicity,
        months_accumulated,
        amount_nc_today,
        amount_nd_today
    )
    WHERE (generates_interest = TRUE AND status_id = 58);

-- Para reportes rápidos de saldos
CREATE INDEX idx_accounts_balances
    ON accounts (currency_id, branch_id) INCLUDE (balance_today);

-- 2. TABLA DE DUEÑOS DE CUENTA (ACCOUNT_HOLDERS)
CREATE TABLE account_holders (
     id BIGSERIAL PRIMARY KEY,
     account_id BIGINT NOT NULL,
     customer_id BIGINT NOT NULL,
     holder_type_id BIGINT NOT NULL,
     status_id BIGINT NOT NULL,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     created_by VARCHAR(30),
     updated_at TIMESTAMP,
     CONSTRAINT fk_holder_account FOREIGN KEY (account_id) REFERENCES accounts(id),
     CONSTRAINT fk_holder_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
     CONSTRAINT fk_holder_type FOREIGN KEY (holder_type_id) REFERENCES catalog_detail(id),
     CONSTRAINT fk_holder_status FOREIGN KEY (status_id) REFERENCES catalog_detail(id),
     CONSTRAINT uk_account_customer_role UNIQUE (account_id, customer_id, holder_type_id)
);

-- Índices de búsqueda para la relación Cliente-Cuenta
CREATE INDEX idx_holders_customer_lookup ON account_holders(customer_id, status_id);
CREATE INDEX idx_holders_account_lookup ON account_holders(account_id);


-- 3. MOTOR DE DEVENGO (ACCRUED_INTEREST)
CREATE TABLE accrued_interest (
    account_id BIGINT PRIMARY KEY,
    total_accrued DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    residual_amount DECIMAL(19, 10) NOT NULL DEFAULT 0.0000000000,
    last_update_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accrual_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

-- Índice para el barrido diario del EoD
CREATE INDEX idx_accrual_last_date ON accrued_interest(last_update_date);

CREATE TABLE account_sequences (
    sequence_id VARCHAR(20) PRIMARY KEY, -- Ej: 'SAV_OFC_001' (Ahorros Oficina 1)
    prefix VARCHAR(10) NOT NULL,        -- Prefijo que irá al inicio del nro de cuenta
    current_value BIGINT NOT NULL,      -- El último número utilizado
    length INTEGER NOT NULL,            -- Longitud total del correlativo (sin contar prefijo)
    description VARCHAR(100),
    version BIGINT DEFAULT 0            -- Para control de concurrencia (Optimistic Locking)
);

CREATE TABLE account_balance_snapshot (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    -- Saldos al cierre del día
    available_balance DECIMAL(19, 4) NOT NULL,
    balance_today DECIMAL(19, 4) NOT NULL,
    amount_hold DECIMAL(19, 4) NOT NULL,
    -- Detalle del procesamiento de ese día
    applied_rate DECIMAL(10, 6),          -- Tasa aplicada según el rango
    interest_day DECIMAL(19, 10),         -- Interés puro generado hoy
    remainder_before DECIMAL(19, 10),     -- Remanente que venía de ayer
    remainder_after DECIMAL(19, 10),      -- Remanente que queda para mañana
    gross_interest DECIMAL(19, 10) DEFAULT 0.0,
    -- Acumulado para el mes (lo que ya se ve en el balance)
    accrued_month_to_date DECIMAL(19, 10),

    -- Acumulados de movimientos para conciliación
    total_nd_day DECIMAL(19, 4),
    total_nc_day DECIMAL(19, 4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_snapshot_account FOREIGN KEY (account_id)
        REFERENCES accounts(id) ON DELETE CASCADE
);

-- 3. Índices para optimización del Snapshot
-- Índice compuesto para búsquedas por fecha (útil para estados de cuenta)
CREATE INDEX idx_snapshot_date_acc ON account_balance_snapshot (snapshot_date, account_id);
-- Para reportes de saldos históricos
CREATE INDEX idx_snapshot_acc_id ON account_balance_snapshot (account_id);

-- Tabla para el detalle de bloqueos (fondos retenidos)
CREATE TABLE account_holds (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    -- Información del Bloqueo
    amount DECIMAL(19, 4) NOT NULL,
    hold_type_id BIGINT NOT NULL,          -- Referencia a catalog_detail (Judicial, Pignoración, Garantía)
    status_id BIGINT NOT NULL,             -- Referencia a catalog_detail (ACTIVE, RELEASED, EXPIRED)
    -- Referencias Externas y Motivo
    reference_number VARCHAR(50),       -- Nro de oficio, nro de contrato, etc.
    description TEXT,                   -- Descripción inicial del porqué del bloqueo
    -- Control de Tiempo Contable (Optimizado para procesos Batch)
    start_date DATE NOT NULL,
    expiry_date DATE,                   -- El Job de EoD buscará esta fecha
    -- Auditoría y Detalle de Liberación
    release_type_id BIGINT,                -- Referencia a catalog_detail (MANUAL, AUTOMATIC)
    released_at TIMESTAMP,              -- Momento exacto de la liberación
    released_by VARCHAR(30),            -- Usuario o 'SYSTEM_EOD'
    release_observations TEXT,          -- JUSTIFICACIÓN DE LA LIBERACIÓN (Nuevo campo solicitado)
    -- Auditoría de Creación y Concurrencia
    created_by VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    -- Restricciones de Integridad
    CONSTRAINT fk_holds_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_holds_type FOREIGN KEY (hold_type_id) REFERENCES catalog_detail(id),
    CONSTRAINT fk_holds_status FOREIGN KEY (status_id) REFERENCES catalog_detail(id),
    CONSTRAINT fk_holds_release_type FOREIGN KEY (release_type_id) REFERENCES catalog_detail(id),
    CONSTRAINT ck_hold_amount CHECK (amount > 0)
);

-- INDICE CLAVE PARA EL JOB DE CIERRE (EoD)
-- Optimiza la búsqueda de bloqueos que vencen hoy para su liberación automática
-- Usamos un ID estático o una subquery si conoces el nombre del estado
CREATE INDEX idx_holds_eod_expiry
    ON account_holds (expiry_date, status_id);

-- Índice para la pantalla de gestión de bloqueos por cuenta
CREATE INDEX idx_holds_account_search ON account_holds (account_id, status_id);

-- Índice para auditoría de liberaciones manuales
CREATE INDEX idx_holds_release_audit ON account_holds (released_by, released_at) WHERE (released_by IS NOT NULL);

-- Flyway Migration: Create Account Restrictions Table (Audit-Login Optimized)
-- Description: Core table for account blocking with user login traceability

CREATE TABLE account_restrictions (
    id                      BIGSERIAL       PRIMARY KEY,
    account_id              BIGINT          NOT NULL,
    -- References to Catalog system (assuming BIGINT for catalogs)
    restriction_type_cat_id  BIGINT          NOT NULL,
    reason_code_cat_id       BIGINT          NOT NULL,
    status_cat_id            BIGINT          NOT NULL,
    -- Date fields for operational validity
    start_date              DATE            NOT NULL DEFAULT CURRENT_DATE,
    end_date                DATE,                     -- Nullable for manual release
    -- Blocking Audit (Login-based)
    authorizer              VARCHAR(30)     NOT NULL,
    observations            VARCHAR(500),
    -- Release Audit (Login-based, filled on manual lift)
    release_authorizer       VARCHAR(30),
    release_observations     VARCHAR(500),
    release_date             TIMESTAMP,               -- Exact moment of release
    -- Internal Audit timestamps
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    -- Constraints
    CONSTRAINT fk_ar_account
        FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_ar_restriction_type
        FOREIGN KEY (restriction_type_cat_id) REFERENCES catalog_detail(id),
    CONSTRAINT fk_ar_reason_code
        FOREIGN KEY (reason_code_cat_id) REFERENCES catalog_detail(id),
    CONSTRAINT fk_ar_status
        FOREIGN KEY (status_cat_id) REFERENCES catalog_detail(id)

);

-- Index for high-speed transaction validation
--CREATE INDEX idx_ar_account_active_status ON account_restrictions(account_id, status_cat_id);

CREATE UNIQUE INDEX uk_account_type_active
    ON account_restrictions (account_id, restriction_type_cat_id, status_cat_id)
    WHERE (status_cat_id = 63);


-- 1. Definición básica (Usa SMALLINT para ahorrar espacio)
CREATE TABLE tr_types (
    type_id INTEGER PRIMARY KEY,
    code VARCHAR(5) UNIQUE,
    name VARCHAR(50)
);

-- 2. El origen o razón
CREATE TABLE tr_reasons (
    reason_id SERIAL PRIMARY KEY,
    type_id INTEGER NOT NULL, -- Relación obligatoria
    name VARCHAR(50) UNIQUE,
    CONSTRAINT fk_reason_type FOREIGN KEY (type_id) REFERENCES tr_types(type_id)
);

-- 3. La configuración Maestra
CREATE TABLE tr_configs (
    config_id BIGSERIAL PRIMARY KEY,
    type_id INTEGER REFERENCES tr_types(type_id),
    reason_id INT REFERENCES tr_reasons(reason_id),
    mnemonic VARCHAR(10) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_config_type FOREIGN KEY (type_id) REFERENCES tr_types(type_id),
    CONSTRAINT fk_config_reason FOREIGN KEY (reason_id) REFERENCES tr_reasons(reason_id),
    -- Esta restricción asegura que no se repita la combinación
    CONSTRAINT unique_combination UNIQUE(type_id, reason_id)
);

CREATE TABLE log_mov (
     id SERIAL PRIMARY KEY,
     account_id BIGSERIAL NOT NULL,
    -- Ajustado para que apunte a catalog_detail
     transaction_type_id BIGINT NOT NULL,
     business_date DATE NOT NULL,
     amount DECIMAL(18, 2) NOT NULL,
     previous_balance DECIMAL(18, 2) NOT NULL,
     new_balance DECIMAL(18, 2) NOT NULL,
     operation_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     description VARCHAR(255),
     transaction_reference VARCHAR(100) UNIQUE NOT NULL,
     origin_id BIGINT NOT NULL,
     status VARCHAR(20) DEFAULT 'REGISTERED' NOT NULL,
     reversed_at TIMESTAMP WITHOUT TIME ZONE,
     reversed_by VARCHAR(50),
     reversal_reference VARCHAR(100),
     created_by VARCHAR(50) NOT NULL,
     terminal_ip VARCHAR(45),
     config_id BIGINT,
    -- CONSTRAINTS CORREGIDAS
     CONSTRAINT fk_log_mov_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    -- Aquí estaba el error, ahora apunta a catalog_detail
     CONSTRAINT fk_log_mov_type FOREIGN KEY (transaction_type_id) REFERENCES catalog_detail(id),
     CONSTRAINT fk_log_mov_origin FOREIGN KEY (origin_id) REFERENCES catalog_detail(id),
     CONSTRAINT ck_status_transaction CHECK (status IN ('REGISTERED', 'REVERSED')),
     CONSTRAINT fk_log_mov_config FOREIGN KEY (config_id) REFERENCES tr_configs(config_id)
);

-- Índice para optimizar la pantalla de reversos (filtro por cuenta y estado)
CREATE INDEX idx_log_mov_status_search ON log_mov(account_id, status);
-- Índice para reportes por canal (útil para el EoD)
CREATE INDEX idx_log_mov_origin ON log_mov(origin_id);

CREATE TABLE hist_mov (
    hist_id BIGSERIAL PRIMARY KEY,
    id_original BIGINT, -- El ID que tenía en log_mov
    account_id BIGINT NOT NULL,
    transaction_type_id BIGINT,
    origin_id BIGINT,
    config_id BIGINT,
    business_date DATE NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    previous_balance DECIMAL(19, 2),
    new_balance DECIMAL(19, 2),
    description TEXT,
    transaction_reference VARCHAR(100),
    status VARCHAR(20),
    created_at TIMESTAMP,
    created_by VARCHAR(50),
    terminal_ip VARCHAR(50),
    reversed_at TIMESTAMP,
    reversed_by VARCHAR(50),
    reversal_reference VARCHAR(100),
    -- Campo extra para control del Job
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices cruciales para que la consulta de movimientos sea rápida
CREATE INDEX idx_hist_account_date ON hist_mov(account_id, business_date);
CREATE INDEX idx_hist_reference ON hist_mov(transaction_reference);

CREATE TABLE eod_process_log (
     id BIGSERIAL PRIMARY KEY,
     job_execution_id BIGINT,
     start_time TIMESTAMP NOT NULL,
     end_time TIMESTAMP,
     status VARCHAR(20), -- COMPLETED, FAILED
     total_accounts_processed INT DEFAULT 0,
     error_message TEXT
);
