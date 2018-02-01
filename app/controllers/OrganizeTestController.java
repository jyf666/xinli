package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import models.Testpaper;
import models.dto.OrganizeTestDto;
import models.dto.ResponseDto;
import models.dto.UserDto;
import models.vo.UserVo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.*;
import utils.DateUtils;
import utils.ExcelUtils;
import utils.SystemConstant;
import views.html.admin.organizeTest.configInstructions;
import views.html.admin.organizeTest.importUser;
import views.html.admin.organizeTest.examArrangement;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIAODA on 2016/1/8.
 */
@org.springframework.stereotype.Controller
public class OrganizeTestController extends Controller {

    @Autowired
    private UserService userService;
    @Autowired
    private TestService testService;
    @Autowired
    private TestPaperService testPaperService;

    /**
     * 组织考试配置说明页
     **/
    @Pattern("/admin/organizeTest/configInstructions")
    public Result configInstructions(){
        return ok(configInstructions.apply());
    }

    /**
     * 考试安排页面
     * @return
     */
    @Pattern("/admin/organizeTest/test/examArrangement")
    public Result examArrangement() {
        Integer orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
        List<Testpaper> testPapers = testPaperService.findAllByOrdersAndOrgCode(orgCode);
        return ok(examArrangement.render(testPapers));
    }

    /**
     * 上传考试
     * @return
     */
    @play.mvc.BodyParser.Of(value = play.mvc.BodyParser.Json.class,maxLength = 8 * 1024 * 1024)
    @Pattern("/admin/organizeTest/test/upload")
    public Result uploadTest() {
        List<Integer> testIds = new ArrayList<>();
        try {
            Form<OrganizeTestDto> form = Form.form(OrganizeTestDto.class);
            OrganizeTestDto organizeTestDto = form.bindFromRequest().get();
            testIds = testService.upload(organizeTestDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }
        return ok(Json.toJson(testIds));
    }

    /**
     * 组织考试--导入考生页面
     * @return
     */
    @Pattern("/admin/organizeTest/user/importView")
    public Result importUserView() {
        Integer orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
        List<String> tests = testService.findByOrgCodeAndPopulation(orgCode);
        return ok(importUser.render(tests));
    }

    /**
     * 校验上传的excel
     **/
    @Pattern("/admin/organizeTest/user/validateExcel")
    public Result validateUserExcel() {
        Http.MultipartFormData.FilePart file = request().body().asMultipartFormData().getFile("Filedata");
        String ip = request().body().asMultipartFormData().asFormUrlEncoded().get("ip")[0];
        List<UserVo> userVos = (List<UserVo>) ExcelUtils.getObjectList(file.getFile(), UserVo.class);
        List<UserVo> errorUsers = new ArrayList<UserVo>();
        List<UserVo> rightUsers = new ArrayList<UserVo>();
        userService.validateExcel(userVos, errorUsers, rightUsers, ip);

        if (errorUsers.size() > 0) {
            return ok(Json.toJson(errorUsers));
        }
        for (int i = 0; i < rightUsers.size(); i++) {
            UserVo userVo = rightUsers.get(i);
            userVo.setExcelUserError("正常");
        }
        return ok(Json.toJson(userVos));
    }

    /**
     * 通过页面传来的数据导出考生
     **/
    @Pattern("/admin/organizeTest/user/exportByData")
    public Result exportUserByData() {
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            Form<UserDto> form = Form.form(UserDto.class);
            UserDto userDto = form.bindFromRequest().get();
            file = utils.FileUtils.getPublicFile("/assets/dowdload/", "useroutput.xls");
            HSSFWorkbook userExcel = userService.export(userDto.getUserVos());
            fileOutputStream = new FileOutputStream(file);
            userExcel.write(fileOutputStream);

            response().setContentType("application/vnd.ms-excel;charset=UTF-8");
            response().setHeader("Content-disposition", "attachment; filename=" + new String("用户列表.xls".getBytes("UTF-8"), "ISO-8859-1"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(fileOutputStream != null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }catch(Exception e1){}
        }
        return ok(file);
    }

    /**
     * 保存考试与考生等信息
     **/
    @Pattern("/admin/organizeTest/user/upload")
    @play.mvc.BodyParser.Of(value = play.mvc.BodyParser.Json.class,maxLength = 8 * 1024 * 1024)
    public Result uploadUser() {
        Form<OrganizeTestDto> form = Form.form(OrganizeTestDto.class);
        OrganizeTestDto organizeTestDto = form.bindFromRequest().get();
        ResponseDto responseDto = new ResponseDto();
        List<UserVo> outputUser = testService.addTestWithUser(responseDto, organizeTestDto);
        responseDto.setResult(Json.toJson(outputUser));
        return ok(Json.toJson(responseDto));
    }
}
