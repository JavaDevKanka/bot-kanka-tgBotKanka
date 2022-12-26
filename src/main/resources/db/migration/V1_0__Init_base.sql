create sequence answer_seq start 1 increment 1;
create sequence message_seq start 1 increment 1;
create sequence poll_ans_seq start 1 increment 1;
create sequence question_seq start 1 increment 1;
create sequence statistics_seq start 1 increment 1;
create sequence ads_seq start 1 increment 1;

CREATE TABLE answer
(
    id          BIGINT       NOT NULL,
    answer      varchar(255) not null,
    is_right    boolean,
    seqnumber   bigint,
    question_id bigint       not null,
    CONSTRAINT pk_answer PRIMARY KEY (id)
);

CREATE TABLE ads_table
(
    id BIGINT       NOT NULL,
    ad varchar(255) not null,
    CONSTRAINT pk_ads PRIMARY KEY (id)
);

CREATE TABLE message_buffer
(
    id                bigint not null,
    chat_id           bigint not null,
    message           varchar(255),
    message_id        BIGINT,
    correct_answer_id INTEGER,
    constraint pk_message_buffer primary key (id)
);

create table poll_answer
(
    id      bigint not null,
    user_id bigint,
    constraint pk_poll_answer primary key (id)
);

create table question
(
    id          bigint       not null,
    question    varchar(255) not null,
    explanation varchar(255),
    constraint pk_question primary key (id)
);

create table statistics
(
    id                  bigint  not null,
    chat_id             bigint  not null,
    quiz_user_answer    INTEGER,
    correct_quiz_answer integer not null,
    question_id         bigint  not null not null,
    constraint pk_statistics primary key (id)
);

create table users
(
    chat_id       bigint,
    first_name    varchar(255),
    last_name     varchar(255),
    user_name     varchar(255),
    registered_at TIMESTAMP WITHOUT TIME ZONE,
    constraint pk_users primary key (chat_id)
);
