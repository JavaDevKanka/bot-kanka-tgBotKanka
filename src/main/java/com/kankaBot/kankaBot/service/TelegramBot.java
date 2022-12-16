package com.kankaBot.kankaBot.service;

import com.kankaBot.kankaBot.models.Ads;
import com.vdurmont.emoji.EmojiParser;
import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.models.UserData.User;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    private final QuestionGenerateService basicFunctional;

    final BotConfig config;

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String HELP_TEXT = "Тут будет help текст";
    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config, AdsRepository adsRepository, UserRepository userRepository, QuestionGenerateService basicFunctional) {
        this.config = config;
        this.adsRepository = adsRepository;
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

    public Message getTextFromMessage(Update update) {
        Message m = new Message();
        m = update.getMessage();
        return m;
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
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {

                if ("/start".equals(messageText)) {
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                } else if ("/help".equals(messageText)) {
                    prepareAndSendMessage(chatId, HELP_TEXT);
                } else if ("/register".equals(messageText) || "Регистрация".equals(messageText)) {
                    register(chatId);
                } else if ("/mydata".equals(messageText) | "Показать ваши данные".equals(messageText)) {
                    mydata(chatId);
                } else if ("/deletedata".equals(messageText) || "Удалить ваши данные".equals(messageText)) {
                    deleteMyData(chatId);
                } else if ("Получить случайный вопрос".equals(messageText)) {
                    startVictorine(chatId);
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();


            if (callbackData.equals(YES_BUTTON)) {
                registerUser(update.getCallbackQuery().getMessage());
                executeEditMessageText("Вы успешно зарегистрированы", chatId, messageId);

            } else if (callbackData.equals(NO_BUTTON)) {
                executeEditMessageText("Вы не зарегистрированы", chatId, messageId);
            } else if (callbackData.equals("/go")) {
                sendRandomQuestion(chatId);
            }
        }
    }


    public void sendRandomQuestion(long chatId) throws TelegramApiException {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setQuestion("Вопрос");
        sendPoll.setAllowMultipleAnswers(true);
        sendPoll.setIsAnonymous(false);
        List<String> list = new ArrayList<>();
        list.add("Ответ1");
        list.add("Ответ2");
        list.add("Ответ3");
        sendPoll.setOptions(list);

        try {
            execute(sendPoll);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }


    public void startVictorine(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Тестирование Java");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        List<InlineKeyboardButton> fourthRow = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Начать тестирование");
        inlineKeyboardButton1.setCallbackData("/go");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Мой счет");
        inlineKeyboardButton2.setCallbackData("/score");

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Топ 10");
        inlineKeyboardButton3.setCallbackData("/top10");

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Помощь");
        inlineKeyboardButton4.setCallbackData("/help");

        firstRow.add(inlineKeyboardButton1);
        secondRow.add(inlineKeyboardButton2);
        thirdRow.add(inlineKeyboardButton3);
        fourthRow.add(inlineKeyboardButton4);
        rowsInLine.add(firstRow);
        rowsInLine.add(secondRow);
        rowsInLine.add(thirdRow);
        rowsInLine.add(fourthRow);
        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);

        executeMessage(message);



    }

    public void register(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Хотите ли выполнить регистрацию?");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Регистрация автоматически");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("Отмена регистрации");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Получить случайный вопрос");

        keyboardRows.add(row);


        row = new KeyboardRow();
        row.add("Регистрация");
        row.add("Показать ваши данные");
        row.add("Удалить ваши данные");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }


    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    public void mydata(long chatId) {
        sendMessage(chatId, "Ваши личные данные");
        StringBuilder readyMessageWithData = new StringBuilder();
        List<String> dataList = new ArrayList<>();
        Optional<User> userData = userRepository.findById(chatId);
        dataList.add("Ваш username - " + userData.get().getUserName());
        dataList.add("Ваше имя - " + userData.get().getFirstName());
        dataList.add("Ваша фамилия - " + userData.get().getLastName());
        dataList.add("Дата регистрации - " + userData.get().getRegisteredAt().toString());
        dataList.add("Чат ID - " + userData.get().getChatId().toString());



        System.out.println(userData);

        for (String s : dataList) {
            readyMessageWithData.append(s).append("\n");
        }
        sendMessage(chatId, readyMessageWithData.toString());
    }

    public void registerUser(Message msg) {

        if (userRepository.findById(msg.getChatId()).isEmpty()) {

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    @Scheduled(cron = "${cron.scheduler}")
    public void sendAds() {

        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }

    public void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Эй, " + name + ", Привет))!" + " :expressionless:");
        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    public void deleteMyData(long chatId) {
        userRepository.deleteById(chatId);
    }

}
