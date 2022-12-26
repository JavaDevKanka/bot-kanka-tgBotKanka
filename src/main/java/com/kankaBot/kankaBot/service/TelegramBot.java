package com.kankaBot.kankaBot.service;

import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.dao.repository.AdsRepository;
import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.*;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.abstracts.StatisticsService;
import com.kankaBot.kankaBot.service.functions.KeyboardsBot;
import com.kankaBot.kankaBot.service.functions.MarginFunc;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final AdsRepository adsRepository;

    private final QuestionGenerateService questionGenerateService;
    private final StatisticsService statisticsService;
    private final KeyboardsBot keyboardsBot;

    private final MessagesBufferService messagesBufferService;
    private final MarginFunc marginFunc;

    private final BotConfig config;

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String HELP_TEXT = "Тут будет help текст";
    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config, AdsRepository adsRepository, UserRepository userRepository, QuestionGenerateService questionGenerateService, StatisticsService statisticsService, KeyboardsBot keyboardsBot, MessagesBufferService messagesBufferService, MarginFunc marginFunc) {
        this.config = config;
        this.adsRepository = adsRepository;
        this.questionGenerateService = questionGenerateService;
        this.statisticsService = statisticsService;
        this.keyboardsBot = keyboardsBot;
        this.messagesBufferService = messagesBufferService;
        this.marginFunc = marginFunc;
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

        } else if (update.getMessage().hasDocument()) {
            String pathToSaveQuestionFile = "filesQuest\\" + update.getMessage().getDocument().getFileName();
            String fieldId = update.getMessage().getDocument().getFileId();
            quizFromTextFile(pathToSaveQuestionFile, fieldId);

        } else if (update.hasPollAnswer()) {
            PollAnswer pollAnswer = update.getPollAnswer();
            setStatisticsFromQuiz(pollAnswer);

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

            commands.put("Получить случайный вопрос", () -> executeQuestion(marginFunc.getQuestion(chatId)));

            commands.put("create", () -> createQuestion(chatId));

            commands.put("Начать игру", () -> startVictorine(chatId));

            if (commands.containsKey(update.getMessage().getText())) {
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

    public void setStatisticsFromQuiz(PollAnswer pollAnswer) {
        Statistics statistics = marginFunc.setStatisticsFromQuiz(pollAnswer);
        statisticsService.persist(statistics);
    }

    @SneakyThrows
    private void executeQuestion(SendPoll sendPoll) {
        execute(sendPoll);
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
        } else if (callbackData.equals("/saveNot")) {
            prepareAndSendMessage(chatId, "Вопрос не сохранен");
            messagesBufferService.deleteAll(messagesBufferService.getAll());
        } else if (callbackData.equals("/score")) {
            prepareAndSendMessage(chatId, "Суммарное количество правильных ответов " + statisticsService.getTotalCountScoreByChatId(chatId));
        } else if (callbackData.equals("/clearstat")) {
            statisticsService.clearStatisticForTheUserChatId(chatId);
            prepareAndSendMessage(chatId, "Выполнена очистка личного счета");
        }
    }


    public void quizFromTextFile(String file_name, String file_id) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + "5922259665:AAFOKzJCcltfkM7oL07x3U9IYsOetZGOWOQ" + "/getFile?file_id=" + file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + "5922259665:AAFOKzJCcltfkM7oL07x3U9IYsOetZGOWOQ" + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(file_name);
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        System.out.println("Uploaded!");
    }

    // Получение текста из файла без потерь "filesQuest\\questionse.txt",
    public String saveStreamQuestionsFromFile(String filePath, String charset) throws IOException {
        java.io.InputStream is = new java.io.FileInputStream(filePath);
        try {
            final int bufsize = 4096;
            int available = is.available();
            byte[] data = new byte[Math.max(available, bufsize)];
            int used = 0;
            while (true) {
                if (data.length - used < bufsize) {
                    byte[] newData = new byte[data.length << 1];
                    System.arraycopy(data, 0, newData, 0, used);
                    data = newData;
                }
                int got = is.read(data, used, data.length - used);
                if (got <= 0) break;
                used += got;
            }
            return charset != null ? new String(data, 0, used, charset)
                    : new String(data, 0, used);
        } finally {
            is.close();
        }
    }


    @SneakyThrows
    public void saveQuestion(Long chatId) {
        List<String> listBuffer = new ArrayList<>(messagesBufferService.answerList());
        messagesBufferService.deleteAll(messagesBufferService.getAll());
        Set<Answer> answers = new HashSet<>();
        Question question = new Question();
        Long answerCounter = 0L;
        for (String buffer : listBuffer) {
            if (buffer.startsWith("!q")) {
                question.setQuestion(buffer.substring(2));
                if (buffer.contains("*")) {
                    question.setExplanation(buffer.substring(buffer.indexOf("*") + 1));
                    question.setQuestion(buffer.substring(buffer.indexOf("!q") + 2, buffer.indexOf("*")));
                }

            } else if (buffer.startsWith("!a")) {
                Answer answer = new Answer();
                answer.setAnswer(buffer.substring(2));
                answer.setSeqnumber(answerCounter);
                answerCounter++;
                if (buffer.contains("!r")) {
                    answer.setIs_right(true);
                    answer.setAnswer(buffer.substring(buffer.indexOf("!a") + 2, buffer.indexOf("!r")));
                } else {
                    answer.setIs_right(false);
                }
                answers.add(answer);
            }
        }
        if (answers.size() < 2 | answers.size() > 10) {
            messagesBufferService.deleteAll(messagesBufferService.getAll());
            prepareAndSendMessage(chatId, "Ответов меньше 2 или больше 10, буфер очищен, введите заново");
        } else {
            int count = 0;
            for (Answer answer : answers) {
                if (answer.getIs_right().equals(true)) {
                    count++;
                }
            }
            if (count > 1 | count < 1) {
                prepareAndSendMessage(chatId, "Правильный ответ должен быть только один, буфер очищен");
                messagesBufferService.deleteAll(messagesBufferService.getAll());
            } else {
                question.setAnswers(answers);
            }
        }
        if (question.getQuestion().isEmpty()) {
            prepareAndSendMessage(chatId, "Вопрос пуст, буфер очищен, введите вопрос и ответы заново");
            messagesBufferService.deleteAll(messagesBufferService.getAll());
        } else {
            questionGenerateService.persist(question);
            prepareAndSendMessage(chatId, "Вопрос добавлен!");
            messagesBufferService.deleteAll(messagesBufferService.getAll());
        }
    }

    public void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = marginFunc.executeEditMessageText(text, chatId, messageId);
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

    public void createQuestion(long chatId) {
        SendMessage message = keyboardsBot.createQuestion(chatId);
        executeMessage(message);

    }

    public void startVictorine(long chatId) {
        SendMessage message = keyboardsBot.startVictorine(chatId);
        executeMessage(message);
    }

    public void register(long chatId) {
        SendMessage message = keyboardsBot.register(chatId);
        executeMessage(message);
    }

    public void mainMenu(long chatId) {
        SendMessage message = keyboardsBot.mainMenu(chatId);
        executeMessage(message);
    }

    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = marginFunc.prepareAndSendMessage(chatId, textToSend);
        executeMessage(message);
    }

    public void mydata(long chatId) {
        List<String> dataList = marginFunc.mydata(chatId);
        Optional<User> userData = userRepository.findById(chatId);

        if (userData.isPresent()) {
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
