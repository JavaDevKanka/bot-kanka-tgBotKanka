package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.dao.service.abstracts.ReadWriteService;
import com.kankaBot.kankaBot.models.AnswerQuestionGenerate.Answer;
import org.springframework.stereotype.Service;

@Service
public interface AnswerVariablesService extends ReadWriteService<Answer, Long> {

}
