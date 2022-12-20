CREATE TABLE user_chat_pin
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    chat_id    BIGINT                                  NOT NULL,

    CONSTRAINT pk_user_chat_pin PRIMARY KEY (id)
);