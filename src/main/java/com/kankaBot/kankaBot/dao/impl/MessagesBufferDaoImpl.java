package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.MessagesBufferDao;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.MessagesBuffer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class MessagesBufferDaoImpl extends ReadWriteDaoImpl<MessagesBuffer, Long> implements MessagesBufferDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void flushBuffer() {
        entityManager.createQuery("delete from MessagesBuffer")
                .executeUpdate();
    }

    @Override
    public Integer CountBuffer() {
        return entityManager.createQuery("select count(*) from MessagesBuffer mb", Integer.class).getMaxResults();
    }

    @Override
    public List<String> answerList() {
        return entityManager.createQuery("select mb.message from MessagesBuffer mb", String.class)
                .getResultList();
    }
}
