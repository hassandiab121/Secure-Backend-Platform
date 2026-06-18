-- Create the main identity table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BINARY(16) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create the isolated password credential table
CREATE TABLE IF NOT EXISTS `password` (
    `user_id` BINARY(16) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_password_user`
        FOREIGN KEY (`user_id`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create the isolated audit table with mandatory last_login_at
CREATE TABLE IF NOT EXISTS `audit` (
    `user_id` BINARY(16) NOT NULL,
    `failed_login_attempts` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `last_login_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_audit_user`
        FOREIGN KEY (`user_id`)
        REFERENCES `user` (`id`)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;