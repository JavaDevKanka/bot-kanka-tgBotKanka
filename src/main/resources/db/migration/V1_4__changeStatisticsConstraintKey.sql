ALTER TABLE user_statistics
    DROP CONSTRAINT pk_users_data_table;

alter table user_statistics
    add constraint pk_users_data_table primary key (usersDataTable_chatId, statistics_chatId);