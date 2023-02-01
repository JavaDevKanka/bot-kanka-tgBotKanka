package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.AnsweredPolls;
import org.springframework.stereotype.Component;

@Component
public interface AnsweredPollsDao extends ReadWriteDao<AnsweredPolls, Long> {
    void addUnansweredQuestions(Long chatId, Long questionId);
}
