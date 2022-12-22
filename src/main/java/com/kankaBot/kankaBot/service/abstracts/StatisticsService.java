package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.Statistics;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StatisticsService extends ReadWriteService<Statistics, Long> {

    void clearStatisticForTheUserChatId(Long chatId);
    Long getTotalCountScoreByChatId(Long chatId);
    List<Long> getListForCheckRepeats(Long chatId);
}
