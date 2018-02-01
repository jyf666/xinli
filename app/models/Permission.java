package models;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by XIAODA on 2015/12/14.
 */
@Entity
@Table(name = "permission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission implements java.io.Serializable, be.objectify.deadbolt.core.models.Permission {

    // Fields
    private Integer id;
    private String code;
    private String name;
    private Integer pid;

    // Constructors

    /** default constructor */
    public Permission() {
    }

    /** full constructor */
    public Permission(String code, String name) {
        this.code = code;
        this.name = name;
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

    @Column(name = "code", nullable = false, length = 20)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false, length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "pid", unique = true, nullable = false)
    public Integer getPid() {
        return this.pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    @Transient
    public String getValue() {
        return this.code;
    }
}
