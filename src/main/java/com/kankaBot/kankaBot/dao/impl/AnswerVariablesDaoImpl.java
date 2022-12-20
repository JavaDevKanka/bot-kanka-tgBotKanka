package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.AnswerVariablesDao;
import com.kankaBot.kankaBot.models.Answer;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class AnswerVariablesDaoImpl extends ReadWriteDaoImpl<Answer, Long> implements AnswerVariablesDao {

    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<String> getAnswersByQuestId(Long questionId) {
        return entityManager.createQuery("select a.answer from Answer a where a.question.id = :questionId", String.class)
                .setParameter("questionId", questionId)
                .getResultList();
    }

    @Override
    public List<Answer> getAnswerObjByQuestId(Long questionId) {
        return entityManager.createQuery("select a from Answer a where a.question.id = :questionId", Answer.class)
                .setParameter("questionId", questionId)
                .getResultList();
    }


    @Override
    public Integer getIntValueIsCorrect(long answerId) {
        return entityManager.createQuery("select a.id from Answer a where a.id = :answerId and a.is_right = true", Integer.class)
                .setParameter("answerId", answerId)
                .getSingleResult();
    }


}
