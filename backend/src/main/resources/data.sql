-- =============================================
-- Users Table (20 records, 5 attributes)
-- =============================================
INSERT INTO users (email, password, is_verified, registration_date) VALUES
                                                                        ('john.doe@univ.ca', 'SecurePass123!', 1, '2023-01-15'),
                                                                        ('emma.wilson@univ.ca', 'EmmaPass!2023', 1, '2023-02-10'),
                                                                        ('liam.smith@univ.ca', 'LiamTech$567', 1, '2023-03-22'),
                                                                        ('olivia.brown@univ.ca', 'OliviaLaptop#99', 1, '2023-04-05'),
                                                                        ('noah.jones@univ.ca', 'NoahUniv_2023', 1, '2023-05-18'),
                                                                        ('ava.garcia@univ.ca', 'AvaG@7890', 1, '2023-06-01'),
                                                                        ('sophia.miller@univ.ca', 'SophiaCMPT!', 1, '2023-07-14'),
                                                                        ('jackson.davis@univ.ca', 'Jackson2023$', 1, '2023-08-27'),
                                                                        ('mia.rodriguez@univ.ca', 'MiaMechEng1', 1, '2023-09-09'),
                                                                        ('lucas.martinez@univ.ca', 'Lucas@ECE2023', 1, '2023-10-12'),
                                                                        ('seller1@univ.ca', 'SellerPass!123', 1, '2023-01-10'),
                                                                        ('seller2@univ.ca', 'SellerPass!456', 1, '2023-02-15'),
                                                                        ('seller3@univ.ca', 'SellerPass!789', 1, '2023-03-20'),
                                                                        ('seller4@univ.ca', 'SellerPass!101', 1, '2023-04-25'),
                                                                        ('seller5@univ.ca', 'SellerPass!112', 1, '2023-05-30'),
                                                                        ('seller6@univ.ca', 'SellerPass!131', 1, '2023-06-05'),
                                                                        ('seller7@univ.ca', 'SellerPass!415', 1, '2023-07-16'),
                                                                        ('seller8@univ.ca', 'SellerPass!161', 1, '2023-08-19'),
                                                                        ('seller9@univ.ca', 'SellerPass!718', 1, '2023-09-22'),
                                                                        ('seller10@univ.ca', 'SellerPass!192', 1, '2023-10-25');

-- =============================================
-- Buyers Table (10 records, 4 attributes)
-- =============================================
INSERT INTO buyers (user_id, major, preferred_brand, budget) VALUES
                                                                 (1, 'Computer Science', 'Apple', 2000.00),
                                                                 (2, 'Electrical Engineering', 'Dell', 1500.00),
                                                                 (3, 'Mechanical Engineering', 'Lenovo', 1800.00),
                                                                 (4, 'Data Science', 'Microsoft', 2200.00),
                                                                 (5, 'Software Engineering', 'HP', 1700.00),
                                                                 (6, 'Cybersecurity', 'Asus', 1900.00),
                                                                 (7, 'AI Engineering', 'Razer', 2500.00),
                                                                 (8, 'Biomedical Engineering', 'Acer', 1600.00),
                                                                 (9, 'Civil Engineering', 'MSI', 2100.00),
                                                                 (10, 'Game Development', 'Alienware', 3000.00);

-- =============================================
-- Sellers Table (10 records, 4 attributes)
-- =============================================
INSERT INTO sellers (user_id, seller_rating, is_admin_verified, university_id) VALUES
                                                                                   (11, 4.8, 1, 'UNIV_SELLER_001'),
                                                                                   (12, 4.5, 1, 'UNIV_SELLER_002'),
                                                                                   (13, 4.9, 1, 'UNIV_SELLER_003'),
                                                                                   (14, 4.2, 1, 'UNIV_SELLER_004'),
                                                                                   (15, 4.7, 1, 'UNIV_SELLER_005'),
                                                                                   (16, 4.4, 0, 'UNIV_SELLER_006'),
                                                                                   (17, 4.6, 1, 'UNIV_SELLER_007'),
                                                                                   (18, 4.3, 0, 'UNIV_SELLER_008'),
                                                                                   (19, 4.1, 1, 'UNIV_SELLER_009'),
                                                                                   (20, 4.0, 1, 'UNIV_SELLER_010');

