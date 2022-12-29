package com.kankaBot.kankaBot.service.functionsImpl;

import com.kankaBot.kankaBot.dao.repository.UserRepository;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Statistics;
import com.kankaBot.kankaBot.models.User;
import com.kankaBot.kankaBot.service.abstracts.AnswerVariablesService;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.abstracts.StatisticsService;
import com.kankaBot.kankaBot.service.functions.MarginFunc;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class MarginFuncImpl implements MarginFunc {

    private final UserRepository userRepository;
    private final StatisticsService statisticsService;

    private final QuestionGenerateService questionGenerateService;

    private final AnswerVariablesService answerVariablesService;

    private Integer correctBufferPollQuiz = 0;
    private Long questionIdToStatistic = 0L;


    public MarginFuncImpl(UserRepository userRepository, StatisticsService statisticsService, QuestionGenerateService questionGenerateService, AnswerVariablesService answerVariablesService) {
        this.userRepository = userRepository;
        this.statisticsService = statisticsService;
        this.questionGenerateService = questionGenerateService;
        this.answerVariablesService = answerVariablesService;
    }

    public EditMessageText executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);
        return message;
    }

    public SendMessage prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        return message;
    }

    public List<String> mydata(long chatId) {
        List<String> dataList = new ArrayList<>();
        Optional<User> userData = userRepository.findById(chatId);

        dataList.add("Ваш username - " + userData.get().getUserName());
        dataList.add("Ваше имя - " + userData.get().getFirstName());
        dataList.add("Ваша фамилия - " + userData.get().getLastName());
        dataList.add("Дата регистрации - " + userData.get().getRegisteredAt().toString());
        dataList.add("Чат ID - " + userData.get().getChatId().toString());
        return dataList;
    }

    public SendPoll getQuestion(Long chatId) {
        SendPoll sendPoll = new SendPoll();
        sendPoll.setChatId(String.valueOf(chatId));
        sendPoll.setIsAnonymous(false);
        sendPoll.setType("quiz");
        Random random = new Random();
        List<Long> poolRandoms = new ArrayList<>(questionGenerateService.listIdQuestions());
        List<Long> existsQuestions = new ArrayList<>(statisticsService.getListForCheckRepeats(chatId));
        for (Long i : existsQuestions) {
            poolRandoms.remove(i);
        }
        var randomran = poolRandoms.get(random.nextInt(poolRandoms.size()));
        List<String> options = new ArrayList<>(answerVariablesService.getAnswersByQuestId(randomran));
        int isRightcount = 0;
        sendPoll.setExplanation(questionGenerateService.getById(randomran).get().getExplanation());
        sendPoll.setOptions(options);
        sendPoll.setQuestion(questionGenerateService.getById(randomran).get().getQuestion());
        questionIdToStatistic = randomran;
        for (Answer i : answerVariablesService.getAnswerObjByQuestId(randomran)) {
            if (i.getIs_right().equals(true)) {
                sendPoll.setCorrectOptionId(isRightcount);
                correctBufferPollQuiz = sendPoll.getCorrectOptionId();
            }
            isRightcount++;
        }
        return sendPoll;
    }

    public Statistics setStatisticsFromQuiz(PollAnswer pollAnswer) {
        Statistics statistics = new Statistics();
        statistics.setChatId(pollAnswer.getUser().getId());
        statistics.setQuizUserAnswer(pollAnswer.getOptionIds().get(0));
        statistics.setCorrectQuizAnswer(correctBufferPollQuiz);
        statistics.setQuestionId(questionIdToStatistic);
        return statistics;
    }
}