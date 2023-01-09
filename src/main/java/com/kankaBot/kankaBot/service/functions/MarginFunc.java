package com.kankaBot.kankaBot.service.functions;

import com.kankaBot.kankaBot.models.Statistics;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.List;

public interface MarginFunc {
    SendMessage prepareAndSendMessage(long chatId, String textToSend);
    SendPoll getQuestion(Long chatId, List<Long> listOfQuestions);
    Statistics setStatisticsFromQuiz(PollAnswer pollAnswer);
    void registerUser(Message msg);
    void saveQuestion(Long chatId);
    SendPhoto sendPhoto(Long chatId, String urlImage);
}
