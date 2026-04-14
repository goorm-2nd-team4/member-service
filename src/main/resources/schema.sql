CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id),
    CONSTRAINT uk_members_email UNIQUE (email),
    CONSTRAINT chk_members_role CHECK (role IN ('USER', 'ADMIN'))
);
