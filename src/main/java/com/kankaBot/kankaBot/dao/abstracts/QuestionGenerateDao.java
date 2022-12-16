package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.JavaQuestion;

public interface QuestionGenerateDao extends ReadWriteDao<JavaQuestion, Long> {

    void createQuestion(String question, String answer, Boolean is_multianswer);


}
