package utils.enums;

/**
 * Created by Lintongyu 2016/05/11
 */
public enum CriticalThinkingTendencyDimensionEnum {

    TRUTH("寻求真理", 1),
    MIND("思想开明", 2),
    ANALYSIS("分析推理",3),
    SYSTEMATIC("系统性",4),
    CONFIDENCE("自信",5),
    THIRST("求知欲",6),
    MATURITY("成熟性",7);
    // 成员变量
    private String name;
    private int id;

    // 构造方法
    private CriticalThinkingTendencyDimensionEnum(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // 普通方法
    public static String getName(int id) {
        for (CriticalThinkingTendencyDimensionEnum dimensionEnum : CriticalThinkingTendencyDimensionEnum.values()) {
            if (dimensionEnum.getId() == id) {
                return dimensionEnum.name;
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
