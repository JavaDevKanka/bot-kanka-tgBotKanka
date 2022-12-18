package com.kankaBot.kankaBot.dao.abstracts;

import com.kankaBot.kankaBot.models.MessagesBuffer;
import com.kankaBot.kankaBot.models.Question;
import org.springframework.stereotype.Component;

@Component
public interface MessagesBufferDao extends ReadWriteDao<MessagesBuffer, Long> {

}
