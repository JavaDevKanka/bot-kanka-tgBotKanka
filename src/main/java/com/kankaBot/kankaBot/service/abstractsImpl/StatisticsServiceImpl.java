package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.dao.abstracts.StatisticsDao;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.dto.ResultOfTest;
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
    @Override
    public Long getCountUserAnswers(Long chatId) {
        return statisticsDao.getCountUserAnswers(chatId);
    }

    @Override
    public int getCountRightForResultByChatId(Long chatId) {
        Long resultOfTest = statisticsDao.getCountRightForResultByChatId(chatId);
        int result = 0;
        if (resultOfTest < 3) {
            result = 2;
        } else if (resultOfTest < 5) {
            result = 3;
        } else if (resultOfTest < 8) {
            result = 4;
        } else if (resultOfTest <= 10) {
            result = 5;
        }
        return result;
    }

    @Override
    public List<ResultOfTest> getLoseAnswersForResultByChatId(Long chatId) {
        return statisticsDao.getLoseAnswersForResultByChatId(chatId);
    }

    @Override
    public List<Long> getUnansweredQuestionIdFromStatistics(Long chatId) {
        return statisticsDao.getUnansweredQuestionIdFromStatistics(chatId);
    }

    @Override
    public List<Long> getAnsweredQuestionIdFromStatistics(Long chatId) {
        return statisticsDao.getUnansweredQuestionIdFromStatistics(chatId);
    }

}
