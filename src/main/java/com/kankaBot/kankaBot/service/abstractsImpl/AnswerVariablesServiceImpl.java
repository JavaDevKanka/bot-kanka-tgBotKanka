package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.dao.abstracts.AnswerVariablesDao;
import com.kankaBot.kankaBot.models.Answer;

import com.kankaBot.kankaBot.service.abstracts.AnswerVariablesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerVariablesServiceImpl extends ReadWriteServiceImpl<Answer, Long> implements AnswerVariablesService {

    private final AnswerVariablesDao answerVariablesDao;


    public AnswerVariablesServiceImpl(AnswerVariablesDao answerVariablesDao) {
        super(answerVariablesDao);
        this.answerVariablesDao = answerVariablesDao;
    }

    @Override
    public List<String> getAnswersByQuestId(Long questionId) {
        return answerVariablesDao.getAnswersByQuestId(questionId);
    }

    @Override
    public List<Answer> getAnswerObjByQuestId(Long questionId) {
        return answerVariablesDao.getAnswerObjByQuestId(questionId);
    }

    @Override
    public Integer getIntValueIsCorrect(long answerId) {
        return answerVariablesDao.getIntValueIsCorrect(answerId);
    }


}
