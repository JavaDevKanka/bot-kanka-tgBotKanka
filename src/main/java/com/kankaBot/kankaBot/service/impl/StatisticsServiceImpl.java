package com.kankaBot.kankaBot.service.impl;

import com.kankaBot.kankaBot.dao.abstracts.StatisticsDao;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.service.abstracts.StatisticsService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class StatisticsServiceImpl extends ReadWriteServiceImpl<Statistics, Long> implements StatisticsService {

    private final StatisticsDao statisticsDao;

    public StatisticsServiceImpl(StatisticsDao statisticsDao) {
        super(statisticsDao);
        this.statisticsDao = statisticsDao;
    }

    @Override
    @Transactional
    public void clearStatisticForTheUserChatId(Long chatId) {
        statisticsDao.clearStatisticForTheUserChatId(chatId);
    }

    @Override
    public Long getTotalCountScoreByChatId(Long chatId) {
        return statisticsDao.getTotalCountScoreByChatId(chatId);
    }

    @Override
    public List<Long> getListForCheckRepeats(Long chatId) {
        return statisticsDao.getListForCheckRepeats(chatId);
    }
}
