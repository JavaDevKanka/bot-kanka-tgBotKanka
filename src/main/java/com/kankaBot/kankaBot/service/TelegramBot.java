package com.kankaBot.kankaBot.service;

import com.vdurmont.emoji.EmojiParser;
import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.models.UserData.User;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final BasicFunctional basicFunctional;

    final BotConfig config;

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String HELP_TEXT = "Тут будет help текст";



    public TelegramBot(BotConfig config, AdsRepository adsRepository, UserRepository userRepository, BasicFunctional basicFunctional) {
        this.config = config;
        this.basicFunctional = basicFunctional;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Начать общаться с ботом"));
        listofCommands.add(new BotCommand("/mydata", "Получить ваши данные из базы"));
        listofCommands.add(new BotCommand("/deletedata", "Удалить ваши данные из базы"));
        listofCommands.add(new BotCommand("/help", "Информация по использованию бота"));
        listofCommands.add(new BotCommand("/settings", "Установить личные настройки"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
        this.userRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();

                for (User user : users) {
                    basicFunctional.prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {

                String anyMessage;
                if ("/start".equals(messageText)) {
                    basicFunctional.registerUser(update.getMessage());
                    basicFunctional.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                } else if ("/help".equals(messageText)) {
                    basicFunctional.prepareAndSendMessage(chatId, HELP_TEXT);
                } else if ("/register".equals(messageText) || "Регистрация".equals(messageText)) {
                    basicFunctional.register(chatId);
                } else if ("/mydata".equals(messageText) | "Показать ваши данные".equals(messageText)) {
                    basicFunctional.mydata(chatId);
                } else if ("/deletedata".equals(messageText) || "Удалить ваши данные".equals(messageText)) {
                    basicFunctional.deleteMyData(chatId);
                } else if ("Получить случайный вопрос".equals(messageText)) {
                    basicFunctional.startVictorine(chatId, "Тестирование началось");
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();


            if (callbackData.equals(YES_BUTTON)) {
                basicFunctional.registerUser(update.getCallbackQuery().getMessage());
                basicFunctional.executeEditMessageText("Вы успешно зарегистрированы", chatId, messageId);

            } else if (callbackData.equals(NO_BUTTON)) {
                basicFunctional.executeEditMessageText("Вы не зарегистрированы", chatId, messageId);
            } else if (callbackData.equals("/go")) {
                basicFunctional.executeEditMessageText("Тестирование началось", chatId,messageId);
            }
        }
    }

    public void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {

        }
    }





}
