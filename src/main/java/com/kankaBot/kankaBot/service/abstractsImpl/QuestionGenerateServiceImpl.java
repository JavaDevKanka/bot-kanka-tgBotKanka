package com.kankaBot.kankaBot.service.abstractsImpl;

import com.kankaBot.kankaBot.dao.abstracts.QuestionGenerateDao;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionGenerateServiceImpl extends ReadWriteServiceImpl<Question, Long> implements QuestionGenerateService {

    private final QuestionGenerateDao questionGenerateDao;

    public QuestionGenerateServiceImpl(QuestionGenerateDao questionGenerateDao) {
        super(questionGenerateDao);
        this.questionGenerateDao = questionGenerateDao;
    }
    public void createQuestion(String question, Answer answers, Boolean is_multianswer) {

    }
    @Override
    public List<Long> listIdQuestions() {
        return questionGenerateDao.listIdQuestions();
    }

    @Override
    public List<Long> listIdQuestionsBasic() {
        return questionGenerateDao.listIdQuestionsBasic();
    }

    @Override
    public List<Long> listIdQuestionsExceptions() {
        return questionGenerateDao.listIdQuestionsExceptions();
    }

    @Override
    public List<Long> listIdQuestionsOOP() {
        return questionGenerateDao.listIdQuestionsOOP();
    }

    @Override
    public List<Long> listIdQuestionsIO() {
        return questionGenerateDao.listIdQuestionsIO();
    }

    @Override
    public List<Long> listIdQuestionsStream() {
        return questionGenerateDao.listIdQuestionsStream();
    }


}
