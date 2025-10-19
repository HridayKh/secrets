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
  `actor_type` ENUM('frontend_admin', 'backend_api') NOT NULL,
  `actor_identifier` VARCHAR(128) NOT NULL,
  `project_id` INT NULL,
  `action` VARCHAR(64) NOT NULL,
  `details` JSON NULL,
  `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_audit_logs_project`
    FOREIGN KEY (`project_id`)
    REFERENCES `projects` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  INDEX `idx_audit_timestamp` (`timestamp`),
  INDEX `idx_audit_actor` (`actor_identifier`),
  INDEX `idx_audit_project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- End of schema
