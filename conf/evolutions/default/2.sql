# ripple schema

# --- !Ups

CREATE TABLE chat_room (
    id int(10) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name)
);

# --- !Downs

DROP TABLE chat_room;