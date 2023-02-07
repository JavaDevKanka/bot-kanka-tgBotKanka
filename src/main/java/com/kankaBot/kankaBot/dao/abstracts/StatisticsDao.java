package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.dto.ResultOfTest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StatisticsDao extends ReadWriteDao<Statistics, Long> {

    void clearStatisticForTheUserChatId(Long chatId);
    Long getTotalCountScoreByChatId(Long chatId);
    List<Long> getListForCheckRepeats(Long chatId);
    Long getCountUserAnswers(Long chatId);
    Long getCountRightForResultByChatId(Long chatId);
    List<ResultOfTest> getLoseAnswersForResultByChatId(Long chatId);
    List<Long> getUnansweredQuestionIdFromStatistics(Long chatId);
    List<Long> getAnsweredQuestionIdFromStatistics(Long chatId);
}
