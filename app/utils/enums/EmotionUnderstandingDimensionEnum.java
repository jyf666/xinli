package utils.enums;

/**
 * Created by XIAODA on 2015/9/6.
 */
public enum EmotionUnderstandingDimensionEnum {
    SAD("悲伤", "a"),
    ANGRY("愤怒", "b"),
    SURPRISED("惊讶","c"),
    FEAR("恐惧","d"),
    HAPPY("快乐","e"),
    HATE("厌恶","f"),
    EXCITEMENT("兴奋","g"),
    LOVE("爱","h"),
    DISSATISFIED("不满","i"),
    WORRY("担心","j"),
    BE_ANXIOUS("担忧","k"),
    ANNOYANCE("烦恼","l"),
    EMBARRASSED("尴尬","m"),
    AFFECT("感动","n"),
    APPRECIATE("感激","o"),
    BE_SHY("害羞","p"),
    CURIOUS("好奇","q"),
    COMPUNCTION("后悔","r"),
    YEARN("怀念","s"),
    SUSPECT("怀疑","t"),
    EXCITE("激动", "u"),
    ENVY("嫉妒", "v"),
    ANXIOUS("焦急","w"),
    INQUIETUDE("焦虑","x"),
    NERVOUS("紧张","y"),
    ADMIRE("敬佩","z"),
    DEPRESSED("沮丧","ab"),
    HOPELESSNESS("绝望","bc"),
    PUZZLED("困惑","cd"),
    SATISFY("满足","de"),
    GUILT("内疚","ef"),
    EXPECT("期待","fg"),
    CONTEMPT("轻蔑","gh"),
    RELAXED("轻松","hi"),
    REJOICE("庆幸","ij"),
    DESPAIR("失望","jk"),
    SYMPATHY("同情","lm"),
    CHAGRIN("委屈","mn"),
    WARMTH("温暖","no"),
    NO_ALTERNATIVE("无奈","op"),
    RELIEVED("欣慰","pq"),
    ASHAMED("羞愧","qr"),
    REGRET("遗憾","rs"),
    DOUBT("疑惑","st"),
    PRIDE("自豪","tu"),
    REMORSE("自责","uv");

    // 成员变量
    private String name;
    private String choice;

    // 构造方法
    private EmotionUnderstandingDimensionEnum(String name, String choice) {
        this.name = name;
        this.choice = choice;
    }

    // 普通方法
    public static String getName(String choice) {
        for (EmotionUnderstandingDimensionEnum emotionUnderstandingDimensionEnum : EmotionUnderstandingDimensionEnum.values()) {
            if (emotionUnderstandingDimensionEnum.getChoice().equals(choice)) {
                return emotionUnderstandingDimensionEnum.name;
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
