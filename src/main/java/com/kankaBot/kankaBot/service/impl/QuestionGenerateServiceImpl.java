package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionGenerateServiceImpl extends ReadWriteServiceImpl<Question, Long> implements QuestionGenerateService {

    private final QuestionGenerateDao questionGenerateDao;

    public QuestionGenerateServiceImpl(QuestionGenerateDao questionGenerateDao) {
        super(questionGenerateDao);
        this.questionGenerateDao = questionGenerateDao;
    }


    public void createQuestion(String question, Answer answers, Boolean is_multianswer) {

   }

    public List<Long> listIdQuestions() {
        return questionGenerateDao.listIdQuestions();
    }


}
