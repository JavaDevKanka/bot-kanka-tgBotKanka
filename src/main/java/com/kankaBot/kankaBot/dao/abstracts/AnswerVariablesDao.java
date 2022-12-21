package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.Answer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AnswerVariablesDao extends ReadWriteDao<Answer, Long> {
    List<String> getAnswersByQuestId(Long questionId);
    List<Answer> getAnswerObjByQuestId(Long questionId);
    Integer getIntValueIsCorrect(long answerId);
}
