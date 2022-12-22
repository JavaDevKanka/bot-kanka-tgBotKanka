package com.kankaBot.kankaBot.service.functions;

import com.kankaBot.kankaBot.models.Statistics;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.List;

public interface MarginFunc {
    EditMessageText executeEditMessageText(String text, long chatId, long messageId);
    SendMessage prepareAndSendMessage(long chatId, String textToSend);
    SendPoll getQuestion(Long chatId);
    List<String> mydata(long chatId);
    Statistics setStatisticsFromQuiz(PollAnswer pollAnswer);
}
