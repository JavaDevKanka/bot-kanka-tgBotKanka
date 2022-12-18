package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.MessagesBufferDao;
import com.kankaBot.kankaBot.models.MessagesBuffer;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class MessagesBufferDaoImpl extends ReadWriteDaoImpl<MessagesBuffer, Long> implements MessagesBufferDao {

    @PersistenceContext
    private EntityManager entityManager;
}
