SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS complaint_history;
DROP TABLE IF EXISTS complaints;
DROP TABLE IF EXISTS complaint_users;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE complaint_users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('student','admin','staff') NOT NULL
);

CREATE TABLE complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    is_anonymous TINYINT(1) NOT NULL DEFAULT 0,
    status ENUM('Pending','Assigned','In-Progress','Resolved') NOT NULL DEFAULT 'Pending',
    assigned_to INT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES complaint_users(user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    FOREIGN KEY (assigned_to) REFERENCES complaint_users(user_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);


CREATE TABLE complaint_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    status ENUM('Pending','Assigned','In-Progress','Resolved') NOT NULL,
    remarks TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- USERS
INSERT INTO complaint_users (name, email, password, role) VALUES
('Aziz Hussain', 'aziz_mhussain@outlook.com', 'aziz$123', 'student'),
('Vedant Jogidasani', 'vedant_jogidasani@outlook.com', 'vedant$123', 'admin'),
('Omkar Bandikatte', 'omkar_bandikatte@outlook.com', 'omkar$123', 'staff'),
('Hussain Shaikh', 'hussain_shaikh@outlook.com', 'hussain$123', 'staff');

-- COMPLAINTS (5 total)
-- 2 Pending
-- 3 Pending complaints (2 normal + 1 anonymous)
INSERT INTO complaints (user_id, category, description, status, assigned_to, is_anonymous)
VALUES
(1, 'Hostel', 'Light not working in corridor', 'Pending', NULL, 0),
(1, 'Fees', 'Fee receipt not generated on portal', 'Pending', NULL, 0),
(1, 'Assault', 'Teacher assaulted me!!!', 'Pending', NULL, 1);

-- 1 Assigned to Vedant (admin)
INSERT INTO complaints (user_id, category, description, status, assigned_to)
VALUES
(1, 'Academics', 'Attendance not updated by professor', 'Assigned', 2);

-- 1 Assigned to Omkar
INSERT INTO complaints (user_id, category, description, status, assigned_to)
VALUES
(1, 'Canteen', 'Menu not updated for two weeks', 'Assigned', 3);

-- 1 Resolved by Hussain
INSERT INTO complaints (user_id, category, description, status, assigned_to)
VALUES
(1, 'Library', 'Printer not working on 1st floor', 'Resolved', 4);


-- HISTORY ENTRIES

-- Complaint 1: Pending
INSERT INTO complaint_history (complaint_id, status, remarks)
VALUES (1, 'Pending', 'Complaint submitted by student');

-- Complaint 2: Pending
INSERT INTO complaint_history (complaint_id, status, remarks)
VALUES (2, 'Pending', 'Complaint submitted by student');

-- Complaint 3: Assigned to Vedant
INSERT INTO complaint_history (complaint_id, status, remarks)
VALUES
(3, 'Pending', 'Complaint submitted'),
(3, 'Assigned', 'Assigned to admin Vedant');

-- Complaint 4: Assigned to Omkar
INSERT INTO complaint_history (complaint_id, status, remarks)
VALUES
(4, 'Pending', 'Complaint submitted'),
(4, 'Assigned', 'Assigned to staff Omkar');

-- Complaint 5: Resolved by Hussain
INSERT INTO complaint_history (complaint_id, status, remarks)
VALUES
(5, 'Pending', 'Complaint submitted'),
(5, 'Assigned', 'Assigned to staff Hussain'),
(5, 'In-Progress', 'Work in progress'),
(5, 'Resolved', 'Issue fixed by Hussain');
