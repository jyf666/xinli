package models;

/**
 * Created by ballma on 16/3/21.
 */

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import org.hibernate.annotations.*;

@Entity
@Table(name = "testpaper_user_question")
public class TestpaperUserQuestion implements java.io.Serializable {
    //Field
    private Integer id;
    private String uuid;
    private Integer uid;
    private Integer tpid;
    private String qid;
    private Integer qtype;
    private String choiceseq;

    // Constructors

    /** default constructor */
    public TestpaperUserQuestion() {
    }

    /** full constructor */
    public TestpaperUserQuestion(Integer tpid, Integer uid, String qid) {
        this.uid = uid;
        this.tpid = tpid;
        this.qid = qid;
    }

    // Property accessors
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    @Column(name = "uuid", nullable = false)
    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    @Column(name = "qid", nullable = false, length = 36)
    public String getQid() {
        return this.qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    @Column(name = "qtype", nullable = false)
    public Integer getQtype() {
        return this.qtype;
    }

    public void setQtype(Integer qtype) {
        this.qtype = qtype;
    }

    @Column(name = "choiceseq", nullable = true)
    public  String getChoiceseq(){
        return this.choiceseq;
    }

    public void setChoiceseq(String choiceseq){
        this.choiceseq = choiceseq;
    }
}
