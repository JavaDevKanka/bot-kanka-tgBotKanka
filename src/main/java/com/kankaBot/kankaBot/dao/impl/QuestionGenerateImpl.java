package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionGenerateImpl implements QuestionGenerate {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createQuestion(String question, String answer) {
        entityManager
                .createNativeQuery("insert into java_question values (?, ?)")
                .setParameter(1, question)
                .setParameter(2, answer)
                .executeUpdate();
    }

    public List showQuestion() {
        return entityManager.createNativeQuery("select * from  java_question")
                .getResultList();
    }

}
