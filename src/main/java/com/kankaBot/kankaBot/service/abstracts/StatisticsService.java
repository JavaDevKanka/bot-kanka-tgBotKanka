package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.dto.ResultOfTest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface StatisticsService extends ReadWriteService<Statistics, Long> {

    void clearStatisticForTheUserChatId(Long chatId);
    Long getTotalCountScoreByChatId(Long chatId);
    List<Long> getListForCheckRepeats(Long chatId);
    Long getCountUserAnswers(Long chatId);
    int getCountRightForResultByChatId(Long chatId);
    List<ResultOfTest> getLoseAnswersForResultByChatId(Long chatId);
}
