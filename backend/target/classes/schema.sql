-- Disable foreign key checks
SET REFERENTIAL_INTEGRITY FALSE;

-- =============================================
-- Table: users (Base User Table)
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    registration_date DATE NOT NULL
);

-- =============================================
-- Table: buyers (Students looking for laptops)
-- =============================================
CREATE TABLE IF NOT EXISTS buyers (
    user_id BIGINT PRIMARY KEY,
    major VARCHAR(100) NOT NULL,
    preferred_brand VARCHAR(50),
    budget NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- Table: sellers (Verified laptop sellers)
-- =============================================
CREATE TABLE IF NOT EXISTS sellers (
    user_id BIGINT PRIMARY KEY,
    seller_rating NUMERIC(3, 2) DEFAULT 0.0,
    is_admin_verified BOOLEAN DEFAULT FALSE,
    university_id VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- Table: laptops (Marketplace listings)
-- =============================================
CREATE TABLE IF NOT EXISTS laptops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    ram VARCHAR(20) NOT NULL,
    storage VARCHAR(20) NOT NULL,
    cpu VARCHAR(50) NOT NULL,
    gpu VARCHAR(50),
    seller_id BIGINT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (seller_id) REFERENCES sellers(user_id) ON DELETE CASCADE
);

-- Indexes for faster searches
CREATE INDEX idx_brand ON laptops(brand);
CREATE INDEX idx_price ON laptops(price);

-- =============================================
-- Table: wishlist (Saved items for buyers)
-- =============================================
CREATE TABLE IF NOT EXISTS wishlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    laptop_id BIGINT NOT NULL,
    target_price NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (buyer_id) REFERENCES buyers(user_id) ON DELETE CASCADE,
    FOREIGN KEY (laptop_id) REFERENCES laptops(id) ON DELETE CASCADE,
    UNIQUE (buyer_id, laptop_id)
);

-- =============================================
-- Table: recommendation_rules (Major-specific requirements)
-- =============================================
CREATE TABLE IF NOT EXISTS recommendation_rules (
    major VARCHAR(100) PRIMARY KEY,
    required_specs TEXT NOT NULL,
    min_budget NUMERIC(10, 2) NOT NULL,
    max_budget NUMERIC(10, 2) NOT NULL
);

-- Re-enable foreign key checks
SET REFERENTIAL_INTEGRITY TRUE;
