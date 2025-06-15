-- V1__create_users_roles_privileges.sql (MySQL version)

-- 1. Users table
CREATE TABLE IF NOT EXISTS users (
  id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(100) UNIQUE NOT NULL,
  password   VARCHAR(255) NOT NULL,
  first_name VARCHAR(50),
  last_name  VARCHAR(50),
  phone      VARCHAR(20),
  enabled    BOOLEAN     DEFAULT TRUE
);

-- 2. Roles table
CREATE TABLE IF NOT EXISTS roles (
  id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(50) UNIQUE NOT NULL
);

-- 3. Privileges table
CREATE TABLE IF NOT EXISTS privileges (
  id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100) UNIQUE NOT NULL
);

-- 4. users_roles join
CREATE TABLE IF NOT EXISTS users_roles (
  user_id    BIGINT NOT NULL,
  role_id    BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 5. roles_privileges join
CREATE TABLE IF NOT EXISTS roles_privileges (
  role_id      BIGINT NOT NULL,
  privilege_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, privilege_id),
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
  FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
);
