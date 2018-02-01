package utils.enums;

/**
 * Created by XIAODA on 2015/9/6.
 */
public enum FamilyDimensionEnum {

    FATHER_CARE("父亲CARE", 1),
    MOTHER_CARE("母亲CARE", 2),
    FATHER_OVERPROTECTION("父亲OVERPROTECTION",3),
    MOTHER_OVERPROTECTION("母亲OVERPROTECTION",4),
    FATHER_EDUCATION_LEVEL("父亲教育程度",5),
    MOTHER_EDUCATION_LEVEL("母亲教育程度",6),
    FAMILY_LEVEL("家庭水平",7);
    // 成员变量
    private String name;
    private int id;

    // 构造方法
    private FamilyDimensionEnum(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // 普通方法
    public static String getName(int id) {
        for (FamilyDimensionEnum familyDimensionEnum : FamilyDimensionEnum.values()) {
            if (familyDimensionEnum.getId() == id) {
                return familyDimensionEnum.name;
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
