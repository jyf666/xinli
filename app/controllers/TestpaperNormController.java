package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.QuestionTypeDimensionNorm;
import models.QuestionTypeNorm;
import models.TestpaperNorm;
import models.vo.QuestionTypeNormVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.QuestiontypeDimensionNormService;
import service.QuestiontypeNormService;
import service.TestpaperNormService;
import utils.PageUtils;
import utils.SystemConstant;
import utils.enums.DimensionEnum;
import utils.enums.FamilyDimensionEnum;
import views.html.admin.testpaperNormManage.testpaperNormList;
import views.html.admin.testpaperNormManage.testpaperNormEdit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by XIAODA on 2015/12/18.
 */
@org.springframework.stereotype.Controller
public class TestpaperNormController extends Controller {


    @Autowired
    private TestpaperNormService testpaperNormService;
    @Autowired
    private QuestiontypeNormService questiontypeNormService;
    @Autowired
    private QuestiontypeDimensionNormService questiontypeDimensionNormService;

    /**
     * 常模列表页面
     * @return
     */
    public Result listView() {
        return ok(testpaperNormList.apply());
    }

    /**
     * 获取常模列表
     **/
    public Result list() {
        List<TestpaperNorm> list = testpaperNormService.findAll();
        return ok(Json.toJson(list));
    }


    /**
     * 获取常模
     **/
    public Result getAll() {
        List<TestpaperNorm> list = testpaperNormService.findAll();
        return ok(Json.toJson(list));
    }

    /**
     * 获取常模分页数据
     * @return
     */
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "age", "sex","province","city","town"};//定义列名
        Page<TestpaperNorm> page = testpaperNormService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /**
     * 查看具体的常模信息
     * @param tpnid
     * @return
     */
    public Result data(Integer tpnid){
        List<QuestionTypeNormVo> data =  new ArrayList<>();
        List<QuestionTypeNormVo> questionTypeNorms = questiontypeNormService.getNormData(tpnid);
        data.addAll(questionTypeNorms);

        List<QuestionTypeNormVo> questionTypDiemensionNorms = questiontypeDimensionNormService.getNormData(tpnid);
        data.addAll(questionTypDiemensionNorms);

        for (int i = 0; i < questionTypDiemensionNorms.size() ; i++) {
            QuestionTypeNormVo questionTypeNormVo = questionTypDiemensionNorms.get(i);
            if(questionTypeNormVo.getQtype()==SystemConstant.QUESTION_TYPE_PERSONALITY)
                questionTypeNormVo.setDimensionName(DimensionEnum.getName(Integer.parseInt(questionTypeNormVo.getDimension())));
            if(questionTypeNormVo.getQtype()==SystemConstant.QUESTION_TYPE_FAMILY_QUESTIONNAIRE)
                questionTypeNormVo.setDimensionName(FamilyDimensionEnum.getName(Integer.parseInt(questionTypeNormVo.getDimension())));
        }
        return ok(Json.toJson(data));
    }

    public Result editView(Integer tpnid){
        List<QuestionTypeNormVo> data =  new ArrayList<>();
        TestpaperNorm testpaperNorm =  testpaperNormService.findById(tpnid);
        List<QuestionTypeNormVo> questionTypeNorms = questiontypeNormService.getNormData(tpnid);
        data.addAll(questionTypeNorms);

        List<QuestionTypeNormVo> questionTypDiemensionNorms = questiontypeDimensionNormService.getNormData(tpnid);
        data.addAll(questionTypDiemensionNorms);

        return ok(testpaperNormEdit.render(testpaperNorm, data));
    }

    @Transactional
    public Result edit(Integer tpnid){
        ObjectNode dataJson = (ObjectNode)request().body().asJson();
        JsonNode answerListJson = Json.parse(dataJson.findPath("data").asText());
        TestpaperNorm testpaperNorm =  testpaperNormService.findById(tpnid);
        testpaperNorm.setAge(dataJson.findPath("age").asInt());
        testpaperNorm.setSex(dataJson.findPath("sex").asText());
        testpaperNorm.setProvince(dataJson.findPath("province").asText());
        testpaperNorm.setCity(dataJson.findPath("city").asText());
        testpaperNorm.setTown(dataJson.findPath("town").asText());


        List<QuestionTypeNorm> questionTypeNorms = questiontypeNormService.findByTpnid(tpnid);
        List<QuestionTypeDimensionNorm> questionTypeDimensionNorms = questiontypeDimensionNormService.findByTpnid(tpnid);

        Iterator iterator = answerListJson.iterator();
        while(iterator.hasNext()){
            ObjectNode normJson = (ObjectNode)iterator.next();
            for (int i = 0; i < questionTypeNorms.size(); i++) {
                QuestionTypeNorm questionTypeNorm = questionTypeNorms.get(i);
                if(normJson.findPath("qtype").asInt() == questionTypeNorm.getQType()){
                    questionTypeNorm.setAvg(normJson.findPath("avg").asText());
                    questionTypeNorm.setStdev(normJson.findPath("stdev").asText());
                }
            }

            for (int i = 0; i < questionTypeDimensionNorms.size(); i++) {
                QuestionTypeDimensionNorm questionTypeDimensionNorm = questionTypeDimensionNorms.get(i);
                if(normJson.findPath("qtype").asInt() == questionTypeDimensionNorm.getQType() && normJson.findPath("dimension").asText().equals(questionTypeDimensionNorm.getDimension()) ){
                    questionTypeDimensionNorm.setAvg(normJson.findPath("avg").asText());
                    questionTypeDimensionNorm.setStdev(normJson.findPath("stdev").asText());
                }
            }
        }
        return ok();
    }
}
