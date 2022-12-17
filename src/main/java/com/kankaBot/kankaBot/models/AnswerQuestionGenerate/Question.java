package com.kankaBot.kankaBot.models.AnswerQuestionGenerate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private Boolean is_multiAnswer;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "question_answer",
            joinColumns = {@JoinColumn (name = "question_id")},
            inverseJoinColumns = {@JoinColumn (name = "answer_id")}
    )
    Set<Answer> answers = new HashSet<>();

}
