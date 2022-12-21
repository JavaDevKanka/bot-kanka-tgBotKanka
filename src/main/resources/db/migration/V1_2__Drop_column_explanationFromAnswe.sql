alter table answer
    drop column "explanation";

alter table question
    add column "explanation" varchar(255);