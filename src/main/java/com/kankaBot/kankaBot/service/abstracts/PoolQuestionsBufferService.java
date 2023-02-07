package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.PoolQuestionsBuffer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PoolQuestionsBufferService extends ReadWriteService<PoolQuestionsBuffer, Long>  {
    void removeAnsweredFromBuffer(Long chatId, Long questionId);
    List<Long> getUnusedIdsQuestion(Long chatId);
    void cleanPoolBuffer(Long chatId);
}
