package com.kankaBot.kankaBot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(generator = "answer_seq")
    private Long id;

    @NotNull
    private String answer;

    private Long seqnumber;

    @NotNull
    private Boolean is_right;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
