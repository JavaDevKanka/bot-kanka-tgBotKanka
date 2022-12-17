package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.dao.service.abstracts.ReadWriteService;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Question;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface QuestionGenerateService extends ReadWriteService<Question, Long> {
    void createQuestion(String question, Answer answers, Boolean is_multianswer);
}

