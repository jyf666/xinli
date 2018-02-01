package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import org.hibernate.annotations.*;

/**
 * Created by ballma on 16/3/17.
 */
@Entity
@Table(name = "testpaper_user")
public class TestpaperUser implements java.io.Serializable {

    //Field
    private Integer id;
    private Integer uid;
    private Integer tpid;
    private Integer qtype;
    private String questionseq;

    // Constructors

    /** default constructor */
    public TestpaperUser() {
    }

    /** full constructor */
    public TestpaperUser(Integer tpid, Integer uid) {
        this.uid = uid;
        this.tpid = tpid;
        this.qtype = qtype;
    }

    // Property accessors
    @Id
    //@GenericGenerator(name = "testpaperuseridGenerator", strategy = "uuid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "uid", nullable = false)
    public Integer getUid() {
        return this.uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Column(name = "tpid", nullable = false)
    public Integer getTpid() {
        return this.tpid;
    }

    public void setTpid(Integer tpid) {
        this.tpid = tpid;
    }

    @Column(name = "qtype", nullable = false)
    public Integer getQtype() {
        return this.qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    @Column(name = "questionseq", nullable = true)
    public  String getQuestionseq(){
        return this.questionseq;
    }

    public void setQuestionseq(String questionseq){
        this.questionseq = questionseq;
    }
}
