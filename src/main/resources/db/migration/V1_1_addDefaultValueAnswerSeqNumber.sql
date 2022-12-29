ALTER TABLE answer
    drop column is_right;

alter table answer
    add column is_right boolean default false;