package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class QuestionGenerateDaoImpl extends ReadWriteDaoImpl<Question, Long> implements QuestionGenerateDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createQuestion(String question, Answer answers, Boolean is_multianswer) {
    }

    public List<Long> listIdQuestions() {
        return entityManager.createQuery("select q.id from Question q ", Long.class)
                .getResultList();
    }
    @Override
    public List<Long> listIdQuestionsBasic() {
        return entityManager.createQuery("select q.id from Question q where q.topic = 'basic'", Long.class)
                .getResultList();
    }
    @Override
    public List<Long> listIdQuestionsOOP() {
        return entityManager.createQuery("select q.id from Question q where q.topic = 'oop'", Long.class)
                .getResultList();


    }
    @Override
    public List<Long> listIdQuestionsExceptions() {
        return entityManager.createQuery("select q.id from Question q where q.topic = 'exceptions'", Long.class)
                .getResultList();

    }
    @Override
    public List<Long> listIdQuestionsIO() {
        return entityManager.createQuery("select q.id from Question q where q.topic = 'io'", Long.class)
                .getResultList();
    }
    @Override
    public List<Long> listIdQuestionsStream() {
        return entityManager.createQuery("select q.id from Question q where q.topic = 'stream'", Long.class)
                .getResultList();
    }
}
