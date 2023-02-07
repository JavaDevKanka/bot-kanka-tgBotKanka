package com.kankaBot.kankaBot.service.functionsImpl;

import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.PoolQuestionsBuffer;
import com.kankaBot.kankaBot.models.Question;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.User;
import com.kankaBot.kankaBot.service.abstracts.AnswerVariablesService;
import com.kankaBot.kankaBot.service.abstracts.MessagesBufferService;
import com.kankaBot.kankaBot.service.abstracts.PoolQuestionsBufferService;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.functions.MarginFunc;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


@Component
public class MarginFuncImpl implements MarginFunc {

    private final UserRepository userRepository;
    private final QuestionGenerateService questionGenerateService;
    private final AnswerVariablesService answerVariablesService;
    private final MessagesBufferService messagesBufferService;
    private final PoolQuestionsBufferService poolQuestionsBufferService;

    private Integer correctBufferPollQuiz = 0;
    private Long questionIdToStatistic = 0L;

    public MarginFuncImpl(UserRepository userRepository,
                          QuestionGenerateService questionGenerateService,
                          AnswerVariablesService answerVariablesService,
                          MessagesBufferService messagesBufferService, PoolQuestionsBufferService poolQuestionsBufferService) {
        this.userRepository = userRepository;
        this.questionGenerateService = questionGenerateService;
        this.answerVariablesService = answerVariablesService;
        this.messagesBufferService = messagesBufferService;
        this.poolQuestionsBufferService = poolQuestionsBufferService;
    }

    public SendMessage prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        return message;
    }

    public SendPhoto sendPhoto(Long chatId, String urlImage) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(urlImage)));
        return sendPhoto;
    }

    @Override
    public void generatePoolIdsForQuestions(Long chatId, int quantityOfQuestions, String typeOfQuest) {
        List<Long> poolQuestionIds = new ArrayList<>();
        switch (typeOfQuest) {
            case "all":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestions());
                break;
            case "IO":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestionsIO());
            case "basic":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestionsBasic());
                break;
            case "stream":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestionsStream());
                break;
            case "OOP":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestionsOOP());
                break;
            case "exceptions":
                poolQuestionIds.addAll(questionGenerateService.listIdQuestionsExceptions());
                break;

        }
        Collections.shuffle(poolQuestionIds);
        for (int i = 0; i < quantityOfQuestions; i++) {
            PoolQuestionsBuffer questionsBuffer = new PoolQuestionsBuffer();
            questionsBuffer.setChatId(chatId);
            questionsBuffer.setQuestionId(poolQuestionIds.get(i));
            poolQuestionsBufferService.persist(questionsBuffer);
        }
    }
    @Override
    public SendPoll getQuestion(Long chatId) {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setIsAnonymous(false);
        sendPoll.setType("quiz");
        Random random = new Random();
        var randomran = poolQuestionsBufferService.getUnusedIdsQuestion(chatId)
                .get(random.nextInt(poolQuestionsBufferService
                        .getUnusedIdsQuestion(chatId).size()));
        List<String> options = new ArrayList<>(answerVariablesService.getAnswersByQuestId(randomran));
        int isRightcount = 0;
        sendPoll.setExplanation(questionGenerateService.getById(randomran).get().getExplanation());
        sendPoll.setQuestion(questionGenerateService.getById(randomran).get().getQuestion());
        sendPoll.setOptions(options);
        sendPoll.setDisableNotification(true);

        questionIdToStatistic = randomran;
        for (Answer i : answerVariablesService.getAnswerObjByQuestId(randomran)) {
            if (i.getIs_right().equals(true)) {
                sendPoll.setCorrectOptionId(isRightcount);
                correctBufferPollQuiz = sendPoll.getCorrectOptionId();
            }
            isRightcount++;
        }
        poolQuestionsBufferService.removeAnsweredFromBuffer(chatId, questionIdToStatistic);
        return sendPoll;
    }

    @Override
    public Statistics setStatisticsFromQuiz(PollAnswer pollAnswer, Long chatId) {
        Statistics statistics = new Statistics();
        statistics.setChatId(chatId);
        statistics.setQuizUserAnswer(pollAnswer.getOptionIds().get(0) + 1);
        statistics.setCorrectQuizAnswer(correctBufferPollQuiz + 1);
        statistics.setQuestionId(questionIdToStatistic);
        statistics.setQuestionType(questionGenerateService.getById(questionIdToStatistic).get().getTopic());
        return statistics;
    }


    @Override
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
        }
    }

    @Override
    @SneakyThrows
    public void saveQuestion(Long chatId) {
        List<String> listBuffer = new ArrayList<>(messagesBufferService.answerList());
        messagesBufferService.deleteAll(messagesBufferService.getAll());
        Set<Answer> answers = new HashSet<>();
        Question question = new Question();
        Long answerCounter = 1L;
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
                    answer.setSeqnumber(answerCounter);
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


}