package com.kankaBot.kankaBot.service;

import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.*;
import com.kankaBot.kankaBot.service.abstracts.AnswerVariablesService;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
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
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    private final QuestionGenerateService questionGenerateService;
    private final AnswerVariablesService answerVariablesService;

    private final MessagesBufferService messagesBufferService;


    final BotConfig config;

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String HELP_TEXT = "Тут будет help текст";
    static final String ERROR_TEXT = "Error occurred: ";


    public TelegramBot(BotConfig config, AdsRepository adsRepository, UserRepository userRepository, QuestionGenerateService questionGenerateService, AnswerVariablesService answerVariablesService, MessagesBufferService messagesBufferService) {
        this.config = config;
        this.adsRepository = adsRepository;
        this.questionGenerateService = questionGenerateService;
        this.answerVariablesService = answerVariablesService;
        this.messagesBufferService = messagesBufferService;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Начать общаться с ботом"));
        listofCommands.add(new BotCommand("/registration", "Регистрация"));
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

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        MessagesBuffer messagesBuffer = new MessagesBuffer();
        if (update.hasCallbackQuery()) {
            handleCallback(update);

        } else if (update.hasPollAnswer()) {
            PollAnswer pollAnswer = update.getPollAnswer();
            String pollId = pollAnswer.getPollId();
            Long chatUserId = pollAnswer.getUser().getId();
            List<Integer> optionIds = pollAnswer.getOptionIds();
            setStatisticsFromQuiz(pollId, chatUserId, optionIds);
        } else if (update.getMessage().hasText()) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            Map<String, Runnable> commands = new HashMap<>();
            commands.put("/start", () -> mainMenu(chatId));

            commands.put("/registration", () -> register(chatId));
            commands.put("Регистрация", () -> register(chatId));

            commands.put("/mydata", () -> mydata(chatId));
            commands.put("Показать ваши данные", () -> mydata(chatId));

            commands.put("/deletedata", () -> deleteMyData(chatId));
            commands.put("Удалить ваши данные", () -> deleteMyData(chatId));

            commands.put("/help", () -> prepareAndSendMessage(chatId, HELP_TEXT));

            commands.put("Получить случайный вопрос", () -> getQuestion(chatId));

            commands.put("create", () -> createQuestion(chatId));

            if (update.getMessage().isUserMessage() & commands.containsKey(update.getMessage().getText())) {
                commands.get(messageText).run();
            } else {

                messagesBuffer.setMessage(update.getMessage().getText());
                messagesBuffer.setChatId(update.getMessage().getChatId());
                messagesBuffer.setMessageId(Long.valueOf(update.getMessage().getMessageId()));

                messagesBufferService.persist(messagesBuffer);

                if (messagesBufferService.getAll().size() > 30) {
                    messagesBufferService.deleteAll(messagesBufferService.getAll());
                }
            }
        }
    }


    public void setStatisticsFromQuiz(String pollId, Long chatUserId, List<Integer> optionIds) {
        System.out.println(pollId);
        System.out.println(chatUserId);
        System.out.println(optionIds);


    }

    public void handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (callbackData.equals(YES_BUTTON)) {
            registerUser(update.getCallbackQuery().getMessage());
            executeEditMessageText("Вы успешно зарегистрированы", chatId, messageId);
        } else if (callbackData.equals(NO_BUTTON)) {
            executeEditMessageText("Вы не зарегистрированы", chatId, messageId);
        } else if (callbackData.equals("/saveQuestion") && !messagesBufferService.getAll().isEmpty()) {
            saveQuestion(chatId);
            messagesBufferService.deleteAll(messagesBufferService.getAll());
        } else if (callbackData.equals("/saveNot") && !messagesBufferService.getAll().isEmpty()) {
            prepareAndSendMessage(chatId, "Вопрос не сохранен");
            messagesBufferService.deleteAll(messagesBufferService.getAll());
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

    public void getQuestion(Long chatId) {
        Random random = new Random();
        List<Long> poolRandoms = new ArrayList<>(questionGenerateService.listIdQuestions());
        var randomran = poolRandoms.get(random.nextInt(poolRandoms.size()));
        List<String> options = new ArrayList<>(answerVariablesService.getAnswersByQuestId(randomran));
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setIsAnonymous(false);
        sendPoll.setType("quiz");
        sendPoll.setExplanation(questionGenerateService.getById(randomran).get().getExplanation());
        sendPoll.setOptions(options);
        int isRightcount = 0;
        sendPoll.setQuestion(questionGenerateService.getById(randomran).get().getQuestion());

        for (Answer i : answerVariablesService.getAnswerObjByQuestId(randomran)) {
            if (i.getIs_right()) {
                sendPoll.setCorrectOptionId(isRightcount);
            }
            isRightcount++;
        }
        try {
            execute(sendPoll);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void createQuestion(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Введите вопрос (начиная с \"!q\", и ответы через \"!a\"," +
                " для указания правильного ответа написать \"!r\" после вопроса," +
                " если нужно добавить уточнение при неправильном ответе, то добавить \"*\" " +
                " а затем нажмите \"Сохранить\" \"ВОПРОСЫ И ОТВЕТЫ НЕ ДОЛЖНЫ БЫТЬ БОЛЬШЕ 100 СИМВОЛОВ," +
                " А ТАК-ЖЕ ДИАПАЗОН ОТВЕТОВ ОТ 2 ДО 10 \"");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Сохранить");
        yesButton.setCallbackData("/saveQuestion");

        var noButton = new InlineKeyboardButton();

        noButton.setText("Не сохранять");
        noButton.setCallbackData("/saveNot");

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);

    }

    @SneakyThrows
    public void saveQuestion(Long chatId) {
        List<String> listBuffer = new ArrayList<>(messagesBufferService.answerList());

        for (String m : listBuffer) {
            if (m.startsWith("!q") & !m.isEmpty()) {
                Question question = new Question();
                if (m.contains("*")) {
                    question.setExplanation(m.substring(m.indexOf('*') + 1));
                }
                Set<Answer> answers = new HashSet<>();
                for (String answersvar : listBuffer) {
                    if (answersvar.startsWith("!a")) {
                        Answer answer = new Answer();

                        if (answersvar.contains("!r")) {
                            answer.setIs_right(true);
                        } else {
                            messagesBufferService.deleteAll(messagesBufferService.getAll());
                            prepareAndSendMessage(chatId, "Не указан правильный ответ!");
                        }
                        answer.setAnswer(answersvar.substring(2).replace("!r", ""));
                        answers.add(answer);
                    }
                }
                question.setAnswers(answers);
                if (answers.size() >= 2 & answers.size() <= 10) {
                    if (m.contains("!q")) {
                        question.setQuestion(m.substring(2));
                    } else {
                        messagesBufferService.deleteAll(messagesBufferService.getAll());
                        prepareAndSendMessage(chatId, "Вопрос не введен, Буфер очищен.");
                        break;
                    }
                    questionGenerateService.persist(question);
                    prepareAndSendMessage(chatId, "Вопрос сохранен в БД");
                } else {
                    messagesBufferService.deleteAll(messagesBufferService.getAll());
                    prepareAndSendMessage(chatId, "Количество вопросов меньше 2 или больше 10, введите вопрос и ответы заново");
                    break;

                }
            } else {
                messagesBufferService.deleteAll(messagesBufferService.getAll());
                prepareAndSendMessage(chatId, "Вопрос не добавлен!");
                break;
            }
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

    public void mainMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Главное меню");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setCallbackData("/createQuestion");

        KeyboardRow row = new KeyboardRow();
        row.add("Получить случайный вопрос");
        row.add("Начать игру");


        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Регистрация");
        row.add("Показать ваши данные");
        row.add("Удалить ваши данные");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setInputFieldPlaceholder("Введите");

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
        List<String> dataList = new ArrayList<>();
        Optional<User> userData = userRepository.findById(chatId);

        if (userData.isPresent()) {
            dataList.add("Ваш username - " + userData.get().getUserName());
            dataList.add("Ваше имя - " + userData.get().getFirstName());
            dataList.add("Ваша фамилия - " + userData.get().getLastName());
            dataList.add("Дата регистрации - " + userData.get().getRegisteredAt().toString());
            dataList.add("Чат ID - " + userData.get().getChatId().toString());

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));

            for (String s : dataList) {
                message.setText(s);
                executeMessage(message);
            }
        } else {
            prepareAndSendMessage(chatId, "Вы не зарегистрированы!");
        }
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


    public void deleteMyData(long chatId) {
        prepareAndSendMessage(chatId, "Данные удалены из БД");
        userRepository.deleteById(chatId);
    }


}
