

CREATE TABLE users (
                       id INT NOT NULL AUTO_INCREMENT,
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       enabled INT NOT NULL,
                       PRIMARY KEY (id)
);

CREATE TABLE authorities (
                             id INT NOT NULL AUTO_INCREMENT,
                             username VARCHAR(50) NOT NULL,
                             authority VARCHAR(50) NOT NULL,
                             PRIMARY KEY (id)
);


INSERT INTO users VALUES (null, 'happy', '12345', '1');
INSERT INTO authorities VALUES (null, 'happy', 'write');

-- 사용자 정의 테이블
create table customer (
                          id int NOT NULL AUTO_INCREMENT,
                          email varchar(50) NOT NULL,
                          pwd varchar(200) NOT NULL,
                          role varchar(50) NOT NULL,
                          primary key (id)
);

INSERT INTO customer (email, pwd, role)
VALUES ('minjiki@test.com', '54321', 'admin');