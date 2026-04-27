USE lostandfound;

-- ---------------------------------------------------------------------------
-- Feature A migration patch
-- 1) Add analytics-friendly indexes
-- 2) Insert expanded seed data for item/claim/user
-- ---------------------------------------------------------------------------

-- Create index helper block
SET @db := DATABASE();

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_type'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_type ON item(`type`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_category'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_category ON item(`category`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_location'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_location ON item(`location`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_lost_date'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_lost_date ON item(`lostDate`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_found_date'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_found_date ON item(`foundDate`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'item' AND index_name = 'idx_item_status'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_item_status ON item(`status`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = @db AND table_name = 'claim' AND index_name = 'idx_claim_status'
);
SET @sql := IF(@idx_exists = 0, 'CREATE INDEX idx_claim_status ON claim(`status`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Expanded seed users
INSERT INTO `user` (`studentId`, `username`, `password`, `email`, `phone`, `role`)
VALUES
('126000111', 'Lily Sun', '123456', '126000111@link.cuhk.edu.cn', '13900010001', 'student'),
('126000112', 'Ethan He', '123456', '126000112@link.cuhk.edu.cn', '13900010002', 'student'),
('126000113', 'Mia Zhao', '123456', '126000113@link.cuhk.edu.cn', '13900010003', 'student')
ON DUPLICATE KEY UPDATE
`username` = VALUES(`username`),
`password` = VALUES(`password`),
`email` = VALUES(`email`),
`phone` = VALUES(`phone`),
`role` = VALUES(`role`);

-- Expanded seed items
INSERT INTO `item`
(`id`, `userId`, `type`, `title`, `description`, `category`, `location`, `lostDate`, `foundDate`, `imageUrl`, `status`, `createdAt`, `updatedAt`)
VALUES
(1001, 1, 'lost',  '丢失校园卡',     '蓝色卡套，卡号尾号 31',           '证件',   '图书馆一楼', '2026-04-24', NULL, NULL, 'open',    NOW(), NOW()),
(1002, 2, 'found', '捡到保温杯',     '银色膳魔师保温杯，杯底有贴纸',       '日用品', '教学楼C栋', NULL, '2026-04-25', NULL, 'open',    NOW(), NOW()),
(1003, 3, 'lost',  '丢失机械键盘',   '白色 68 键机械键盘',                '电子产品', '教学楼B栋', '2026-04-26', NULL, NULL, 'open',    NOW(), NOW()),
(1004, 1, 'found', '捡到运动手环',   '黑色小米手环，屏幕轻微划痕',          '电子产品', '操场看台', NULL, '2026-04-26', NULL, 'claimed', NOW(), NOW()),
(1005, 2, 'found', '捡到蓝牙耳机盒', '白色耳机盒，无耳机本体',             '电子产品', '饭堂门口', NULL, '2026-04-27', NULL, 'open',    NOW(), NOW())
ON DUPLICATE KEY UPDATE
`userId` = VALUES(`userId`),
`type` = VALUES(`type`),
`title` = VALUES(`title`),
`description` = VALUES(`description`),
`category` = VALUES(`category`),
`location` = VALUES(`location`),
`lostDate` = VALUES(`lostDate`),
`foundDate` = VALUES(`foundDate`),
`imageUrl` = VALUES(`imageUrl`),
`status` = VALUES(`status`),
`updatedAt` = NOW();

-- Expanded seed claims
INSERT INTO `claim`
(`id`, `itemId`, `userId`, `message`, `status`, `createdAt`, `updatedAt`)
VALUES
(2001, 1004, 2, '这是我的手环，表带内侧有我名字缩写。', 'approved', NOW(), NOW()),
(2002, 1001, 3, '校园卡可能是我的，卡套颜色和位置都吻合。', 'pending',  NOW(), NOW()),
(2003, 1005, 1, '耳机盒是我上周在饭堂丢的。', 'pending', NOW(), NOW())
ON DUPLICATE KEY UPDATE
`itemId` = VALUES(`itemId`),
`userId` = VALUES(`userId`),
`message` = VALUES(`message`),
`status` = VALUES(`status`),
`updatedAt` = NOW();
