package models.dto;

import models.Orders;

import java.util.List;

/**
 * Created by XIAODA on 2016/3/1.
 */
public class OrderDto {

    private Orders order;
    private String testpaperName;
    private List<Integer> questiontypeList;

    public OrderDto(){}

    public OrderDto(Orders order, String testpaperName, List<Integer> questiontypeList) {
        this.order = order;
        this.testpaperName = testpaperName;
        this.questiontypeList = questiontypeList;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public String getTestpaperName() {
        return testpaperName;
    }

    public void setTestpaperName(String testpaperName) {
        this.testpaperName = testpaperName;
    }

    public List<Integer> getQuestiontypeList() {
        return questiontypeList;
    }

    public void setQuestiontypeList(List<Integer> questiontypeList) {
        this.questiontypeList = questiontypeList;
    }
}
