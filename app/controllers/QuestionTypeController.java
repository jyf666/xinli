package controllers;

import models.Questiontype;
import models.VQuestiontype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.QuestionTypeService;
import utils.PageUtils;
import views.html.admin.testPapaerManage.questionTypeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIAODA on 2016/1/26.
 */
@org.springframework.stereotype.Controller
public class QuestionTypeController extends Controller {

    @Autowired
    private QuestionTypeService questionTypeService;

    public Result list(Integer tpid) {
        List<Questiontype> list = new ArrayList<>();
        if(tpid == 0){
            list = questionTypeService.findAll(request());
        } else {
            list = questionTypeService.findAllByTpid(tpid);
        }
        return ok(Json.toJson(list));
    }

    /**
     * 题型列表页面
     * @return
     */
    public Result listView(){
        return ok(questionTypeList.apply());
    }

    /**
     * 获取试题类型分页数据
     * @return
     */
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "name", "introduce", "limitTime", "questionNumber", "scoringFormula","type"};// 定义列名
        Page<VQuestiontype> page = questionTypeService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }
}
