# ripple schema

# --- !Ups

CREATE TABLE music (
    id int(10) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    raw_data longblob NOT NULL,
    artist_name varchar(255),
    album_name varchar(255),
    song_title varchar(255),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE music;