package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QuestionGenerateDao extends ReadWriteDao<Question, Long> {

    void createQuestion(String question, Answer answers, Boolean is_multianswer);
    List<Long> listIdQuestions();

}
