package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.Statistics;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StatisticsDao extends ReadWriteDao<Statistics, Long> {

    void clearStatisticForTheUserChatId(Long chatId);
    Long getTotalCountScoreByChatId(Long chatId);
    List<Long> getListForCheckRepeats(Long chatId);
    Long getCountUserAnswers(Long chatId);
}
