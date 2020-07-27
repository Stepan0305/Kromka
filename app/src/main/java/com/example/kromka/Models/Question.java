package com.example.kromka.Models;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Question {
    private String title;
    private ArrayList<Answer> answers;
    private String subTitle;

    public Question(String title, @Nullable String subTitle, ArrayList<Answer> answers) {
        this.title = title;
        this.answers = answers;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
