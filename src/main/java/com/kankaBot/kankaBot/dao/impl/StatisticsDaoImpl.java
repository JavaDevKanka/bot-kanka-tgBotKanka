package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.StatisticsDao;
import com.kankaBot.kankaBot.models.Statistics;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatisticsDaoImpl extends ReadWriteDaoImpl<Statistics, Long> implements StatisticsDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void clearStatisticForTheUserChatId(Long chatId) {
        entityManager.createQuery("DELETE FROM Statistics s WHERE s.chatId = :chatId")
                .setParameter("chatId", chatId)
                .executeUpdate();
    }

    @Override
    public Long getTotalCountScoreByChatId(Long chatId) {
        return entityManager.createQuery("select count(s.quizUserAnswer) FROM Statistics s where s.chatId = :chatId and" +
                        " s.correctQuizAnswer = s.quizUserAnswer", Long.class)
                .setParameter("chatId", chatId)
                .getSingleResult();
    }

    @Override
    public List<Long> getListForCheckRepeats(Long chatId) {
        return entityManager.createQuery("select s.questionId FROM Statistics s where s.chatId = :chatId", Long.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }
}
