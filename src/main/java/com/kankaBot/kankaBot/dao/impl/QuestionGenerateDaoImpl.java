package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.JavaQuestion;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class QuestionGenerateDaoImpl extends ReadWriteDaoImpl<JavaQuestion, Long> implements QuestionGenerateDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createQuestion(String question, String answer, Boolean is_multianswer) {
        entityManager
                .createQuery("insert into JavaQuestion (question, answers, is_multiAnswer) values (?, ?, ?)")
                .setParameter(1, question)
                .setParameter(2, answer)
                .setParameter(3, is_multianswer);
    }

}
