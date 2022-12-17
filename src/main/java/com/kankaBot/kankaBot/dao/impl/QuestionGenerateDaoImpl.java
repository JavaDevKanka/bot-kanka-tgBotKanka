package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class QuestionGenerateDaoImpl extends ReadWriteDaoImpl<Question, Long> implements QuestionGenerateDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createQuestion(String question, Answer answers, Boolean is_multianswer) {

    }
}
