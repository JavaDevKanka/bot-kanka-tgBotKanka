package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import org.springframework.stereotype.Component;

@Component
public interface AnswerVariablesDao extends ReadWriteDao<Answer, Long> {

}
