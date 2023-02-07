package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.StatisticsDao;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.dto.ResultOfTest;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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

    @Override
    public Long getCountUserAnswers(Long chatId) {
        return entityManager.createQuery("select count(s.id) FROM Statistics s where s.chatId = :chatId", Long.class)
                .setParameter("chatId", chatId)
                .getSingleResult();
    }

    @Override
    public Long getCountRightForResultByChatId(Long chatId) {
        return entityManager.createQuery("select count (s.id) from Statistics s where s.quizUserAnswer = s.correctQuizAnswer", Long.class)
                .getSingleResult();
    }
    @Override
    public List<ResultOfTest> getLoseAnswersForResultByChatId(Long chatId) {
        return entityManager.createQuery("select new com.kankaBot.kankaBot.models.dto.ResultOfTest " +
                "(q.question, " +
                "a.answer, " +
                "(select ac.answer from Answer ac where ac.seqnumber = s.quizUserAnswer and ac.question.id = q.id)) " +
                "from Statistics s " +
                "left join Question q on q.id = s.questionId" +
                " join Answer a on a.question.id = s.questionId and s.quizUserAnswer != s.correctQuizAnswer and a.is_right = true where s.chatId =: chatId order by q.id", ResultOfTest.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }
    @Override
    public List<Long> getUnansweredQuestionIdFromStatistics(Long chatId) {
        return entityManager.createQuery("select s.questionId from Statistics s where s.correctQuizAnswer != s.quizUserAnswer and s.chatId =: chatId", Long.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }

    @Override
    public List<Long> getAnsweredQuestionIdFromStatistics(Long chatId) {
        return entityManager.createQuery("select s.questionId from Statistics s where s.correctQuizAnswer = s.quizUserAnswer and s.chatId =: chatId", Long.class)
                .setParameter("chatId", chatId)
                .getResultList();
    }
}
