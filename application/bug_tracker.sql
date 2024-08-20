CREATE DATABASE bug_tracker;

USE bug_tracker;


-- ------------------ users table ---------------------

CREATE TABLE users(
	user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    user_password VARCHAR(100) NOT NULL,
	user_email VARCHAR(100),
    user_role ENUM('MANAGER', 'DEVELOPER', 'TESTER') NOT NULL,
    project_count INT DEFAULT 0,
    last_logged_in DATETIME
) AUTO_INCREMENT = 100001;

INSERT INTO users(username, user_password, user_role) VALUES ("prathmesh", "root", "MANAGER");

SELECT * FROM users;

DROP TABLE users;


-- ------------------ projects table ----------------

CREATE TABLE projects (
	project_id INT PRIMARY KEY AUTO_INCREMENT,
    project_name VARCHAR(100) NOT NULL,
    project_status ENUM('COMPLETED', 'IN_PROGRESS') DEFAULT 'IN_PROGRESS',
    project_manager INT NOT NULL,
    start_date DATE NOT NULL,
    no_of_bugs INT
) AUTO_INCREMENT = 101;

ALTER TABLE projects
ADD CONSTRAINT pfk_project_manager FOREIGN KEY (project_manager)
REFERENCES users(user_id)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE projects DROP FOREIGN KEY pfk_project_manager;

INSERT INTO projects (project_name, project_status, project_manager, no_of_bugs) VALUES ("BUG TRACKER", "COMPLETED", 12, 6);

SELECT * FROM projects;

DELETE FROM projects;

DROP TABLE projects;

-- ---------- bugs table ---------------- 
CREATE TABLE bugs (
	bug_id INT PRIMARY KEY AUTO_INCREMENT,
    bug_name VARCHAR(500) NOT NULL,
    bug_description VARCHAR(1000),
    created_by INT NOT NULL,
    created_on DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    severity_level ENUM('LOW', 'MID', 'HIGH') NOT NULL,
    bug_status ENUM('MARKED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'IN_PROGRESS',
    accepted BOOLEAN DEFAULT false,
    project_id INT NOT NULL
) AUTO_INCREMENT = 1001;

ALTER TABLE bugs
ADD CONSTRAINT bfk_created_by FOREIGN KEY (created_by)
REFERENCES users(user_id)
ON UPDATE CASCADE
ON DELETE RESTRICT;

ALTER TABLE bugs
ADD CONSTRAINT bfk_project_id FOREIGN KEY (project_id)
REFERENCES projects (project_id)
ON UPDATE CASCADE
ON DELETE CASCADE;

ALTER TABLE bugs DROP FOREIGN KEY bfk_user_id;
ALTER TABLE bugs DROP FOREIGN KEY bfk_project_id;

DROP TABLE bugs;


-- ------------------ user_projects table ----------------

CREATE TABLE user_projects (
	user_id INT NOT NULL,
    project_id INT NOT NULL,
    CONSTRAINT upfk_user_id FOREIGN KEY (user_id)
		REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
	CONSTRAINT upfk_project_id FOREIGN KEY (project_id)
		REFERENCES projects(project_id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
	PRIMARY KEY (user_id, project_id)
);

ALTER TABLE user_projects
ADD CONSTRAINT upfk_user_id FOREIGN KEY (user_id)
	REFERENCES users(user_id)
	ON UPDATE CASCADE
	ON DELETE CASCADE;
ALTER TABLE user_projects
ADD CONSTRAINT upfk_project_id FOREIGN KEY (project_id)
	REFERENCES projects(project_id)
	ON UPDATE CASCADE
	ON DELETE CASCADE;

ALTER TABLE user_projects DROP FOREIGN KEY upfk_user_id;
ALTER TABLE user_projects DROP FOREIGN KEY upfk_project_id;

DROP TABLE user_projects;


-- ------------------ user_bugs table ----------------

CREATE TABLE user_bugs (
	user_id INT NOT NULL,
    bug_id INT NOT NULL,
    CONSTRAINT ubfk_user_id FOREIGN KEY (user_id)
		REFERENCES users(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
	CONSTRAINT ubfk_bug_id FOREIGN KEY (bug_id)
		REFERENCES bugs(bug_id)
		ON UPDATE CASCADE
        ON DELETE CASCADE,
	PRIMARY KEY (user_id, bug_id)
);

ALTER TABLE user_bugs
ADD CONSTRAINT ubfk_user_id FOREIGN KEY (user_id)
	REFERENCES users(user_id)
	ON UPDATE CASCADE
	ON DELETE CASCADE;
    
ALTER TABLE user_bugs
ADD CONSTRAINT ubfk_bug_id FOREIGN KEY (bug_id)
	REFERENCES bugs(bug_id)
	ON UPDATE CASCADE
	ON DELETE CASCADE;

ALTER TABLE user_bugs DROP FOREIGN KEY ubfk_user_id;
ALTER TABLE user_bugs DROP FOREIGN KEY ubfk_bug_id;

DROP TABLE user_bugs;


