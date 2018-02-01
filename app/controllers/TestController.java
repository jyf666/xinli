package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.dto.OrganizeTestDto;
import models.dto.ResponseDto;
import models.vo.NormVo;
import models.vo.SearchVo;
import models.vo.TestVo;
import models.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import views.html.admin.testManage.*;
import utils.PageUtils;
import views.html.admin.testManage.user.list;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 考试控制
 *
 * @pdOid 13e917c5-e997-4e0a-8be0-2caf2a9cec9d
 * */
@org.springframework.stereotype.Controller
public class TestController extends Controller {

    @Autowired
    private AdmissionsOrgService admissionsOrgService;
    @Autowired
    private TestService testService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestPaperService testPaperService;
    @Autowired
    private QuestionTypeService questionTypeService;
    @Autowired
    private VTestUserService vTestUserService;

    /** 添加考试页面
     *
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result addView() {
        List<AdmissionsOrg> admissionsOrgs = admissionsOrgService.findAll();
        List<Questiontype> questiontypes = questionTypeService.getAllQuestionType();
        return ok(testAdd.render(admissionsOrgs,questiontypes));
    }


    /**
     * 添加考试
     * @return
     */
    public Result addTest() {
        Form<Test> testForm = Form.form(Test.class);
        Test test = testForm.bindFromRequest().get();
        ResponseDto responseDto = new ResponseDto(true, "保存成功");
        try {
            testService.save(test);
        } catch (Exception e) {
            responseDto.setMessage("保存失败");
            responseDto.setSuccess(false);
            e.printStackTrace();
        }
        return ok(Json.toJson(responseDto));
    }

    /** 添加考试考生页面
     *
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result addUserView(Integer tid,Integer orgCode) {
        List<UserVo> testUsers = userService.getTestUserList(tid, orgCode);
        List<UserVo> users = userService.getOrgUserList(orgCode);

        for(int i = 0;i<testUsers.size();i++){
            if(users.contains(testUsers.get(i))){
                users.remove(testUsers.get(i));
            }
        }
        return ok(addUser.render(users,tid,orgCode));
    }

    /** 添加考试考生
     *
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result addUser() {
        Map<String,String[]> params = request().body().asFormUrlEncoded();
        String userStr = params.get("userStr")[0];
        Integer tid = Integer.parseInt(params.get("tid")[0]);
        testService.addUser(tid, userStr);
        return ok();
    }

    /** 修改考试页面
     *  @param tid 考试id
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result editView(Integer tid) {
        List<AdmissionsOrg> admissionsOrgs = admissionsOrgService.findAll();
        Test test = testService.findById(tid);
        List<Testpaper> testpapers = testPaperService.findAllByOrgCode(test.getOrgCode());
        JsonNode normDatajsonNode = Json.parse(test.getNormData());
        Iterator iterator = normDatajsonNode.iterator();
        List<NormVo> normVos = new ArrayList<NormVo>();
        while(iterator.hasNext()){
            ObjectNode normJson = (ObjectNode)iterator.next();
            NormVo normVo = new NormVo();
            normVo.setQtid(normJson.findPath("qtid").asInt());
            normVo.setQtypeName(normJson.findPath("qType").asText());
            normVo.setAverage(normJson.findPath("average").asText());
            normVo.setStandard(normJson.findPath("standard").asText());
            normVos.add(normVo);
        }
        return ok(testEdit.render(admissionsOrgs,testpapers,test,normVos));
    }

    /** 修改考试
     *
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result editTest() {
        ObjectNode objectNode = (ObjectNode)request().body().asJson();
        testService.update(objectNode);
        return ok();
    }


    /** 修改招生机构考试
     *
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result editOrgTest() {
        ObjectNode objectNode = (ObjectNode)request().body().asJson();
        testService.orgUpdateTest(objectNode);
        return ok();
    }

    /** 删除考试
     * @param tid 考试id
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result deleteTest(Integer tid) {
        testService.delete(tid);
        return ok();
    }

    /** 删除考试的学生
     * @param tid 考试id
     * @param uid 用户id
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result deleteUser(Integer tid,Integer uid) {
        testService.deleteUser(tid, uid);
        return ok();
    }

    /** 根据试卷id获取试卷题目类型
     * @param tpid 试卷id
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result questionType(Integer tpid) {
        List<Questiontype> questiontypes = questionTypeService.findAllByTpid(tpid);
        JsonNode jsonNode = Json.toJson(questiontypes);
        return ok(jsonNode);
    }

    /** 根据orgCode获取试卷
     * @param orgCode orgCode
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result orgTestpaper(Integer orgCode) {
        List<Testpaper> testpapers = testPaperService.findAllByOrgCode(orgCode);
        if(request().body().asFormUrlEncoded()!=null){
            Map<String,String[]> params = request().body().asFormUrlEncoded();
            String qTypeStr = params.get("qTypeStr")[0];
            if(qTypeStr.equals("")){
                return ok(Json.toJson(testpapers));
            }
            testpapers.clear();
            String[] qType = qTypeStr.split(",");
            testpapers = testPaperService.containsQtypeTestpaper(qTypeStr);
        }
        JsonNode jsonNode = Json.toJson(testpapers);
        return ok(jsonNode);
    }

    /**
     * 考试列表页面
     * @return
     */
    public Result listView() {
        return ok(testList.apply());
    }

