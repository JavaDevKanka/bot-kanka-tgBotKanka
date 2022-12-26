package com.kankaBot.kankaBot.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(generator = "question_seq")
    private Long id;

    private String question;

    private String explanation;

    @OneToMany(cascade = CascadeType.ALL)
            @JoinColumn(name = "question_id")
    Set<Answer> answers = new HashSet<>();

}
