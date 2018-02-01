package controllers;

import models.Permission;
import models.vo.PermissionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.PermissionService;
import utils.ConvertUtils;
import utils.SystemConstant;
import views.html.admin.systemManage.permissionManage.permissionList;

import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/12/23.
 */
@org.springframework.stereotype.Controller
public class PermissionController extends Controller {

    @Autowired
    private PermissionService permissionService;

    /**
     * 权限管理页面
     * @return
     */
    public Result treeView() {
        return ok(permissionList.apply());
    }

    /**
     * 获取权限树
     **/
    public Result tree() {
        String roleId = request().getQueryString("roleId");
        List<PermissionVo> list = permissionService.findTree(Integer.valueOf(roleId));
        return ok(Json.toJson(list));
    }

    /**
     * 获取权限列表
     **/
    public Result list() {
        List<Permission> list = permissionService.findAll();
        return ok(Json.toJson(list));
    }
}
