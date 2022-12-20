package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.MessagesBufferDao;
import com.kankaBot.kankaBot.models.MessagesBuffer;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagesBufferServiceImpl extends ReadWriteServiceImpl<MessagesBuffer, Long> implements MessagesBufferService {

    private final MessagesBufferDao messagesBufferDao;

    public MessagesBufferServiceImpl(MessagesBufferDao messagesBufferDao) {
        super(messagesBufferDao);
        this.messagesBufferDao = messagesBufferDao;

    }

    @Override
    public List<String> answerList() {
        return messagesBufferDao.answerList();
    }

    @Override
    public void flushBuffer() {
        messagesBufferDao.deleteAll(messagesBufferDao.getAll());
    }

    @Override
    public Integer CountBuffer() {
        return messagesBufferDao.CountBuffer();
    }
}
