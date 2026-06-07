-- File: schema.sql
-- ════════════════════════════════════════════════════════════
-- STREAMFLIX Database Schema — MySQL 8
-- ════════════════════════════════════════════════════════════

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ────────────────────────────────────────────────────────────
-- USERS TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE users (
    user_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    profile_image_url VARCHAR(512) DEFAULT NULL,
    role             ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- VIDEOS TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE videos (
    video_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    genre            ENUM('ACTION','COMEDY','HORROR','DRAMA','SCI_FI','DOCUMENTARY','ENTERTAINMENT') NOT NULL,
    duration_seconds INT NOT NULL,
    release_year     SMALLINT NOT NULL,
    rating           DECIMAL(3,1) DEFAULT NULL,
    age_rating       ENUM('G','PG','PG13','R','NC17') NOT NULL,
    thumbnail_url    VARCHAR(512) DEFAULT NULL,
    video_url        VARCHAR(512) DEFAULT NULL,
    type             ENUM('MOVIE','TV_SHOW','DOCUMENTARY') NOT NULL,
    is_premium       BOOLEAN NOT NULL DEFAULT FALSE,
    created_by       BIGINT DEFAULT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_videos_genre (genre),
    INDEX idx_videos_title (title),
    INDEX idx_videos_type (type),
    INDEX idx_videos_release_year (release_year),
    INDEX idx_videos_is_premium (is_premium),
    INDEX idx_videos_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- WATCHLIST TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE watchlist (
    watchlist_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    video_id      BIGINT NOT NULL,
    added_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_video (user_id, video_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    INDEX idx_watchlist_user (user_id),
    INDEX idx_watchlist_video (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- WATCH HISTORY TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE watch_history (
    history_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    video_id         BIGINT NOT NULL,
    progress_seconds INT NOT NULL DEFAULT 0,
    completed        BOOLEAN NOT NULL DEFAULT FALSE,
    watch_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (video_id) REFERENCES videos(video_id) ON DELETE CASCADE,
    INDEX idx_history_user (user_id),
    INDEX idx_history_video (video_id),
    INDEX idx_history_watch_date (watch_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- SUBSCRIPTIONS TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE subscriptions (
    subscription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    plan            ENUM('BASIC','STANDARD','PREMIUM') NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          ENUM('ACTIVE','CANCELLED','EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_subscriptions_status (status),
    INDEX idx_subscriptions_end_date (end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- PAYMENTS TABLE
-- ────────────────────────────────────────────────────────────
CREATE TABLE payments (
    payment_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    currency        CHAR(3) NOT NULL DEFAULT 'USD',
    payment_method  ENUM('PAYPAL','STRIPE') NOT NULL,
    gateway_txn_id  VARCHAR(255) NOT NULL,
    payment_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status          ENUM('SUCCESS','FAILED','REFUNDED') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_payments_user (user_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_gateway_txn (gateway_txn_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ────────────────────────────────────────────────────────────
-- VIEW: Active Subscribers
-- ────────────────────────────────────────────────────────────
CREATE OR REPLACE VIEW vw_active_subscribers AS
SELECT
    u.user_id,
    u.name,
    u.email,
    s.plan,
    s.start_date,
    s.end_date,
    s.status
FROM users u
JOIN subscriptions s ON u.user_id = s.user_id
WHERE s.status = 'ACTIVE'
  AND s.end_date >= CURRENT_DATE;

-- ────────────────────────────────────────────────────────────
-- STORED PROCEDURE: Expire Subscriptions
-- ────────────────────────────────────────────────────────────
DELIMITER //
CREATE PROCEDURE sp_expire_subscriptions()
BEGIN
    UPDATE subscriptions
    SET status = 'EXPIRED',
        updated_at = CURRENT_TIMESTAMP
    WHERE status = 'ACTIVE'
      AND end_date < CURRENT_DATE;

    SELECT ROW_COUNT() AS expired_count;
END //
DELIMITER ;