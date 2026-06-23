-- H2 兼容建表脚本（测试用）
CREATE TABLE IF NOT EXISTS books (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    isbn            VARCHAR(255) NOT NULL,
    published_date  DATE DEFAULT NULL,
    description     VARCHAR(2000) DEFAULT NULL,
    available       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_author ON books(author);
CREATE INDEX IF NOT EXISTS idx_available ON books(available);