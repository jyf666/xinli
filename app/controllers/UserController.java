/***********************************************************************
 * Module:  UserController.java
 * Author:  XIAODA
 * Purpose: Defines the Class UserController
 ***********************************************************************/

package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import models.*;
import models.vo.TestVo;
import models.vo.UserVo;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import service.*;
import utils.PageUtils;
import utils.SystemConstant;
import views.html.admin.userManage.userList;
import views.html.admin.userManage.regList;
import views.html.admin.userManage.uploadUser;
import views.html.admin.userManage.userInfo;
import views.html.admin.userManage.userAdd;
import views.html.admin.userManage.userListTest;
import views.html.admin.userManage.userEdit;
import views.html.admin.userManage.listpdf;
import views.html.admin.userManage.showpdf;
import views.html.admin.userManage.showAbstract;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;


/** 考生控制
 *
 * @pdOid cb7c85df-08f9-4530-9e6b-1e1658140ab5 */
@org.springframework.stereotype.Controller
public class UserController extends Controller {

    @Autowired
    private UserService userService;
    @Autowired
    private AdmissionsOrgService admissionsOrgService;
    @Autowired
    private TestService testService;
    @Autowired
    private PersonalReportService personalReportService;

    private static List<User> generatePDFUsers = new ArrayList<>(); //要生成pdf报告的用户

    private static List<TestVo> generateTestsPDF = new ArrayList<>();//要生成pdf报告的考试

    public Result generatePDF(Integer orgCode){

        generateTestsPDF = testService.findByOrgCode(orgCode);
        generatePDFUsers =  userService.findByTid(generateTestsPDF.get(0).getId(),orgCode);

        return redirect(controllers.routes.UserController.listPDF(orgCode, generateTestsPDF.get(0).getId(), generatePDFUsers.get(0).getId()));

    }

    /**
     * 添加考生页面
     *  @param tid 考试id
     *  @param orgCode 招生机构
     **/
    public Result addView(Integer tid,Integer orgCode) {
        return ok(userAdd.render(tid, orgCode));
    }

    public Result showUserAbstract(Integer orgCode,Integer tid,Integer uid) {
        UserVo userVo =  userService.findUserVoByUid(uid);
        //用户的pdf数据
        Map<String,Object> pdfData = personalReportService.getUserAbstract(uid,tid,orgCode);
        return ok(showAbstract.render(orgCode, userVo, pdfData));

    }

    public Result showUserPDF(Integer orgCode,Integer tid,Integer uid) {
        UserVo userVo =  userService.findUserVoByUid(uid);
        //用户的pdf数据
        Map<String,Object> pdfData = personalReportService.getUserPdfData(uid,tid,orgCode);
        return ok(showpdf.render(orgCode, userVo, pdfData));

    }

    public Result listPDF(Integer orgCode,Integer tid,Integer uid) {
        UserVo userVo =  userService.findUserVoByUid(uid);
        //用户的pdf数据
        Map<String,Object> pdfData = personalReportService.getUserPdfData(uid,tid,orgCode);
        return ok(listpdf.render(orgCode, userVo, pdfData));

    }

    @play.mvc.BodyParser.Of(value = play.mvc.BodyParser.Json.class,maxLength = 16 * 1024 * 1024)
    public Result PDF(Integer orgCode) {
        try {
            Document document = new Document(PageSize.A4,0,0,0,0);
            PdfWriter.getInstance(document, new FileOutputStream("E://bb_"+ generatePDFUsers.get(0).getId()+".pdf"));
            document.open();
            JsonNode jsonNode = request().body().asJson();
            Iterator iterator = jsonNode.iterator();
            Integer i = 1;
            while (iterator.hasNext()){
                ObjectNode objectNode = (ObjectNode)iterator.next();
                byte[] byt = Base64.decodeBase64(objectNode.findPath("tupian").asText().substring(22, objectNode.findPath("tupian").asText().length()));
                FileOutputStream fos = new FileOutputStream("E://test_"+ i+ ".png");
                fos.write(byt);
                fos.flush();
                fos.close();
                this.cutImage("E://test_" + i + ".png", "E://test_" + i + ".png", 140, 0, 1810, 2800);
                Image image = Image.getInstance("E://test_"+ i+ ".png");
                if(i==1){
                    image.scalePercent(33);
                }else{
                    image.scalePercent(30);
                }
                document.add(image);
                if(i ==1){
                    document.setMargins(26.0F,0,2.0F,0);
                }
                document.newPage();
                i++;
            }
            document.close();
        }catch (Exception e) {
            e.printStackTrace();
        }


        generatePDFUsers.remove(0);
        if(generatePDFUsers.size() ==0 && generateTestsPDF.size()!=0){
            generateTestsPDF.remove(0);
            if( generateTestsPDF.size()!=0) {
                generatePDFUsers = userService.findByTid(generateTestsPDF.get(0).getId(), orgCode);
            }
        }


        if(generatePDFUsers.size() ==0 && generateTestsPDF.size()==0){
            return ok("success");
        }
        return ok(generateTestsPDF.get(0).getId().toString()+","+generatePDFUsers.get(0).getId().toString());
    }

    /** 添加考生
     *
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     * */
    public Result add() {
        ObjectNode objectNode = (ObjectNode)request().body().asJson();
        userService.save(objectNode);
        return ok();
    }

    /** 修改考生页面
     * @param  uid 用户id
     * @pdOid d613e481-922a-4361-b8a6-9368eb518772
     * */
    @Pattern("/admin/user/editView")
    public Result editView(Integer uid) {
        UserVo userVo = userService.getUserVoByUid(uid);
        userVo.setUid(uid);
        return ok(userEdit.render(userVo));
    }

