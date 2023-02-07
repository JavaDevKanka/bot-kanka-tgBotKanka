package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.PoolQuestionsBufferDao;
import com.kankaBot.kankaBot.models.PoolQuestionsBuffer;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class PoolQuestionsBufferDaoImpl extends ReadWriteDaoImpl<PoolQuestionsBuffer, Long>  implements PoolQuestionsBufferDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public void removeAnsweredFromBuffer(Long chatId, Long questionId) {
         entityManager.createQuery("delete from PoolQuestionsBuffer q where q.chatId = :chatId and q.questionId = :questionId")
                .setParameter("chatId", chatId)
                .setParameter("questionId", questionId)
                .executeUpdate();
    }

    @Override
    public void cleanPoolBuffer(Long chatId) {
        entityManager.createQuery("delete from PoolQuestionsBuffer q where q.chatId = :chatId")
                .setParameter("chatId", chatId)
                .executeUpdate();
    }
    @Override
    public List<Long> getUnusedIdsQuestion(Long chatId) {
        return entityManager.createQuery("select q.questionId from PoolQuestionsBuffer q where q.chatId = :chatId", Long.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }

}
