package models.vo;

import com.google.common.collect.Lists;
import models.Permission;

import java.util.List;

/**
 * Created by XIAODA on 2015/12/24.
 */
public class PermissionVo {

    private Integer id;
    private String code;
    private String name;
    private Boolean checked = false;
    private List<PermissionVo> children;

    public PermissionVo() {}

    public PermissionVo(Permission permission) {
        if (permission != null) {
            this.id = permission.getId();
            this.code = permission.getCode();
            this.name = permission.getName();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public List<PermissionVo> getChildren() {
        return children;
    }

    public void setChildren(List<PermissionVo> children) {
        this.children = children;
    }

    public void addChild(PermissionVo child) {
        if (this.children == null) {
            this.children = Lists.newArrayList();
        }
        this.children.add(child);
    }
}
