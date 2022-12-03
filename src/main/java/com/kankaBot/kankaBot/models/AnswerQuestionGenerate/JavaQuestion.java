package com.kankaBot.kankaBot.models.AnswerQuestionGenerate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "java_question")
public class JavaQuestion {

    @Id
    private Long id;

    @Column(name = "question")
    private String question;

    @OneToMany
    private List<AnswerVariables> answers;





}