    /**
     * 删除考生
     * @return
     */
    @Pattern("/admin/user/delete")
    public Result delete() {
        try {
            Map<String, String[]> form = request().body().asFormUrlEncoded();
            if (form != null) {
                String userId = form.get("userId")[0];
                userService.updateUseStatus(Integer.valueOf(userId), "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }
        return ok(SystemConstant.SUCCESS);
    }

    /**
     * 考生列表页面
     * @return
     */
    @Pattern("/admin/user/listView")
    public Result listView() {
        return ok(userList.apply());
    }

    /**
     * 获取考生分页数据
     * @return
     */
    @Pattern("/admin/user/page")
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "name", "orgName", "account", "password", "email"};// 定义列名

        Page<VUserInfo> page = userService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /**
     * 考生资料页面
     * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
     * */
    @Pattern("/admin/user/userInfo")
    public Result userInfoView(Integer uid) {
        UserVo userVo = userService.getUserVoByUid(uid);
        return ok(userInfo.render(userVo));
    }


    /** 考生参加过的考试页面
     *
     * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
     * */
    public Result userTestView(Integer uid,Integer page) {
        Page<TestVo> pageContent = testService.getUserTestList(uid, page);
        String userName = userService.findById(uid).getName();

        List<TestVo> tests = pageContent.getContent();
        Long totalNum = pageContent.getTotalElements();
        Integer totalPage = pageContent.getTotalPages();
        Integer currentPage = page;
        return ok(userListTest.render(tests,totalNum,userName,totalPage,currentPage,uid));
    }

    /** 审核报名考生页面
     *  @param page 页数
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     * */
    public Result applyListView(Integer page) {
        List<AdmissionsOrg> admissionsOrgs = admissionsOrgService.findAll();
        Page<UserVo> pageContent = userService.getUserRegList(page);

        List<UserVo> users = pageContent.getContent();
        Long totalNum = pageContent.getTotalElements();
        Integer totalPage = pageContent.getTotalPages();
        Integer currentPage = page;
        return ok(regList.render(users,totalNum,totalPage,currentPage));
    }


    /**
     * 通过报名审核
     * @param uid
     * @return
     */
    public Result receiveApplyUser(Integer uid) {
        userService.updateUseStatus(uid, "1");
        return ok();
    }

    /**批量添加用户
     * @param tid 试卷id
     * @param orgCode 招生机构
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     * */
    public Result addByExcel(Integer tid,Integer orgCode) {
        Http.MultipartFormData.FilePart file = request().body().asMultipartFormData().getFile("Filedata");
        userService.addByExcel(tid, orgCode, file.getFile());
        return ok();
    }

    /** 根据招生机构代码查找对应考试
     * @param orgCode 招生机构
     * @pdOid 62ffcf7a-4a66-4ec7-8a66-7f26a2b8cc65 */
    public Result orgListTest(Integer orgCode) {
        List<TestVo> tests = testService.getOrgTestList(orgCode);
        JsonNode ss = Json.toJson(tests);
        return ok(ss);
    }

    /** 上传用户界面
     *
     * @pdOid 62ffcf7a-4a66-4ec7-8a66-7f26a2b8cc65 */
    public Result uploadView() {
        List<AdmissionsOrg> admissionsOrgs = admissionsOrgService.findAll();
        return ok(uploadUser.render(admissionsOrgs));
    }



    /** 未通过报名审核
     * @param uid 用户id
     * @pdOid aabbf4ae-d87b-42d7-8613-943bd4437cb5
     */
    public Result rejectApplyUser(Integer uid) {
        userService.updateUseStatus(uid, "3");
        return ok();
    }

    /** 修改考生
     *
     * @pdOid b3e0b918-8d6d-492f-9c54-80e25654ab21
     * */
    @Pattern("/admin/user/edit")
    public Result edit() {
        ObjectNode objectNode = (ObjectNode)request().body().asJson();
        userService.update(objectNode);
        return ok();
   }


    /** 注册用户接口（需要注意设置用户的未审核用户状态）
     *
     * @pdOid d374a5c3-008a-4686-a1e1-4803bb497e74 */
    public Result register() {
        // TODO: implement
        return null;
    }

    /**
     * 离线答案提交考生列表
     * @param pageNum 页码
     * */
    public Result localStorageUser(int pageNum) {

        JsonNode uidListJson = request().body().asJson();
        Iterator iterator = uidListJson.iterator();
        List<Integer> uidList = new ArrayList<Integer>();
        while(iterator.hasNext()){
            IntNode uidNode = (IntNode)iterator.next();
            uidList.add(uidNode.intValue());
        }
        Page<User> page = userService.findPageByIds(pageNum, uidList);
        return ok(Json.toJson(page));
    }

    /**
     * 裁剪图片
     * @param src 源图片地址
     * @param dest 目标图片地址
     * @param x x坐标
     * @param y y坐标
     * @param w 宽度
     * @param h 高度
     * @throws IOException
     */
    private void cutImage(String src,String dest,int x,int y,int w,int h) throws IOException {

        Iterator iterator = ImageIO.getImageReadersByFormatName("png");
        ImageReader reader = (ImageReader)iterator.next();
        InputStream in= new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        java.awt.Rectangle rect = new java.awt.Rectangle(x, y, w,h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0, param);
        ImageIO.write(bi, "png", new File(dest));

    }

}