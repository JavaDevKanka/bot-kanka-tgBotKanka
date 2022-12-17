package com.kankaBot.kankaBot.dao.impl;

import com.kankaBot.kankaBot.dao.abstracts.AnswerVariablesDao;
import com.kankaBot.kankaBot.models.Answer;
import org.springframework.stereotype.Component;

@Component
public class AnswerVariablesDaoImpl extends ReadWriteDaoImpl<Answer, Long> implements AnswerVariablesDao {

}
