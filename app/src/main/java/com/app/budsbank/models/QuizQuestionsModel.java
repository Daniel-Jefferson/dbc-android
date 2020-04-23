package com.app.budsbank.models;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizQuestionsModel implements Serializable {
    private int id;
    private String question;
    ArrayList<QuizOptionModel> options;

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<QuizOptionModel> getOptions() {
        return options;
    }
}
