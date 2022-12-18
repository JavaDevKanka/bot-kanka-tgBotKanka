package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.MessagesBufferDao;
import com.kankaBot.kankaBot.dao.abstracts.ReadWriteDao;
import com.kankaBot.kankaBot.models.MessagesBuffer;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;

public class MessagesBufferServiceImpl extends ReadWriteServiceImpl<MessagesBuffer, Long> implements MessagesBufferService {

    private final MessagesBufferDao messagesBufferDao;

    public MessagesBufferServiceImpl(MessagesBufferDao messagesBufferDao) {
        super(messagesBufferDao);
        this.messagesBufferDao = messagesBufferDao;

    }
}
