package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.dao.service.model.ReadWriteServiceImpl;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Question;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class QuestionGenerateServiceImpl extends ReadWriteServiceImpl<Question, Long> implements QuestionGenerateService {

    private final QuestionGenerateDao questionGenerateDao;

    public QuestionGenerateServiceImpl(QuestionGenerateDao questionGenerateDao) {
        super(questionGenerateDao);
        this.questionGenerateDao = questionGenerateDao;
    }


    public void createQuestion(String question, Answer answers, Boolean is_multianswer) {

   }


}
