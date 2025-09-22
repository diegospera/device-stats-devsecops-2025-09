-- Safra Bank Device Statistics Database Schema
-- PostgreSQL Database initialization script
-- 
-- DevSecOps Security Features:
-- 1. Unique constraints to prevent duplicate registrations
-- 2. Indexes for performance optimization
-- 3. Data type constraints for input validation
-- 4. Audit trail with timestamps

-- Create database (if running manually)
-- CREATE DATABASE safra_device_stats;

-- Use the database
-- \c safra_device_stats;

-- Create device_registrations table
CREATE TABLE IF NOT EXISTS device_registrations (
    id BIGSERIAL PRIMARY KEY,
    user_key VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL CHECK (device_type IN ('iOS', 'Android', 'Watch', 'TV')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_device_type ON device_registrations(device_type);
CREATE INDEX IF NOT EXISTS idx_user_key ON device_registrations(user_key);
CREATE INDEX IF NOT EXISTS idx_created_at ON device_registrations(created_at);

-- Create unique constraint to prevent duplicate user-device combinations
ALTER TABLE device_registrations 
ADD CONSTRAINT uk_user_device UNIQUE (user_key, device_type);

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_device_registrations_updated_at 
    BEFORE UPDATE ON device_registrations 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data for testing (optional)
INSERT INTO device_registrations (user_key, device_type) VALUES
    ('user1', 'iOS'),
    ('user1', 'Watch'),
    ('user2', 'Android'),
    ('user3', 'iOS'),
    ('user4', 'TV'),
    ('user5', 'Android'),
    ('user6', 'iOS')
ON CONFLICT (user_key, device_type) DO NOTHING;

-- Grant permissions for application user (to be created in deployment)
-- CREATE USER safra_app WITH PASSWORD 'secure_password';
-- GRANT SELECT, INSERT, UPDATE ON device_registrations TO safra_app;
-- GRANT USAGE, SELECT ON SEQUENCE device_registrations_id_seq TO safra_app;