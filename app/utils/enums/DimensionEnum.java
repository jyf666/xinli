package utils.enums;

/**
 * Created by XIAODA on 2015/9/6.
 */
public enum DimensionEnum {

    EMOTIONAL_STABILITY("情绪稳定", 1),
    RESPONSIBILITY("责任感", 2),
    EXPLORATION("探究性",3),
    OPENNESS("开放性",4),
    INDEPENDENCE("独立性",5),
    AGREEABLENESS("宜人性",6),
    INTERPERSONAL_COMMUNICATION("人际交往",7);
    // 成员变量
    private String name;
    private int id;

    // 构造方法
    private DimensionEnum(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // 普通方法
    public static String getName(int id) {
        for (DimensionEnum dimensionEnum : DimensionEnum.values()) {
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
