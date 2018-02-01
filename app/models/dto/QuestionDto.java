package models.dto;

import models.Question;

import java.util.List;

/**
 * Created by XIAODA on 2016/4/2.
 */
public class QuestionDto {
    List<Question> questionList;

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }
}
