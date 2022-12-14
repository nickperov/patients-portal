CREATE TABLE IF NOT EXISTS PATIENT
(
    ID int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    FIRST_NAME varchar(255) NOT NULL,
    LAST_NAME varchar(255) NOT NULL,
    DATE_OF_BIRTH date NOT NULL
);

CREATE UNIQUE INDEX IDX_PATIENT ON PATIENT (FIRST_NAME, LAST_NAME, DATE_OF_BIRTH);