package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Question;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public interface QuestionGenerateDao extends ReadWriteDao<Question, Long> {

    void createQuestion(String question, Answer answers, Boolean is_multianswer);


}
