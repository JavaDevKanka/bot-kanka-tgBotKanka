package com.kankaBot.kankaBot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BasicFunctional {
    Message getTextFromMessage(Update update);
    void startVictorine(long chatId, String textToSend);
    void sendMessage(long chatId, String textToSend);
    void mydata(long chatId);
    void deleteMyData(long chatId);
    void sendRandomQuestion();

    void register(long chatId);

    void registerUser(Message msg);

    void startCommandReceived(long chatId, String name);

    void executeEditMessageText(String text, long chatId, long messageId);
    void executeMessage(SendMessage message);

    void prepareAndSendMessage(long chatId, String textToSend);
    void sendAds();

}
