package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.dao.abstracts.PoolQuestionsBufferDao;
import com.kankaBot.kankaBot.models.PoolQuestionsBuffer;
import com.kankaBot.kankaBot.service.abstracts.PoolQuestionsBufferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PoolQuestionsBufferServiceImpl extends ReadWriteServiceImpl<PoolQuestionsBuffer, Long> implements PoolQuestionsBufferService {

    private final PoolQuestionsBufferDao poolQuestionsBufferDao;


    public PoolQuestionsBufferServiceImpl(PoolQuestionsBufferDao poolQuestionsBufferDao) {
        super(poolQuestionsBufferDao);
        this.poolQuestionsBufferDao = poolQuestionsBufferDao;
    }
    @Override
    @Transactional
    public void removeAnsweredFromBuffer(Long chatId, Long questionId) {
        poolQuestionsBufferDao.removeAnsweredFromBuffer(chatId, questionId);
    }
    @Override
    public List<Long> getUnusedIdsQuestion(Long chatId) {
        return poolQuestionsBufferDao.getUnusedIdsQuestion(chatId);
    }
    @Override
    @Transactional
    public void cleanPoolBuffer(Long chatId) {
        poolQuestionsBufferDao.cleanPoolBuffer(chatId);
    }
}
