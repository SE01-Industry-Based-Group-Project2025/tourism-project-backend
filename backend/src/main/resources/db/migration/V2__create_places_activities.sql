-- V2__create_places_activities.sql

-- 1. Places table
CREATE TABLE IF NOT EXISTS places (
  id     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(100) NOT NULL,
  region VARCHAR(100) NOT NULL
);

-- 2. Activities table
CREATE TABLE IF NOT EXISTS activities (
  id     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name   VARCHAR(100) NOT NULL,
  region VARCHAR(100) NOT NULL
);
