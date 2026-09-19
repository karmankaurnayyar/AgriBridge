-- ============================================================================
-- AgriBridge - Database Schema (MySQL 8+)
-- Individual project — Karman Kaur Nayyar, Junior Software Developer Intern
--
-- Week 2 status: this schema defines the FULL planned data model so the
-- architecture can be reviewed end-to-end. The backend (Week 2) currently
-- reads/writes only `users` and `farms` via JPA/Hibernate. `crops` and
-- `harvest_lots` have matching JPA entities with no service/controller yet
-- (planned Week 3). `buyer_requests` and `matches` are schema-only for now
-- (planned Week 4). See docs/roadmap.md.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS agribridge
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE agribridge;

-- ----------------------------------------------------------------------------
-- users
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(120)  NOT NULL,
    email           VARCHAR(150)  NOT NULL,
    password_hash   VARCHAR(255)  NOT NULL,
    role            ENUM('FARMER', 'BUYER', 'ADMIN') NOT NULL,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- farms  (Week 2 — implemented)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS farms (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id    BIGINT NOT NULL,
    farm_name   VARCHAR(150) NOT NULL,
    location    VARCHAR(200) NOT NULL,
    land_area   DECIMAL(10,2) NOT NULL,
    soil_type   VARCHAR(100),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_farms_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_farms_land_area CHECK (land_area > 0),
    INDEX idx_farms_owner_id (owner_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- crops  (schema ready — service/controller planned Week 3)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS crops (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    farm_id                 BIGINT NOT NULL,
    crop_name               VARCHAR(100) NOT NULL,
    crop_type               VARCHAR(100),
    sowing_date             DATE,
    expected_harvest_date   DATE,
    status                  ENUM('PLANNED','GROWING','HARVEST_READY','COMPLETED') NOT NULL DEFAULT 'PLANNED',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_crops_farm FOREIGN KEY (farm_id) REFERENCES farms(id) ON DELETE CASCADE,
    CONSTRAINT chk_crops_dates CHECK (expected_harvest_date IS NULL OR sowing_date IS NULL OR expected_harvest_date >= sowing_date),
    INDEX idx_crops_farm_id (farm_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- harvest_lots  (schema ready — service/controller planned Week 3)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_lots (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_code                VARCHAR(40) NOT NULL,
    crop_id                 BIGINT NOT NULL,
    quantity                DECIMAL(10,2) NOT NULL,
    unit                    VARCHAR(20) NOT NULL DEFAULT 'kg',
    harvest_date            DATE,
    quality_grade           VARCHAR(20),
    expected_price          DECIMAL(10,2),
    availability_status     ENUM('AVAILABLE','RESERVED','SOLD') NOT NULL DEFAULT 'AVAILABLE',
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_harvest_lots_code UNIQUE (lot_code),
    CONSTRAINT fk_harvest_lots_crop FOREIGN KEY (crop_id) REFERENCES crops(id) ON DELETE CASCADE,
    CONSTRAINT chk_harvest_lots_quantity CHECK (quantity > 0),
    INDEX idx_harvest_lots_crop_id (crop_id),
    INDEX idx_harvest_lots_status (availability_status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- buyer_requests  (schema only — planned Week 4)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buyer_requests (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id            BIGINT NOT NULL,
    commodity           VARCHAR(100) NOT NULL,
    quantity            DECIMAL(10,2) NOT NULL,
    min_price           DECIMAL(10,2),
    max_price           DECIMAL(10,2),
    location            VARCHAR(200),
    required_by_date    DATE,
    status              ENUM('OPEN','MATCHED','CANCELLED') NOT NULL DEFAULT 'OPEN',
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_buyer_requests_buyer FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_buyer_requests_quantity CHECK (quantity > 0),
    INDEX idx_buyer_requests_buyer_id (buyer_id),
    INDEX idx_buyer_requests_status (status)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- matches  (schema only — planned Week 4, alongside the matching engine)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS matches (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    lot_id          BIGINT NOT NULL,
    request_id      BIGINT NOT NULL,
    status          ENUM('SUGGESTED','CONFIRMED','REJECTED') NOT NULL DEFAULT 'SUGGESTED',
    confirmed_at    DATETIME NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_matches_lot FOREIGN KEY (lot_id) REFERENCES harvest_lots(id) ON DELETE CASCADE,
    CONSTRAINT fk_matches_request FOREIGN KEY (request_id) REFERENCES buyer_requests(id) ON DELETE CASCADE,
    CONSTRAINT uq_matches_lot_request UNIQUE (lot_id, request_id),
    INDEX idx_matches_status (status)
) ENGINE=InnoDB;
