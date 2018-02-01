package service;

import com.google.common.collect.Lists;
import dao.PermissionDao;
import dao.RoleDao;
import models.Permission;
import models.Role;
import models.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIAODA on 2015/12/23.
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private RoleDao roleDao;

    public List<Permission> findAll(){
        return permissionDao.findAll();
    }

    public List<PermissionVo> findTree(Integer roleId){
        List<Permission> permissionList = new ArrayList<>();
        if(roleId != null && roleId > 0){
            Role role = roleDao.findOne(roleId);
            permissionList = role.getPermissions();
        }
        List<Permission> permissions = permissionDao.findAll(SearchFilter.eq("pid", 0));
        List<PermissionVo> permissionVoList = Lists.newArrayList();
        for(Permission permission : permissions){
            PermissionVo permissionVo = new PermissionVo(permission);
            if (permissionList != null && permissionList.size() > 0) {
                if (contains(permissionList, permission)) {
                    permissionVo.setChecked(true);
                }
            }
            buildPermissionTreeVo(permissionVo, permission, permissionList);

            permissionVoList.add(permissionVo);
        }

        return permissionVoList;
    }

    /**
     * 根据所攒id集合查询所有权限
     * @param ids
     * @return
     */
    public List<Permission> findAll(List ids) {
        return permissionDao.findAll(SearchFilter.in("id", ids));
    }

    /**
     * 添加权限信息
     * @param role
     * @return
     */
    @Transactional
    public Permission save(Permission role) {
        return permissionDao.save(role);
    }

    /**
     * 删除权限信息
     * @param id
     */
    @Transactional
    public void delete(Integer id) {
        permissionDao.delete(id);
    }

    private void buildPermissionTreeVo(PermissionVo permissionVo, Permission permission, List<Permission> permissionList){
        List<Permission> permissions = permissionDao.findAll(SearchFilter.eq("pid", permission.getId()));
        if (permissions != null && permissions.size() > 0) {
            for (Permission pm : permissions) {
                PermissionVo pv = new PermissionVo(pm);
                if (permissionList != null && permissionList.size() > 0) {
                    if (contains(permissionList, pm)) {
                        pv.setChecked(true);
                    }
                }
                permissionVo.addChild(pv);
                buildPermissionTreeVo(pv, pm, permissionList);
            }
        }
    }

    private Boolean contains(List<Permission> permissionList, Permission permission){
        Integer id = permission.getId();
        for (Permission pm: permissionList) {
            Integer permissionId = pm.getId();
            if(id == permissionId){
                return true;
            }
        }
        return false;
    }
}
