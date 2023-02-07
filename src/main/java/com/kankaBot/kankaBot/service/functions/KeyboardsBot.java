package com.kankaBot.kankaBot.service.functions;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public interface KeyboardsBot {
    SendMessage createQuestion(long chatId);
    SendMessage startVictorine(long chatId);
    SendMessage register(long chatId);
    SendMessage mainMenu(long chatId);
    SendMessage selectThemeVictorine(long chatId);
    SendMessage nextQuestion(long chatId);
    SendMessage getResult(long chatId);
}
