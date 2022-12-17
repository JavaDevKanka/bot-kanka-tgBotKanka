package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.AnswerVariablesDao;
import com.kankaBot.kankaBot.dao.service.model.ReadWriteServiceImpl;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import com.kankaBot.kankaBot.service.abstracts.AnswerVariablesService;
import org.springframework.stereotype.Service;

@Service
public class AnswerVariablesServiceImpl extends ReadWriteServiceImpl<Answer, Long> implements AnswerVariablesService {

    private final AnswerVariablesDao answerVariablesDao;


    public AnswerVariablesServiceImpl(AnswerVariablesDao answerVariablesDao) {
        super(answerVariablesDao);
        this.answerVariablesDao = answerVariablesDao;
    }
}
