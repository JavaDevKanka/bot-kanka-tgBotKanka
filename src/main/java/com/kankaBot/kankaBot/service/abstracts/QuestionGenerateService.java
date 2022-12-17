package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Service;

@Service
public interface QuestionGenerateService extends ReadWriteService<Question, Long> {
    void createQuestion(String question, Answer answers, Boolean is_multianswer);
}

