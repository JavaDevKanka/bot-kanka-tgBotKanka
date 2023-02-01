package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.AnsweredPolls;
import org.springframework.stereotype.Service;

@Service
public interface AnsweredPollsService extends ReadWriteService<AnsweredPolls, Long>  {
    void addUnansweredQuestions(Long chatId, Long questionId);
}
