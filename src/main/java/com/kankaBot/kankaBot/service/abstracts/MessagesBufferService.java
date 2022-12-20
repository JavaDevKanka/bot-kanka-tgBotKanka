package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.MessagesBuffer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessagesBufferService extends ReadWriteService<MessagesBuffer, Long> {

    void flushBuffer();
    Integer CountBuffer();
    List<String> answerList();

}
