package com.kankaBot.kankaBot.service.functions;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface ReadFileQuestions {
    void quizFromTextFile(String file_name, String file_id) throws IOException;
    String saveStreamQuestionsFromFile(String filePath, String charset) throws IOException;
    void writeQuestionsToDBFromFile(String fileData);
}
