# ripple schema

# --- !Ups

CREATE TABLE music (
    id int(10) NOT NULL AUTO_INCREMENT,
    name varchar(10) NOT NULL,
    raw_data longblob NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE music;