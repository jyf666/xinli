package models.vo;

import java.util.Date;

/**
 * Created by wangbin on 2016/1/5.
 */
public class QuestionTypeNormVo {

    private Integer id;
    private Integer tpnid;
    private Integer qtype;
    private String qtypeName;
    private String dimension;
    private String dimensionName;
    private String avg;
    private String stdev;


    public QuestionTypeNormVo() {
    }

    public QuestionTypeNormVo(Integer id, Integer tpnid, Integer qtype, String qtypeName,String avg, String stdev) {
        this.avg = avg;
        this.id = id;
        this.tpnid = tpnid;
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.stdev = stdev;
    }

    public QuestionTypeNormVo(Integer id, Integer tpnid, Integer qtype, String qtypeName, String dimension, String avg, String stdev) {
        this.id = id;
        this.tpnid = tpnid;
        this.qtype = qtype;
        this.qtypeName = qtypeName;
        this.dimension = dimension;
        this.avg = avg;
        this.stdev = stdev;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTpnid() {
        return tpnid;
    }

    public void setTpnid(Integer tpnid) {
        this.tpnid = tpnid;
    }

    public Integer getQtype() {
        return qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    public String getQtypeName() {
        return qtypeName;
    }

    public void setQtypeName(String qtypeName) {
        this.qtypeName = qtypeName;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getAvg() {
        return avg;
    }

    public void setAvg(String avg) {
        this.avg = avg;
    }

    public String getStdev() {
        return stdev;
    }

    public void setStdev(String stdev) {
        this.stdev = stdev;
    }
}
