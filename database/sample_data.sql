-- ============================================================================
-- AgriBridge - Sample Data
--
-- All sample users share the password: Password123
-- (BCrypt hash below was generated with a cost factor of 10 and verified to
-- be a well-formed $2b$ hash; it is not a real credential for any real
-- account and is safe to commit.)
-- ============================================================================

USE agribridge;

INSERT INTO users (name, email, password_hash, role) VALUES
('Ravi Kumar',      'ravi.kumar@example.com',      '$2b$10$Vowq8AVzdU4cZkjj50ugPum2mOI1sgh1zjh7HHfEeCcrWKzXsfsdG', 'FARMER'),
('Simran Gill',     'simran.gill@example.com',     '$2b$10$Vowq8AVzdU4cZkjj50ugPum2mOI1sgh1zjh7HHfEeCcrWKzXsfsdG', 'FARMER'),
('Anil Traders Co', 'anil.traders@example.com',    '$2b$10$Vowq8AVzdU4cZkjj50ugPum2mOI1sgh1zjh7HHfEeCcrWKzXsfsdG', 'BUYER'),
('Platform Admin',  'admin@agribridge.example.com','$2b$10$Vowq8AVzdU4cZkjj50ugPum2mOI1sgh1zjh7HHfEeCcrWKzXsfsdG', 'ADMIN');

-- Farms (Week 2 — implemented and exercised via the Farm Management API)
INSERT INTO farms (owner_id, farm_name, location, land_area, soil_type) VALUES
(1, 'Green Valley Farm',   'Village Rampur, Hoshiarpur, Punjab', 2.50, 'Loamy'),
(1, 'Riverside Plot',      'Beas Riverside, Hoshiarpur, Punjab', 1.20, 'Alluvial'),
(2, 'Sunrise Fields',      'Sangrur, Punjab',                    3.80, 'Clay Loam');

-- Crops (schema-ready sample data for Week 3 development/testing)
INSERT INTO crops (farm_id, crop_name, crop_type, sowing_date, expected_harvest_date, status) VALUES
(1, 'Wheat',  'Cereal',     '2026-11-01', '2027-03-15', 'PLANNED'),
(1, 'Tomato', 'Vegetable',  '2026-09-01', '2026-12-01', 'GROWING'),
(3, 'Basmati Rice', 'Cereal', '2026-06-15', '2026-10-30', 'HARVEST_READY');

-- Harvest lots (schema-ready sample data for Week 3 development/testing)
INSERT INTO harvest_lots (lot_code, crop_id, quantity, unit, harvest_date, quality_grade, expected_price, availability_status) VALUES
('LOT-2026-000001', 3, 620.00, 'kg', '2026-10-28', 'Grade A', 42.50, 'AVAILABLE');

-- Buyer requests (schema-ready sample data for Week 4 development/testing)
INSERT INTO buyer_requests (buyer_id, commodity, quantity, min_price, max_price, location, required_by_date, status) VALUES
(3, 'Basmati Rice', 500.00, 38.00, 45.00, 'Ludhiana, Punjab', '2026-11-10', 'OPEN');