-- =============================================
-- Laptops Table (30 records, 9 attributes)
-- =============================================
INSERT INTO laptops (brand, model, price, ram, storage, cpu, gpu, seller_id, is_verified) VALUES
-- Seller 11
('Apple', 'MacBook Pro 16"', 2499.99, '16GB', '1TB SSD', 'M2 Pro', 'M2 Pro 19-core', 11, 1),
('Apple', 'MacBook Air M2', 1299.99, '8GB', '256GB SSD', 'M2', 'Integrated 10-core', 11, 1),

-- Seller 12
('Dell', 'XPS 15', 1899.99, '32GB', '1TB SSD', 'i7-12700H', 'RTX 3050 Ti', 12, 1),
('Dell', 'Inspiron 16', 899.99, '16GB', '512GB SSD', 'i5-1240P', 'Iris Xe', 12, 1),

-- Seller 13 (3 laptops)
('Lenovo', 'ThinkPad X1 Carbon', 1699.99, '16GB', '1TB SSD', 'i7-1260P', 'Iris Xe', 13, 1),
('Lenovo', 'Legion 5 Pro', 1499.99, '32GB', '1TB SSD', 'Ryzen 7 6800H', 'RTX 3070 Ti', 13, 1),
('Lenovo', 'Yoga 9i', 1399.99, '16GB', '512GB SSD', 'i7-1260P', 'Iris Xe', 13, 1),

-- Seller 14 (3 laptops)
('Microsoft', 'Surface Laptop 5', 1299.99, '16GB', '512GB SSD', 'i5-1235U', 'Iris Xe', 14, 1),
('Microsoft', 'Surface Pro 9', 1599.99, '16GB', '1TB SSD', 'i7-1255U', 'Iris Xe', 14, 1),
('Microsoft', 'Surface Studio 2', 3499.99, '32GB', '2TB SSD', 'i7-12700H', 'RTX 3060', 14, 1),

-- Seller 15 (3 laptops)
('HP', 'Spectre x360', 1399.99, '16GB', '1TB SSD', 'i7-1260P', 'Iris Xe', 15, 1),
('HP', 'Omen 17', 1999.99, '32GB', '1TB SSD', 'i9-12900H', 'RTX 3080 Ti', 15, 1),
('HP', 'Envy 13', 1099.99, '8GB', '256GB SSD', 'i5-1235U', 'Iris Xe', 15, 1),

-- Seller 16 (3 laptops)
('Asus', 'ROG Zephyrus G14', 1499.99, '16GB', '1TB SSD', 'Ryzen 9 6900HS', 'RX 6800S', 16, 0),
('Asus', 'ZenBook 14X', 1299.99, '16GB', '512GB SSD', 'i7-1260P', 'Iris Xe', 16, 0),
('Asus', 'TUF Dash F15', 1199.99, '16GB', '512GB SSD', 'i7-12650H', 'RTX 3060', 16, 0),

-- Seller 17 (3 laptops)
('Razer', 'Blade 15', 2499.99, '32GB', '2TB SSD', 'i9-12900H', 'RTX 3080 Ti', 17, 1),
('Razer', 'Blade 14', 1999.99, '16GB', '1TB SSD', 'Ryzen 9 6900HX', 'RX 6800M', 17, 1),
('Razer', 'Book 13', 1599.99, '16GB', '512GB SSD', 'i7-1260P', 'Iris Xe', 17, 1),

