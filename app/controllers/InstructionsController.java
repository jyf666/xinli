package controllers;

import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import views.html.exam.common.adminssionCard;
import views.html.instructions.index;
import views.html.instructions.instructions;
import views.html.manifest;

import java.util.List;

/**
 * Created by XIAODA on 2015/8/11.
 */
@org.springframework.stereotype.Controller
public class InstructionsController extends Controller {

    @Autowired
    private InstructionsService instructionsService;
    @Autowired
    private QuestionTypeService questionTypeService;

    /**
     * 测试系统整体介绍页面
     * @return
     */
    public Result index(String isOffLine){
        SessionService.saveSessionItem(SessionService.SessionItemMark.IS_OFFLINE, isOffLine);
        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        List<Questiontype> list = questionTypeService.findAllByTpid(tpid);
        return ok(index.render(list));
    }

    /**
     * 测试系统操作说明页面
     * @return
     */
    public Result instructions(){
        return ok(instructions.apply());
    }

    /**
     * 测试系统整体介绍页面的Manifest文件
     * @return
     */
    public Result indexManifest(){
        List<String> cacheList = instructionsService.getIndexManifest();
        return ok(manifest.render(cacheList));
    }

    /**
     * 测试系统操作说明页面的Manifest文件
     * @return
     */
    public Result instructionsManifest(){

        List<String> cacheList = instructionsService.getInstructionsManifest();
        return ok(manifest.render(cacheList));
    }
}
