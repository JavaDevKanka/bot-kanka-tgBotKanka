package com.kankaBot.kankaBot.service.functions;

import com.kankaBot.kankaBot.models.Statistics;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

public interface MarginFunc {
    SendMessage prepareAndSendMessage(long chatId, String textToSend);
    SendPoll getQuestion(Long chatId);
    Statistics setStatisticsFromQuiz(PollAnswer pollAnswer, Long chatId);
    void registerUser(Message msg);
    void saveQuestion(Long chatId);
    SendPhoto sendPhoto(Long chatId, String urlImage);
    void generatePoolIdsForQuestions(Long chatId, int quantityOfQuestions, String typeOfQuest);
}
