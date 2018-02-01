package utils.enums;

/**
 * Created by XIAODA on 2015/9/7.
 */
public enum SexEnum {
    MAN ("男", 1),
    WOMAN("女", 0);

    // 成员变量
    private String name;
    private int id;

    // 构造方法
    private SexEnum(String name, int id) {
        this.name = name;
        this.id = id;
    }

    // 普通方法
    public static String getName(int id) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getId() == id) {
                return sexEnum.name;
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
