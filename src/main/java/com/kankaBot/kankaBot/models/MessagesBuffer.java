package com.kankaBot.kankaBot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message_buffer")
public class MessagesBuffer {

    @Id
    @GeneratedValue(generator = "message_seq")
    private Long id;

    private Long chatId;

    private String message;

    private Long messageId;

    private Integer correctAnswerId;

}
