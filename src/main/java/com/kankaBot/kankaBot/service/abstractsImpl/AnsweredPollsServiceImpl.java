package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.dao.abstracts.AnsweredPollsDao;
import com.kankaBot.kankaBot.models.AnsweredPolls;
import com.kankaBot.kankaBot.service.abstracts.AnsweredPollsService;
import org.springframework.stereotype.Service;

@Service
public class AnsweredPollsServiceImpl extends ReadWriteServiceImpl<AnsweredPolls, Long> implements AnsweredPollsService {

    private final AnsweredPollsDao answeredPollsDao;


    public AnsweredPollsServiceImpl(AnsweredPollsDao answeredPollsDao) {
        super(answeredPollsDao);
        this.answeredPollsDao = answeredPollsDao;
    }
    @Override
    public void addUnansweredQuestions(Long chatId, Long questionId) {
        answeredPollsDao.addUnansweredQuestions(chatId, questionId);
    }
}
