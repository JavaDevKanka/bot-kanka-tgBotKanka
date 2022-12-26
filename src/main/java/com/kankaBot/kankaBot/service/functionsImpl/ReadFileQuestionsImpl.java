package com.kankaBot.kankaBot.service.functionsImpl;

import com.kankaBot.kankaBot.models.Question;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.functions.ReadFileQuestions;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;


@Component
public class ReadFileQuestionsImpl implements ReadFileQuestions {

    private final QuestionGenerateService questionGenerateService;

    public ReadFileQuestionsImpl(QuestionGenerateService questionGenerateService) {
        this.questionGenerateService = questionGenerateService;
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

    public String saveStreamQuestionsFromFile(String filePath, String charset) throws IOException {
        try (java.io.InputStream is = new java.io.FileInputStream(filePath)) {
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
        }
    }

    public void writeQuestionsToDBFromFile(String fileData) {
        List<String> list = new ArrayList<>(List.of(fileData.split("-")));
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Question question = new Question();
            if (list.get(i).contains("!q")) {
                question.setQuestion(list.get(i).substring(list.get(i).indexOf("!q") + 2, list.get(i).indexOf(";")));
                if (list.get(i).contains("!e")) {
                    question.setExplanation(list.get(i).substring(list.get(i).indexOf("!e") + 2, list.get(i).indexOf(",")));
                }
                questionGenerateService.persist(question);
            }


        }
    }
}


