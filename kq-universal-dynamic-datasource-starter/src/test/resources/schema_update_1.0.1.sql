ALTER TABLE dynamic_data_source MODIFY COLUMN jdbc_url varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
ALTER TABLE dynamic_data_source MODIFY COLUMN username varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL;
