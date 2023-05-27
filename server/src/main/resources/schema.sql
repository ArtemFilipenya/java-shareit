CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(255)                            NOT NULL,
    requester_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created      DATE                                    NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(255)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL DEFAULT FALSE,
    owner_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id  BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP                               NOT NULL,
    end_date   TIMESTAMP                               NOT NULL,
    item_id    BIGINT REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(30)                             NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(255)                            NOT NULL,
    item_id   BIGINT REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created   TIMESTAMP                               NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);