package utils.enums;

/**
 * Created by XIAODA on 2015/9/6.
 */
public enum EmotionRecognitionDimensionEnum {

    SAD("悲伤", "a"),
    ANGRY("愤怒", "b"),
    SURPRISED("惊讶","c"),
    FEAR("恐惧","d"),
    HAPPY("快乐","e"),
    HATE("厌恶","f"),
    EXCITEMENT("兴奋","g");
    // 成员变量
    private String name;
    private String choice;

    // 构造方法
    private EmotionRecognitionDimensionEnum(String name, String choice) {
        this.name = name;
        this.choice = choice;
    }

    // 普通方法
    public static String getName(String choice) {
        for (EmotionRecognitionDimensionEnum emotionRecognitionDimensionEnum : EmotionRecognitionDimensionEnum.values()) {
            if (emotionRecognitionDimensionEnum.getChoice().equals(choice)) {
                return emotionRecognitionDimensionEnum.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
