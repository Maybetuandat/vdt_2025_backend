-- init.sql
CREATE DATABASE student_management;

-- Kết nối tới database (dùng \c thay vì use)
\c student_management;

-- Tạo bảng (bỏ dấu phẩy thừa)
CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    school_category VARCHAR(500)
);

-- Tạo index
CREATE INDEX IF NOT EXISTS idx_students_full_name ON students(full_name);
CREATE INDEX IF NOT EXISTS idx_students_school_category ON students(school_category);

-- Thêm dữ liệu
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