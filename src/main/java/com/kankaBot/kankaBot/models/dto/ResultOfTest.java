package com.kankaBot.kankaBot.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultOfTest {
    private String question;
    private String correctAnswer;
    private String userAnswer;

}
