CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    school_category VARCHAR(500)
);
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
CREATE INDEX idx_students_full_name ON students(full_name);
CREATE INDEX idx_students_school_category ON students(school_category);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
INSERT INTO roles (name) VALUES 
('ROLE_ADMIN'),
('ROLE_USER');

-- Thêm dữ liệu users (password được encode bằng BCrypt)
-- Password cho tất cả user là "password123"
-- BCrypt hash của "password123" là: $2a$10$XQOj2kpyI9UUcfbq1tA3PuP6eiB0pHEIzfTk4Wkn8LqNr7vHJOKCW
$2a$10$B8tC2TEshD.iRk9.fUK1r.jW5LKpD8U.9Jufc0ZnUAwoe9IruQ5Qm
INSERT INTO users (username, password, email, enabled) VALUES 
('admin', '$2a$10$B8tC2TEshD.iRk9.fUK1r.jW5LKpD8U.9Jufc0ZnUAwoe9IruQ5Qm', 'admin@vdt.com', true),
('user1', '$2a$10$B8tC2TEshD.iRk9.fUK1r.jW5LKpD8U.9Jufc0ZnUAwoe9IruQ5Qm', 'user1@vdt.com', true),
('user2', '$2a$10$B8tC2TEshD.iRk9.fUK1r.jW5LKpD8U.9Jufc0ZnUAwoe9IruQ5Qm', 'user2@vdt.com', true);


INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- admin có ROLE_ADMIN
(1, 2), -- admin có ROLE_USER
(2, 2), -- user1 có ROLE_USER
(3, 2); -- user2 có ROLE_USER


INSERT INTO students (full_name, birth_date, school_category) VALUES
('Nguyễn Đăng Quân', '2004-05-11', 'Đại học Công nghệ (UET)'),
('Trịnh Vinh Tuấn Đạt', '2003-10-05', 'Học viện Công nghệ Bưu chính Viễn thông - Cơ sở phía Bắc'),
('Ngô Xuân Hòa', '2004-07-27', 'Học viện Công nghệ Bưu chính Viễn thông - Cơ sở phía Bắc'),
('Bùi Đức Hùng', '2004-07-31', 'Đại học Bách Khoa Hà Nội (HUST)'),
('Nguyễn Tuấn Anh', '2003-01-25', 'Đại học Bách Khoa Hà Nội (HUST)'),
('Lương Nhật Hào', '2003-07-09', 'Đại học Công nghệ (UET)'),
('Nguyễn Đức Anh', '2003-01-23', 'Học viện Công nghệ Bưu chính Viễn thông - Cơ sở phía Bắc'),
('Đinh Trường Lãm', '2001-02-23', 'Đại học tổng hợp ITMO'),
('Nguyễn Đăng Bảo Lâm', '2004-08-17', 'Đại học Bách Khoa Hà Nội (HUST)'),
('Phạm Ngọc Hải Dương', '2005-03-20', 'Đại học Công nghệ (UET)'),
('Nguyễn Minh Quân', '2004-01-05', 'Đại học Bách Khoa Hà Nội (HUST)'),
('Nguyễn Sỹ Tân', '2004-07-07', 'Đại học Công nghệ (UET)'),
('Mai Xuân Duy Quang', '2003-07-04', 'Đại học Bách Khoa Hà Nội (HUST)'),
('Lê Tấn Phát', '2004-12-15', 'ĐH Mở Tp.HCM'),
('Nguyễn Quang Ninh', '2004-04-24', 'Đại học Công nghệ (UET)'),
('Nguyễn Trung Vương', '2003-10-03', 'Đại học Bách Khoa - ĐHQG TPHCM (HCMUT)'),
('Nguyễn Phước Ngưỡng Long', '2005-10-18', 'Đại học Công nghệ (UET)'),
('Nguyễn Văn Dương', '2003-10-30', 'Đại học Công nghệ (UET)'),
('Lê Minh Hoàng', '2004-05-17', 'Đại học Khoa học tự nhiên - ĐHQG TPHCM (HCMUS)'),
('Nguyễn Đức Thịnh', '2001-09-10', 'Đại học Thủy Lợi'),
('Hoàng Minh Thắng', '1999-06-09', 'Đại học tổng hợp ITMO'),
('Vũ Đình Ngọc Bảo', '2005-01-29', 'Đại học Khoa học tự nhiên - ĐHQG TPHCM (HCMUS)'),
('Nguyễn Hồng Lĩnh', '2003-12-08', 'Đại học Công nghệ (UET)');


SELECT COUNT(*) as total_students FROM students;
SELECT COUNT(*) as total_users FROM users;
SELECT COUNT(*) as total_roles FROM roles;
SELECT 
    u.username, 
    u.email, 
    r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
ORDER BY u.username;


SELECT 'Database initialization completed successfully' as status;