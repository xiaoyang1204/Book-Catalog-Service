-- ============================================================
-- Book Catalog Service - 创建 books 表
-- ============================================================

CREATE TABLE IF NOT EXISTS `books` (
    `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键ID',
    `title`           VARCHAR(255)    NOT NULL                 COMMENT '书名',
    `author`          VARCHAR(255)    NOT NULL                 COMMENT '作者',
    `isbn`            VARCHAR(255)    NOT NULL                 COMMENT 'ISBN编号',
    `published_date`  DATE            DEFAULT NULL             COMMENT '出版日期',
    `description`     VARCHAR(2000)   DEFAULT NULL             COMMENT '书籍描述',
    `available`       TINYINT(1)      NOT NULL DEFAULT 1       COMMENT '是否可借阅（1:可借阅, 0:已借出）',
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_available` (`available`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍信息表';