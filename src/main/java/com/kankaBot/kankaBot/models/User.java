package com.kankaBot.kankaBot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.ChatPhoto;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;

    @OneToOne(mappedBy = "userId", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private PollAnswer pollAnswer;
    @Transient
    private ChatPhoto chatPhoto;

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                ", chatPhoto=" + chatPhoto +
                '}';
    }
}
