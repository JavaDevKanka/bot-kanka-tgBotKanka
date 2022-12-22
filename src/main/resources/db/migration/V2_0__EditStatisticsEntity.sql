alter table statistics
    drop column correct_answers;


alter table statistics
    add column quiz_user_answer INTEGER;