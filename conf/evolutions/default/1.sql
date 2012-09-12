# play_sample schema

# --- !Ups

CREATE TABLE User (
    id int(10) NOT NULL AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    email varchar(100) NOT NULL,
    password varchar(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name),
    UNIQUE(email)
);

# --- !Downs

DROP TABLE User;