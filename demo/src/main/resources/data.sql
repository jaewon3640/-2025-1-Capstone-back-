-- 사용자 테이블 (enum 기반 role 사용)
INSERT INTO users (id, name, role, phone, email) VALUES
(1, '김영희', '피보호자', '010-1234-5678', 'elder@example.com'),
(2, '이수민', '보호자', '010-8765-4321', 'caregiver@example.com'),
(3, '송씨', '피보호자', '010-1214-5678', 'eer@example.com'),
(4, '이씨', '보호자', '010-8345-4321', 'carver@example.com');


-- 보호자-피보호자 관계
INSERT INTO care_relation (id, caregiver_id, protected_user_id) VALUES
(1, 2, 1);

-- 일정
INSERT INTO schedules (id, title, description, scheduled_at, completed, protected_user_id) VALUES
(1, '병원 방문', '내과 진료', '2025-04-09T09:00:00', FALSE, 1),
(2, '약 복용', '고혈압 약 복용', '2025-04-09T11:00:00', FALSE, 1);

-- 건강 기록
INSERT INTO health_status (id, type, measurement, recorded_at, protected_user_id) VALUES
(1, '혈압', '120/80', '2025-04-09T09:00:00', 1),
(2, '수면', '7시간', '2025-04-09T09:00:00', 1);

-- 위치 정보
INSERT INTO locations (id, latitude, longitude, timestamp, status, protected_user_id) VALUES
(1, 37.5665, 126.9780, '2025-04-09T09:00:00', '정상', 1);

-- 긴급 알림
INSERT INTO emergency_alerts (id, triggered_at, message, resolved, protected_user_id) VALUES
(1, '2025-04-09T09:00:00', '넘어졌어요', FALSE, 1);