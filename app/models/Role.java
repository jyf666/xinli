package models;

import java.util.List;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by XIAODA on 2015/12/14.
 */
@Entity
@Table(name = "role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role implements java.io.Serializable, be.objectify.deadbolt.core.models.Role {

    private Integer id;
    private String roleName;
    private String code;

    private List<Permission> permissions;

    /** default constructor */
    public Role() {
    }

    /** full constructor */
    public Role(String code, String roleName) {
        this.code = code;
        this.roleName = roleName;
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

    @Column(name = "role_name", nullable = false, length = 100)
    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Transient
    public String getName() {
        return this.code;
    }

    // 多对多定义
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "role_permission", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = { @JoinColumn(name = "permission_id") })
    // Fecth策略定义
    @Fetch(FetchMode.SUBSELECT)
    // 集合按id排序
    @OrderBy("id ASC")
    // 缓存策略
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
