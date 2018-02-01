package controllers;

import be.objectify.deadbolt.java.actions.Pattern;
import models.*;

import models.dto.OrderDto;
import models.dto.ResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import service.*;
import utils.FileUtils;
import utils.PageUtils;
import utils.SystemConstant;
import views.html.admin.ordersManage.orderAdd;
import views.html.admin.ordersManage.orderList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/** 考试控制
 *
 * @pdOid 13e917c5-e997-4e0a-8be0-2caf2a9cec9d
 * */
@org.springframework.stereotype.Controller
public class OrdersController extends Controller {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private TestPaperService testPaperService;
    @Autowired
    private QuestionTypeService questionTypeService;

    /**
     * 订单列表页面
     * @return
     */
    public Result listView() {
        return ok(orderList.apply());
    }

    /**
     * 获取订单分页数据
     * @return
     */
    public Result page() {
        String draw = request().getQueryString("draw"); //获取请求次数
        String[] cols = {"id", "id", "testpaperName", "dateCreated", "testNumber", "remainNumber", "startTime", "endTime", "ageLow", "ageUpp", "status"};// 定义列名
        Page<Orders> page = ordersService.findAll(request(), cols);
        return ok(PageUtils.convertToTableData(page, draw));
    }

    /**
     * 追加订单
     * @return
     */
    @Pattern("/admin/orders/addView")
    public Result addView() {
        List<Questiontype> questiontypes = questionTypeService.getByIsReference();
        Integer orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
        Integer[] orgCodes = {orgCode, 0};
        List<Testpaper> testpapers = testPaperService.findAllByOrgCodes(Arrays.asList(orgCodes));
        return ok(orderAdd.render(questiontypes, testpapers));
    }

    /**
     * 添加订单
     * @return
     */
    public Result add() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        OrderDto orderDto = new OrderDto();

        if(body == null){
            Form<OrderDto> orderDtoForm = Form.form(OrderDto.class);
            orderDto = orderDtoForm.bindFromRequest().get();
        } else {
            play.mvc.Http.MultipartFormData.FilePart picture = body.getFile("agreement_img");

            Form<Orders> orderForm = Form.form(Orders.class);
            Orders order = orderForm.bindFromRequest().get();

            Map<String, String[]> param = body.asFormUrlEncoded();
            String testpaperName = param.get("testpaperName")[0];
            String questiontypeStr = param.get("questiontypeStr")[0];
            List<Integer> questiontypeList = null;
            if(StringUtils.isNotBlank(questiontypeStr)){
                String[] questiontypeArr = questiontypeStr.substring(0, questiontypeStr.length() - 1).split(",");
                for (int i = 0; i < questiontypeArr.length; i++) {
                    questiontypeList.add(Integer.valueOf(questiontypeArr[i]));
                }
//                questiontypeList = Arrays.asList(questiontypeStr.substring(0, questiontypeStr.length() - 1).split(","));
            }

            String agreement = "";
            if(picture != null){
                try {
                    agreement = FileUtils.encodeBase64File(picture.getFile());
                    order.setAgreement(agreement);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            orderDto = new OrderDto(order, testpaperName, questiontypeList);
        }

        ResponseDto responseDto = new ResponseDto();
        if(!testPaperService.existTestpaper(orderDto.getQuestiontypeList())){
            try {
                ordersService.save(orderDto);
                responseDto.setSuccess(true);
            } catch (DataIntegrityViolationException e){
                responseDto.setMessage("上传的文件异常，请注意图片不能超过65k");
                responseDto.setSuccess(false);
                e.printStackTrace();
            } catch (Exception e) {
                responseDto.setMessage("保存失败");
                responseDto.setSuccess(false);
                e.printStackTrace();
            }
        } else {
            responseDto.setMessage("已有试卷与此试卷所含内容一致，请使用已有试卷");
            responseDto.setSuccess(false);
        }
        return ok(Json.toJson(responseDto));
    }

    /**
     * 审核通过
     * @param orderId 订单id
     * @return
     */
    public Result approved(int orderId){
        Orders order = ordersService.updateStatus(orderId, "1");
        ordersService.approvedEmail(order);
        return ok();
    }

    /**
     * 拒绝审核
     * @param orderId 订单id
     * @return
     */
    public Result refused(int orderId){
        Orders order = ordersService.updateStatus(orderId,"3");
        ordersService.refusedEmail(order);
        return ok();
    }

    /**
     * 通过试卷获取机构订单中剩余考生人数
     * @param tpId
     * @return
     */
    public Result getRemainNumber(Integer tpId){
        Integer orgCode = SessionService.getSessionItemInteger(SessionService.SessionItemMark.ORG_CODE);
        Orders order = ordersService.findByTpidAndOrgCode(tpId, orgCode);
        return ok(Json.toJson(order));
    }

    /**
     * 删除订单
     * @param orderId 订单id
     * @return
     */
    public Result delete(int orderId){
        try {
            ordersService.delete(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return ok(SystemConstant.FAIL);
        }
        return ok(SystemConstant.SUCCESS);
    }

}
