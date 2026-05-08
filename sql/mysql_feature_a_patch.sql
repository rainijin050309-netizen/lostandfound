USE lostandfound;

-- ---------------------------------------------------------------------------
-- Feature A migration patch
-- 1) Add analytics-friendly indexes
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