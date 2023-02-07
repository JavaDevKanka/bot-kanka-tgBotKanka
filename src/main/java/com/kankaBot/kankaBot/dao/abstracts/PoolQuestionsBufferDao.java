package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.PoolQuestionsBuffer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PoolQuestionsBufferDao extends ReadWriteDao<PoolQuestionsBuffer, Long> {
    void removeAnsweredFromBuffer(Long chatId, Long questionId);
    List<Long> getUnusedIdsQuestion(Long chatId);
    void cleanPoolBuffer(Long chatId);
}
