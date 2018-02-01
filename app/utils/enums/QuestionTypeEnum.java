package utils.enums;

/**
 * Created by XIAODA on 2015/9/6.
 */
public enum QuestionTypeEnum {

    MATERIAL_MEMORY("配对记忆", 1),
    SYMBOLIC_OPERATION("符号运算", 2),
    SPATIAL_MEMORY("顺序记忆", 3),
    GRAPF_SEARCH("图形搜索", 4),
    SHAPE_LINKING("图形连线", 5),
    PARAGRAPH_REASONING("段落推理", 6),
    MATERIAL_EXTRACT("配对回忆", 7),
    ANALOGIC_REASONING("类比推理", 8),
    MATRIX_REASONING("矩阵推理", 9),
    PERSONALITY("人格问卷", 10),
    FAMILY_QUESTIONNAIRE("家庭环境问卷", 11),
    SPACE_ROTATION("空间旋转", 12),
    EMOTION_RECOGNITION("情绪识别", 13),
    EMOTION_UNDERSTANDING("情绪理解", 14),
    REMOTE_ASSOCIATION("远距离联想", 15),
    CRITICALTHINKING_ABILITY("批判性思维能力", 16),
    CRITICALTHINKING_TENDENCY("批判性思维倾向", 17);

    // 成员变量
    private String name;
    private int id;

    // 构造方法
    private QuestionTypeEnum(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // 普通方法
    public static String getName(int id) {
        for (QuestionTypeEnum questionTypeEnum : QuestionTypeEnum.values()) {
            if (questionTypeEnum.getId() == id) {
                return questionTypeEnum.name;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
