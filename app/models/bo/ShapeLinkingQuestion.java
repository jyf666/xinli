package models.bo;

/**
 * Created by XIAODA on 2016/5/24.
 */
public class ShapeLinkingQuestion {

    private int number;
    private String shape;
    private String location;

    public enum ShapeType {
        circle,
        rectangle,
        plus
    }

    public ShapeLinkingQuestion(){}

    public ShapeLinkingQuestion(int number, String shape, String location) {
        this.number = number;
        this.shape = shape;
        this.location = location;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

