# ripple schema

# --- !Ups

CREATE TABLE access_token (
    id int(10) NOT NULL AUTO_INCREMENT,
    user_id int(10) NOT NULL,
    provider varchar(255) NOT NULL,
    token varchar(255) NOT NULL,
    secret varchar(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(token)
);

# --- !Downs

DROP TABLE access_token;