package com.app.budsbank.models;

import java.util.ArrayList;

public class QuizResponseModel extends ResponseModel {
    private ArrayList<QuizQuestionsModel> questions;

    public ArrayList<QuizQuestionsModel> getQuestions() {
        return questions;
    }
}
