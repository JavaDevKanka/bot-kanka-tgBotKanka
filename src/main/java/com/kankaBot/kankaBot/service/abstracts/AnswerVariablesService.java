package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.Answer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AnswerVariablesService extends ReadWriteService<Answer, Long> {

    List<String> getAnswersByQuestId(Long questionId);
    List<Answer> getAnswerObjByQuestId(Long questionId);
    Integer getIntValueIsCorrect(long answerId);

}
