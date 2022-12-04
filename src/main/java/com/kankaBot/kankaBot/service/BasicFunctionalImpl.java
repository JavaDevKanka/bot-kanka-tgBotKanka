package com.kankaBot.kankaBot.service;

import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.Ads;
import com.kankaBot.kankaBot.models.UserData.User;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
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
public class BasicFunctionalImpl implements BasicFunctional {


    private final AdsRepository adsRepository;
    private final UserRepository userRepository;


    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";



    static final String ERROR_TEXT = "Error occurred: ";


    public BasicFunctionalImpl(AdsRepository adsRepository, UserRepository userRepository) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
    }

    public Message getTextFromMessage(Update update) {
        Message m = new Message();
        m = update.getMessage();
        return m;
    }


    public void startVictorine(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

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

        for (String s : dataList) {
            readyMessageWithData.append(s).append("\n");
        }
        sendMessage(chatId, readyMessageWithData.toString());


    }

    public void deleteMyData(long chatId) {
        userRepository.deleteById(chatId);
    }


    public void sendRandomQuestion() {

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

    public void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Эй, " + name + ", Привет))!" + " :expressionless:");
        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
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

    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
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
}
