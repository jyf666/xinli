package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.RoleService;
import utils.PageUtils;
import utils.SystemConstant;
import views.html.admin.systemManage.roleManage.roleList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/12/18.
 */
@org.springframework.stereotype.Controller
public class RoleController extends Controller {

    @Autowired
    private RoleService roleService;

    /**
     * 角色管理页面
     * @return
     */
    public Result listView() {
        return ok(roleList.apply());
    }

    /**
     * 获取角色列表
     **/
    public Result list() {
        List<Role> list = roleService.findAll();
        return ok(Json.toJson(list));
    }

    /**
     * 获取角色分页数据
     * @return
     */
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "roleName", "code"};//定义列名
        Page<Role> page = roleService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /**
     * 添加角色
     * @return
     */
    public Result add() {

        try {
            Map<String, String[]> form = request().body().asFormUrlEncoded();
            if (form != null) {
                String roleName = form.get("roleName")[0];
                String code = form.get("code")[0];
                Role role = new Role(code, roleName);
                roleService.save(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }

        return ok(SystemConstant.SUCCESS);
    }

    /**
     * 修改角色
     * @return
     */
    public Result edit() {
        try {
            Map<String, String[]> form = request().body().asFormUrlEncoded();
            if (form != null) {
                String id = form.get("id")[0];
                String roleName = form.get("roleName")[0];
                String code = form.get("code")[0];
                Role role = new Role(code, roleName);
                role.setId(Integer.valueOf(id));
                roleService.save(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }

        return ok(SystemConstant.SUCCESS);
    }

    /**
     * 为角色绑定权限
     * @return
     */
    public Result savePermission() {
        try {
            JsonNode form = request().body().asJson();
            if (form != null) {
                int roleId = form.get("roleId").asInt();
                JsonNode permissionIdsNode = form.get("permissionIds");
                Iterator iterator = permissionIdsNode.iterator();
                List<Integer> permissionIds = new ArrayList();
                while (iterator.hasNext()) {
                    IntNode permissionIdNode = (IntNode) iterator.next();
                    permissionIds.add(permissionIdNode.asInt());
                }
                roleService.savePermission(roleId, permissionIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }

        return ok(SystemConstant.SUCCESS);
    }

    /**
     * 删除角色
     * @return
     */
    public Result delete() {
        try {
            Map<String, String[]> form = request().body().asFormUrlEncoded();
            if (form != null) {
                String roleId = form.get("roleId")[0];
                roleService.delete(Integer.valueOf(roleId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }
        return ok(SystemConstant.SUCCESS);
    }
}