    /**
     * 获取考试分页数据
     * @return
     */
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "turn", "name", "orgName", "startTime", "report", "status", "importedPopulation", "submitNum", "orgCode", "endTime"};// 定义列名
        Page<VTest> page = testService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /**
     * 考试列表页面
     * @return
     */
    public Result userListView(Integer tid) {
        return ok(list.render(tid));
    }

    /**
     * 获取考试分页数据
     * @return
     */
    public Result userPage() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "name", "orgCode", "account", "password", "email", "isover"};// 定义列名
        Page<VTestUser> page = vTestUserService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /** 根据前台所传的查询某次考试考生
     *  @param tid 试卷id
     *  @param orgCode 招生机构
     *  @param page 页数
     *
     * @pdOid 0afa767d-839e-4b0c-99cd-552357042e96 */
    public Result testUser(Integer tid,Integer orgCode,Integer page) {
        Page<UserVo> pageContent = userService.getTestUserList(tid, orgCode, page);
        Test test = testService.findById(tid);

        TestVo testVo = new TestVo(tid,orgCode);
        SearchVo searchVo = new SearchVo(0,"","","");

        List<UserVo> userInfos = pageContent.getContent();
        Long totalNum = pageContent.getTotalElements();
        Integer totalPage = pageContent.getTotalPages();
        Integer currentPage = page;
        return ok(testListUser.render(userInfos, totalNum, test,totalPage,currentPage,testVo,searchVo,orgCode,tid));
    }

    /** 根据前台所传的查询某次考试没有提交答案的考生
     *  @param tid 试卷id
     *  @param orgCode 招生机构
     *  @param page 页数
     *
     * @pdOid 0afa767d-839e-4b0c-99cd-552357042e96 */
    public Result noAnswerUser(Integer tid,Integer orgCode,Integer page) {
        Page<UserVo> pageContent = userService.getTestNoAnswerUserList(tid, orgCode, page);
        Test test = testService.findById(tid);

        TestVo testVo = new TestVo(tid,orgCode);
        SearchVo searchVo = new SearchVo(0,"","","");

        List<UserVo> userInfos = pageContent.getContent();
        Long totalNum = pageContent.getTotalElements();
        Integer totalPage = pageContent.getTotalPages();
        Integer currentPage = page;
        return ok(testListNoAnswerUser.render(userInfos, totalNum, test,totalPage,currentPage,testVo,searchVo));
    }



    /** 根据前台所传的查询参数，查询考试考生
     *  @param tid 试卷id
     *  @param orgCode 招生机构
     *  @param name 姓名
     *  @param account 帐号
     *  @param email 邮箱
     *  @param page 页数
     * @pdOid 0afa767d-839e-4b0c-99cd-552357042e96 */
    public Result searchUser(Integer tid,Integer orgCode,String name,String account,String email,Integer page) {

        SearchVo searchVo = new SearchVo(orgCode,name,account,email);
        Test test = testService.findById(tid);
        Page<UserVo> pageContent = testService.searchUser(tid,orgCode, name, account, email, page);
        TestVo testVo = new TestVo(tid,orgCode);

        List<UserVo> userInfos = pageContent.getContent();
        Long totalNum = pageContent.getTotalElements();
        Integer totalPage = pageContent.getTotalPages();
        Integer currentPage = page;
        return ok(testListUser.render(userInfos, totalNum, test,totalPage,currentPage,testVo,searchVo,orgCode,tid));
    }

    /** 上传用户页面
     * @param tid 考试id
     * @param orgCode 招生机构
     * @pdOid 0f637f79-bc5a-4b6c-8da3-8c608ce65fcf */
    public Result uploadUserView(Integer tid,Integer orgCode) {
        return ok(testUploadUser.render(tid,orgCode));
    }

    /**重新导入用户
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     * */
    @play.mvc.BodyParser.Of(value = play.mvc.BodyParser.Json.class,maxLength = 8 * 1024 * 1024)
    public Result uploadUser() {
        Form<OrganizeTestDto> form = Form.form(OrganizeTestDto.class);
        OrganizeTestDto organizeTestDto = form.bindFromRequest().get();
        ResponseDto responseDto = new ResponseDto();
        List<UserVo> outputUser = userService.uploadUserWithTest(responseDto, organizeTestDto);
        responseDto.setResult(Json.toJson(outputUser));
        return ok(Json.toJson(responseDto));
    }

    /** 考试模版下载
     *
     * @pdOid d374a5c3-008a-4686-a1e1-4803bb497e74 */
    public Result downloadExcelDemo() {
        try {
            response().setContentType("application/x-download");
            response().setHeader("Content-disposition","attachment; filename="+new String(new String("考试填写模版.xls").getBytes("UTF-8"),"ISO8859-1"));
            return ok(new File("./public/download/test.xls"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证考试名称是否存在
     * @return
     */
    public Result validateName(String name){
        Integer orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
        return ok(testService.validateName(name, orgCode));
    }
}
