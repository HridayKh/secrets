-- Secrets Manager schema
-- Generated: 2025-10-19
-- Notes:
--  - This schema creates the core tables: users, projects, environments, secrets, api_keys, audit_logs.
--  - Secret values should be encrypted by the application before INSERT/UPDATE, or use database-level
--    encryption functions (AES_ENCRYPT/AES_DECRYPT) with a secure key that is NOT checked into source.
--  - If using MySQL/MariaDB JSON column types are available; details column can be JSON or TEXT depending on
--    your database version. This file uses JSON for modern MySQL (>=5.7). Change to TEXT if needed.

CREATE DATABASE IF NOT EXISTS `secrets_manager` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `secrets_manager`;

-- -----------------------------------------------------
-- Table `users` - frontend/admin users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL UNIQUE,
  `password_hash` VARCHAR(128) NOT NULL,
  `is_admin` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `projects`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `projects` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `slug` VARCHAR(64) NOT NULL UNIQUE,
  `name` VARCHAR(128) NOT NULL,
  `description` TEXT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `environments`
-- Each project can have multiple environments (dev, prod, etc.)
-- Composite unique (project_id, name)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `environments` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `uq_environment_project_name` UNIQUE (`project_id`, `name`),
  CONSTRAINT `fk_environments_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `secrets`
-- Stores encrypted secret values per environment
-- Unique per environment for a secret key
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `secrets` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `environment_id` INT NOT NULL,
  `key` VARCHAR(128) NOT NULL,
  `value` MEDIUMTEXT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `uq_secret_env_key` UNIQUE (`environment_id`, `key`),
  CONSTRAINT `fk_secrets_environment` FOREIGN KEY (`environment_id`) REFERENCES `environments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX `idx_secrets_environment` (`environment_id`),
  INDEX `idx_secrets_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `api_keys`
-- API keys used by backend projects to fetch secrets
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `api_keys` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `key` VARCHAR(128) NOT NULL UNIQUE,
  `label` VARCHAR(64) NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_used` DATETIME NULL,
  `active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_api_keys_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  INDEX `idx_api_keys_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `audit_logs`
-- Tracks actions for security and debugging
-- `user_id` and `project_id` are nullable because API actions may be unauthenticated user-wise.
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NULL,
  `project_id` INT NULL,
  `action` VARCHAR(64) NOT NULL,
  `details` JSON NULL,
  `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_audit_logs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_audit_logs_project` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  INDEX `idx_audit_timestamp` (`timestamp`),
  INDEX `idx_audit_user` (`user_id`),
  INDEX `idx_audit_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Example: application-level encryption recommendation
-- -----------------------------------------------------
-- Recommended approach: encrypt secrets in the application with a secure master key (e.g. AES-256-GCM)
-- and store only the ciphertext in `secrets.value`. Keep the master key out-of-band (key vault or env var
-- on the server). This avoids exposing plaintext to DB backups or DB admins.

-- Optional: If you want to use MySQL's AES functions, here's an example pattern (do NOT store the KEY in source):
-- SET @app_master_key = 'replace_with_strong_key_from_env';
-- INSERT INTO `secrets` (`environment_id`, `key`, `value`) VALUES (1, 'DB_PASSWORD', TO_BASE64(AES_ENCRYPT('plain-value', @app_master_key)));
-- SELECT CAST(AES_DECRYPT(FROM_BASE64(value), @app_master_key) AS CHAR) AS plaintext FROM `secrets` WHERE id = 1;

-- End of schema
