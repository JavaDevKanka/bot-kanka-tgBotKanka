package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.AnsweredPollsDao;
import com.kankaBot.kankaBot.models.AnsweredPolls;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class AnsweredPollsDaoImpl extends ReadWriteDaoImpl<AnsweredPolls, Long>  implements AnsweredPollsDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addUnansweredQuestions(Long chatId, Long questionId) {
        entityManager.createNativeQuery("insert into answered_polls (chat_id, question_id) values (?, ?)")
                .setParameter(1, chatId)
                .setParameter(2, questionId)
                .executeUpdate();
    }
}
