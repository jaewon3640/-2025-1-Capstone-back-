-- 사용자 테이블 (enum 기반 role 사용)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(255) UNIQUE
);

-- 사용자 데이터 삽입
INSERT INTO users (name, role, phone, email) VALUES
('김영희', '피보호자', '010-1234-5678', 'elder@example.com'),
('이수민', '보호자', '010-8765-4321', 'caregiver@example.com'),
('송씨', '피보호자', '010-1214-5678', 'eer@example.com'),
('이씨', '보호자', '010-8345-4321', 'carver@example.com');