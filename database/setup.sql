CREATE DATABASE IF NOT EXISTS campuscart_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create a local application user separately with a strong password.
-- Grant only the permissions required for the local CampusCart database.
-- Keep the chosen password in the root .env file; never commit it.
