package com.kankaBot.kankaBot.service.functionsImpl;

import com.kankaBot.kankaBot.config.BotConfig;
import com.kankaBot.kankaBot.models.Answer;
import com.kankaBot.kankaBot.models.Question;
import com.kankaBot.kankaBot.models.dto.ResultOfTest;
import com.kankaBot.kankaBot.service.abstracts.QuestionGenerateService;
import com.kankaBot.kankaBot.service.abstracts.StatisticsService;
import com.kankaBot.kankaBot.service.functions.FileOperations;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class FileOperationsImpl implements FileOperations {

    private final QuestionGenerateService questionGenerateService;
    private final BotConfig botConfig;
    private final StatisticsService statisticsService;


    public FileOperationsImpl(QuestionGenerateService questionGenerateService, BotConfig botConfig, StatisticsService statisticsService) {
        this.questionGenerateService = questionGenerateService;
        this.botConfig = botConfig;
        this.statisticsService = statisticsService;
    }

    public void quizFromTextFile(String file_name, String file_id) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botConfig.getToken() + "/getFile?file_id=" + file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + botConfig.getToken() + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(file_name);
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
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
        List<String> list = new ArrayList<>(List.of(fileData.split("@")));
        for (String s : list) {
            List<String> subStrList = new ArrayList<>(List.of(s.split("\r")));
            Set<Answer> answers = new HashSet<>();
            Question question = new Question();
            question.setQuestion(subStrList.get(0));
            question.setTopic(subStrList.get(1));
            for (int i = 2; i < subStrList.size() - 1; i++) {
                Answer answer = new Answer();
                if (subStrList.get(i).contains("$")) {
                    answer.setIs_right(true);
                } else {
                    answer.setIs_right(false);
                }
                answer.setAnswer(subStrList.get(i).replace('$', ' '));
                answer.setSeqnumber((long) i - 1);
                answers.add(answer);
            }
            question.setAnswers(answers);
            questionGenerateService.persist(question);
        }
    }

    @SneakyThrows
    public void resultImage(Long chatId) {
        String grade = "???????? ???????????? - " + statisticsService.getCountRightForResultByChatId(chatId);
        List<ResultOfTest> resultOfTests = statisticsService.getLoseAnswersForResultByChatId(chatId);
        File file = new File("./imageResult/start.png");
        BufferedImage image = ImageIO.read(file);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.setBackground(Color.DARK_GRAY);
        g.setFont(new Font("Arial", Font.CENTER_BASELINE, 15));

        g.drawString("?????????????????? ?????????????????????? ?????????? ^_^", 600, 50);

        g.drawString("?????????????? ?? ???????????????????????? ??????????????", 100, 80);
        g.drawString(grade, 100, 115);

        int ordinalX = 100;
        int ordinalY = 160;

        for (int i = 0; i < resultOfTests.size(); i++) {
            g.drawString("???????????? - " + resultOfTests.get(i).getQuestion(), ordinalX, ordinalY);

            for (int j = 0; j < 1; j++) {
                g.drawString("???????????????????? ?????????? - " + resultOfTests.get(i).getCorrectAnswer(), ordinalX, ordinalY + 25);
            }

            for (int j = 0; j < 900; j++) {
                g.drawString("_", ordinalX + j, ordinalY + 30);
            }
            ordinalY += 60;
        }

        for (ResultOfTest s : resultOfTests) {
            System.out.println(s.getUserAnswer());
        }


        ImageIO.write(image, "png", new File(file.getParentFile(), "end.png"));
        statisticsService.clearStatisticForTheUserChatId(chatId);
    }
}