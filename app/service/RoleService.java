package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import dao.RoleDao;
import models.Admin;
import models.Permission;
import models.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.PageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIAODA on 2015/12/18.
 */
@Service
public class RoleService {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionService permissionService;

    public List<Role> findAll(){
        return roleDao.findAll();
    }

    /**
     * 分页查找角色
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Role> findAll(int pageNum, int pageSize) {
        Pageable pageable = new PageRequest(pageNum - 1, pageSize);
        return roleDao.findAll(pageable);
    }

    /**
     * 分页查找角色
     * @param start
     * @param pageSize
     * @param orderDir
     * @param orderColumn
     * @return
     */
    public Page<Role> findAll(int start, int pageSize, String orderDir ,String orderColumn) {
        int pageNum = start/pageSize;// 第几页
        Pageable pageable = new PageRequest(pageNum, pageSize, new Sort(Sort.Direction.fromString(StringUtils.upperCase(orderDir)), orderColumn));
        return roleDao.findAll(pageable);
    }

    /**
     * 分页查找角色
     * @param request
     * @param cols 分页列表的列名对应的字段
     * @return
     */
    public Page<Role> findAll(Http.Request request, String[] cols) {
        Pageable pageable = PageUtils.getPageRequest(request, cols);
        Specification<Role> spec = DynamicSpecifications.fromRequest(request, Role.class);
        return roleDao.findAll(spec, pageable);
    }
    
    /**
     * 更新角色信息
     * @param objectNode
     * @return
     */
    @Transactional
    public Role update(ObjectNode objectNode) {
        Role role = roleDao.findOne(objectNode.findPath("roleId").asInt());
        role.setCode(objectNode.findPath("roleCode").asText());
        role.setRoleName(objectNode.findPath("roleName").asText());

        roleDao.save(role);
        return role;
    }

    /**
     * 添加角色信息
     * @param role
     * @return
     */
    @Transactional
    public Role save(Role role) {
        return roleDao.save(role);
    }

    /**
     * 删除角色信息
     * @param id
     */
    @Transactional
    public void delete(Integer id) {
        roleDao.delete(id);
    }

    /**
     * 为角色绑定权限
     * @param roleId
     * @param permissionIds
     * @return
     */
    @Transactional
    public Role savePermission(Integer roleId, List<Integer> permissionIds) {
        Role role = roleDao.findOne(roleId);
        List<Permission> permissions = new ArrayList<>();
        if (permissionIds.size() > 0) {
            permissions = permissionService.findAll(permissionIds);
        }
        role.setPermissions(permissions);
        return roleDao.save(role);
    }
}
