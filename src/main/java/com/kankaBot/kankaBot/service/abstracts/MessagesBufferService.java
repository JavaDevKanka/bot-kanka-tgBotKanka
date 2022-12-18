package com.kankaBot.kankaBot.service.abstracts;

import com.kankaBot.kankaBot.models.MessagesBuffer;
import org.springframework.stereotype.Service;

@Service
public interface MessagesBufferService extends ReadWriteService<MessagesBuffer, Long> {

}
