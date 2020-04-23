package com.app.budsbank.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuizOptionModel implements Serializable {
    private int id;
    @SerializedName("question_id")
    private int questionId;
    @SerializedName("option_value")
    private String optionValue;
    @SerializedName("is_answer")
    private boolean isAnswer;

    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public boolean isAnswer() {
        return isAnswer;
    }
}
