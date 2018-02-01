package models.vo;

/**
 * Created by wangbin on 2015/8/20.
 */
public class NormVo {
    private Integer qtid;
    private String qtypeName;
    private String average;
    private String standard;


    public NormVo(){

    }

    public NormVo(Integer qtid,String qtypeName,String average,String standard){
        this.qtid = qtid;
        this.qtypeName = qtypeName;
        this.average = average;
        this.standard = standard;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getQtypeName() {

        return qtypeName;
    }

    public void setQtypeName(String qtypeName) {
        this.qtypeName = qtypeName;
    }

    public Integer getQtid() {
        return qtid;
    }

    public void setQtid(Integer qtid) {
        this.qtid = qtid;
    }
}
