package models.vo;

/**
 * Created by wangbin on 2015/12/16.
 */
public class TestpaperVo {

    private Integer qtype;
    private Long qtypeQuestionNumber;

    public TestpaperVo() {
    }

    public TestpaperVo(Integer qtype, Long qtypeQuestionNumber) {
        this.qtype = qtype;
        this.qtypeQuestionNumber = qtypeQuestionNumber;
    }

    public Integer getQtype() {
        return qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    public Long getQtypeQuestionNumber() {
        return qtypeQuestionNumber;
    }

    public void setQtypeQuestionNumber(Long qtypeQuestionNumber) {
        this.qtypeQuestionNumber = qtypeQuestionNumber;
    }
}
