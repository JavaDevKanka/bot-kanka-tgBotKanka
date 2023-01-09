package com.kankaBot.kankaBot.service;

import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.Ads;
import com.kankaBot.kankaBot.models.MessagesBuffer;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.User;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.abstracts.StatisticsService;
import com.kankaBot.kankaBot.service.functions.KeyboardsBot;
import com.kankaBot.kankaBot.service.functions.MarginFunc;
import com.kankaBot.kankaBot.service.functions.FileOperations;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    private final StatisticsService statisticsService;
    private final KeyboardsBot keyboardsBot;
    private final FileOperations fileOperations;
    private final MessagesBufferService messagesBufferService;
    private final MarginFunc marginFunc;
    private final QuestionGenerateService questionGenerateService;
    private final BotConfig config;

    private static final String HELP_TEXT = "Тут будет help текст";
    private static final String ERROR_TEXT = "Error occurred: ";
    private static final String YES_BUTTON = "YES_BUTTON";
    private static final String NO_BUTTON = "NO_BUTTON";
    int counterGlobal = 0;

    public TelegramBot(BotConfig config,
                       AdsRepository adsRepository,
                       UserRepository userRepository,
                       StatisticsService statisticsService,
                       KeyboardsBot keyboardsBot,
                       FileOperations fileOperations,
                       MessagesBufferService messagesBufferService,
                       MarginFunc marginFunc, QuestionGenerateService questionGenerateService) {
        this.config = config;
        this.adsRepository = adsRepository;
        this.statisticsService = statisticsService;
        this.keyboardsBot = keyboardsBot;
        this.fileOperations = fileOperations;
        this.messagesBufferService = messagesBufferService;
        this.marginFunc = marginFunc;
        this.questionGenerateService = questionGenerateService;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Начать общаться с ботом"));
        listofCommands.add(new BotCommand("/registration", "Регистрация"));
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
            setStatisticsFromQuiz(pollAnswer);
            globalQuestionVictorineSelector(pollAnswer.getUser().getId(), questionGenerateService.listIdQuestions());

        } else if (update.getMessage().hasDocument()) {
            String pathToSaveQuestionFile = "filesQuest\\" + update.getMessage().getDocument().getFileName();
            String fieldId = update.getMessage().getDocument().getFileId();
            fileOperations.quizFromTextFile(pathToSaveQuestionFile, fieldId);
            String stringFromFile = fileOperations.saveStreamQuestionsFromFile("filesQuest\\" + update.getMessage().getDocument().getFileName(), "UTF-8");
            fileOperations.writeQuestionsToDBFromFile(stringFromFile);
        } else if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            Map<String, Runnable> commands = new HashMap<>();

            commands.put("/start", () -> executeSendMessage(keyboardsBot.mainMenu(chatId)));
            commands.put("/registration", () -> executeSendMessage(keyboardsBot.register(chatId)));
            commands.put("/help", () -> prepareAndSendMessage(chatId, HELP_TEXT));
            commands.put("create", () -> executeSendMessage(keyboardsBot.createQuestion(chatId)));
            commands.put("Тестирование по теме", () -> executeSendMessage(keyboardsBot.startVictorine(chatId)));

            if (commands.containsKey(update.getMessage().getText())) {
                commands.get(messageText).run();

            } else if (messageText.equals("Тест по случайным темам")) {
                counterGlobal = 0;
                globalQuestionVictorineSelector(chatId, questionGenerateService.listIdQuestions());

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

    @SneakyThrows
    public void globalQuestionVictorineSelector(Long chatId, List<Long> listIds) {

        if (counterGlobal < 10) {
            executeQuestion(marginFunc.getQuestion(chatId, listIds));
            counterGlobal++;
        } else {
            fileOperations.resultImage(chatId);
            executeImage(marginFunc.sendPhoto(chatId, "./imageResult/end.png"));
            counterGlobal = 0;
        }
    }


    @SneakyThrows
    public void handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (callbackData.equals(YES_BUTTON)) {
            marginFunc.registerUser(update.getCallbackQuery().getMessage());
            prepareAndSendMessage(chatId, "Вы успешно зарегистрированы");
        } else if (callbackData.equals(NO_BUTTON)) {
            prepareAndSendMessage(chatId, "Вы не зарегистрированы");
        } else if (callbackData.equals("/saveQuestion") && !messagesBufferService.getAll().isEmpty()) {
            marginFunc.saveQuestion(chatId);
        } else if (callbackData.equals("/saveNot")) {
            prepareAndSendMessage(chatId, "Вопрос не сохранен");
            messagesBufferService.deleteAll(messagesBufferService.getAll());
        } else if (callbackData.equals("/go")) {
            executeSendMessage(keyboardsBot.selectThemeVictorine(chatId));
        } else if (callbackData.equals("/score")) {
            prepareAndSendMessage(chatId, "Суммарное количество правильных ответов " + statisticsService.getTotalCountScoreByChatId(chatId));
        } else if (callbackData.equals("/clearstat")) {
            statisticsService.clearStatisticForTheUserChatId(chatId);
            prepareAndSendMessage(chatId, "Выполнена очистка личного счета");
        }
    }


    @SneakyThrows
    public void setStatisticsFromQuiz(PollAnswer pollAnswer) {
        Statistics statistics = marginFunc.setStatisticsFromQuiz(pollAnswer);
        statisticsService.persist(statistics);
    }

    @SneakyThrows
    private void executeQuestion(SendPoll sendPoll) {
        execute(sendPoll);
    }


    @SneakyThrows
    private void executeSendMessage(SendMessage message) {
        execute(message);
    }

    @SneakyThrows
    private void executeImage(SendPhoto sendPhoto) {
        execute(sendPhoto);
    }


    @SneakyThrows
    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = marginFunc.prepareAndSendMessage(chatId, textToSend);
        executeSendMessage(message);
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
