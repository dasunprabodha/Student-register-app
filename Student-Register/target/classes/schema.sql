
DROP TABLE IF EXISTS Student;
CREATE TABLE Student(
                        id INT PRIMARY KEY AUTO_INCREMENT ,
                        first_name VARCHAR(100) NOT NULL ,
                        last_name VARCHAR(100) NOT NULL ,
                        address VARCHAR(500) DEFAULT 'PANADURA' NOT NULL ,
                        gender ENUM ('MALE','FEMALE') NOT NULL ,
                        dob DATE NOT NULL
);


# INSERT INTO  Student VALUES (1,'Kasun','Sampth',DEFAULT,'MALE','1995-02-02'),
#                             (2,'Nuwan','Vissai',DEFAULT,'MALE','1998-03-02'),
#                             (3,'John','Sampath','GALLE','MALE','1997-02-02'),
#                             (4,'Muga','Sampath','GALLE','MALE','1995-02-02'),
#                             (5,'Patta','Sampath','GALLE','MALE','1985-03-02'),
#                             (6,'Gracely','Sampath','GALLE','MALE','2001-02-02');