-- Seller 18 (3 laptops)
('Acer', 'Predator Helios 300', 1499.99, '16GB', '1TB SSD', 'i7-12700H', 'RTX 3070 Ti', 18, 0),
('Acer', 'Swift X', 1099.99, '16GB', '512GB SSD', 'Ryzen 7 5800U', 'RTX 3050 Ti', 18, 0),
('Acer', 'Spin 5', 999.99, '8GB', '256GB SSD', 'i5-1235U', 'Iris Xe', 18, 0),

-- Seller 19 (3 laptops)
('MSI', 'Stealth GS77', 2299.99, '32GB', '2TB SSD', 'i9-12900H', 'RTX 3080 Ti', 19, 1),
('MSI', 'Katana GF76', 1599.99, '16GB', '1TB SSD', 'i7-12700H', 'RTX 3070', 19, 1),
('MSI', 'Prestige 14', 1399.99, '16GB', '1TB SSD', 'i7-1260P', 'Iris Xe', 19, 1),

-- Seller 20 (3 laptops)
('Alienware', 'x17 R2', 2999.99, '32GB', '2TB SSD', 'i9-12900HK', 'RTX 3080 Ti', 20, 1),
('Alienware', 'x17 R2', 1999.99, '16GB', '1TB SSD', 'i7-12700H', 'RTX 3070 Ti', 20, 1),
('Alienware', 'x17 R2', 1799.99, '16GB', '1TB SSD', 'i7-1260P', 'RTX 3060 Ti', 20, 1),
('Dell', 'XPS 14', 1799.99, '16GB', '1TB SSD', 'i7-1260P', 'RTX 3060 Ti', 20, 1),
('Dell', 'XPS 14', 1399.99, '8GB', '512GB SSD', 'i7-1260P', 'RTX 3060 Ti', 20, 1);


-- =============================================
-- Wishlist Table (15 records, 4 attributes)
-- =============================================
INSERT INTO wishlist (buyer_id, laptop_id, target_price) VALUES
-- Buyer 1 (CS Student)
(1, 1, 2200.00),   -- MacBook Pro 16"
(1, 3, 1700.00),   -- Dell XPS 15

-- Buyer 2 (EE Student)
(2, 5, 1500.00),   -- ThinkPad X1 Carbon
(2, 7, 1200.00),   -- Surface Laptop 5

-- Buyer 3 (MechEng)
(3, 9, 3200.00),   -- Surface Studio 2
(3, 11, 1800.00),  -- HP Omen 17

-- Buyer 4 (Data Science)
(4, 13, 1300.00),  -- ROG Zephyrus G14
(4, 15, 1000.00),  -- Razer Book 13

-- Buyer 5 (Software Eng)
(5, 17, 2000.00),  -- Blade 15
(5, 19, 1300.00),  -- MSI Prestige 14

-- Buyer 6 (Cybersecurity)
(6, 21, 2700.00),  -- Alienware x17 R2
(6, 23, 1600.00),  -- Alienware x14

-- Buyer 7 (AI Eng)
(7, 2, 1100.00),   -- MacBook Air M2
(7, 4, 800.00),    -- Dell Inspiron 16

-- Buyer 8 (Biomedical Eng)
(8, 6, 1300.00),   -- Legion 5 Pro

-- Buyer 9 (Civil Eng)
(9, 8, 1400.00);   -- Surface Pro 9

-- =============================================
-- Recommendation Rules (5 records, 4 attributes)
-- =============================================
INSERT INTO recommendation_rules (major, required_specs, min_budget, max_budget) VALUES
                                                                                     ('Computer Science', 'RAM >= 16GB, Storage >= 512GB SSD', 1500.00, 3000.00),
                                                                                     ('Electrical Engineering', 'CPU: Intel i7/Ryzen 7 or better', 1200.00, 2500.00),
                                                                                     ('Game Development', 'GPU: RTX 3060 or better', 2000.00, 4000.00),
                                                                                     ('Data Science', 'Storage >= 1TB SSD, RAM >= 32GB', 1800.00, 3500.00),
                                                                                     ('AI Engineering', 'GPU: RTX 3080/RX 6800M or better', 2500.00, 5000.00);