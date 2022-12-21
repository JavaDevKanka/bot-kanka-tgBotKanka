CREATE TABLE statistics
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    chat_id         BIGINT                                  NOT NULL,
    correct_answers BIGINT,
    CONSTRAINT pk_message_star PRIMARY KEY (id)
);

create table user_statistics
(
    usersDataTable_chatId bigint,
    statistics_chatId bigint,
    constraint pk_users_data_table primary key (usersDataTable_chatId)
);