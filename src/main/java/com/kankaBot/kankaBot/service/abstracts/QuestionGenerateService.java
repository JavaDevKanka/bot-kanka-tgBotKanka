package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestionGenerateService extends ReadWriteService<Question, Long> {
    void createQuestion(String question, Answer answers, Boolean is_multianswer);
    List<Long> listIdQuestions();
    List<Long> listIdQuestionsBasic();
    List<Long> listIdQuestionsExceptions();
    List<Long> listIdQuestionsOOP();
    List<Long> listIdQuestionsIO();
    List<Long> listIdQuestionsStream();
}

