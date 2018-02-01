package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.dto.OrderDto;
import models.vo.OrdersVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.MailUtils;
import utils.PageUtils;
import utils.SystemConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tina on 2015/8/10.
 */
@Service
public class OrdersService {

    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private TestpaperDao testpaperDao;
    @Autowired
    private TestpaperQuestiontypeDao testpaperQuestiontypeDao;
    @Autowired
    private TestpaperQuestionDao testpaperQuestionDao;
    @Autowired
    private AdminDao adminDao;

    @Transactional
    public void save(OrderDto orderDto){
        Orders order = orderDto.getOrder();
        order.setDateCreated(new Date());
        order.setStatus("2");
        if(order.getTpid() == 0){
            Testpaper testpaperReference = testpaperDao.findOne(SearchFilter.eq("useStatus", "1"), SearchFilter.eq("isReference", "1"), SearchFilter.eq("orgCode", "0"));
            List<Integer> testpaperQuestionTypeList = orderDto.getQuestiontypeList();
            List<TestpaperQuestiontype> testpaperQuestiontypes = testpaperQuestiontypeDao.findAll(SearchFilter.eq("tpid", testpaperReference.getId()), SearchFilter.in("qtid", testpaperQuestionTypeList));
            StringBuilder testpaperQuestionStrBuilder =  new StringBuilder("SELECT a FROM TestpaperQuestion a,Question b WHERE a.tpid="+ testpaperReference.getId() + " AND a.qid=b.id AND b.qtype IN (?1)");
            List<TestpaperQuestion> testpaperQuestions = testpaperQuestionDao.findAll(testpaperQuestionStrBuilder.toString(), testpaperQuestionTypeList);
            Testpaper testpaper = new Testpaper();
            testpaper.setDateCreated(new Date());
            testpaper.setExpectTime(testpaperReference.getExpectTime());
            testpaper.setName(orderDto.getTestpaperName());
            testpaper.setUseStatus("2");
            testpaper.setOrgCode(order.getOrgCode());
            testpaper.setIsReference("0");
            testpaper = testpaperDao.save(testpaper);

            List<TestpaperQuestiontype> newTestpaperQuestiontypes = new ArrayList<>();
            List<TestpaperQuestion> newTestpaperQuestions = new ArrayList<>();
            for (int i = 0; i < testpaperQuestiontypes.size(); i++) {
                TestpaperQuestiontype testpaperQuestiontype = testpaperQuestiontypes.get(i);
                TestpaperQuestiontype newTestpaperQuestiontype = new TestpaperQuestiontype();
                newTestpaperQuestiontype.setQtid(testpaperQuestiontype.getQtid());
                newTestpaperQuestiontype.setTpid(testpaper.getId());
                newTestpaperQuestiontype.setSeq(i+1);
                newTestpaperQuestiontypes.add(newTestpaperQuestiontype);
            }
            for (int i = 0; i < testpaperQuestions.size(); i++) {
                TestpaperQuestion testpaperQuestion = testpaperQuestions.get(i);
                TestpaperQuestion newTestpaperQuestion = new TestpaperQuestion();
                newTestpaperQuestion.setQid(testpaperQuestion.getQid());
                newTestpaperQuestion.setTpid(testpaper.getId());
                newTestpaperQuestions.add(newTestpaperQuestion);
            }
            testpaperQuestionDao.save(newTestpaperQuestions);
            testpaperQuestiontypeDao.save(newTestpaperQuestiontypes);
            order.setTpid(testpaper.getId());
            order.setTestpaperName(orderDto.getTestpaperName());
        } else {
            Testpaper testpaper = testpaperDao.findOne(order.getTpid());
            order.setTestpaperName(testpaper.getName());
        }
        ordersDao.save(order);
    }

    @Transactional
    public Orders updateStatus(int orderId, String status) {
        try {
            Orders order = ordersDao.findOne(orderId);
            order.setStatus(status);
            ordersDao.save(order);

            Testpaper testpaper = testpaperDao.findOne(order.getTpid());
            testpaper.setUseStatus(status);
            testpaperDao.save(testpaper);
            return order;
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    public Orders findByTpidAndOrgCode(Integer tpid, Integer orgCode){
        return ordersDao.findOne(SearchFilter.eq("orgCode", orgCode), SearchFilter.eq("tpid", tpid));
    }

    /**
     * 分页查找订单
     * @param request
     * @param cols 分页列表的列名对应的字段
     * @return
     */
    public Page<Orders> findAll(Http.Request request, String[] cols) {
        Pageable pageable = PageUtils.getPageRequest(request, cols);
        Specification<Orders> spec = DynamicSpecifications.fromRequest(request, Orders.class);
        return ordersDao.findAll(spec, pageable);
    }

    @Transactional
    public void delete(int orderId) {
        Orders order = ordersDao.findOne(orderId);
        Integer tpid = order.getTpid();
        Testpaper testpaper = testpaperDao.findOne(tpid);
        if(!"1".equals(testpaper.getIsReference())){
            testpaperQuestiontypeDao.delete(SearchFilter.eq("tpid", tpid));
            testpaperDao.delete(tpid);
        }
        ordersDao.delete(order);
    }

    /**
     * 根据id查找订单
     * @param orderId
     * @return
     */
    public Orders findOne(int orderId) {
        return ordersDao.findOne(orderId);
    }

    /**
     * 发送邮件通知审核通过
     * @param order
     */
    public void approvedEmail(Orders order){
        Integer adminId = order.getAdminId();
        Admin admin = adminDao.findOne(adminId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("尊敬的");
        stringBuilder.append(admin.getName()+"(先生/女士)：<br /><br />");
        stringBuilder.append("恭喜您！您的订单申请已通过。<br /><br />");
        stringBuilder.append("---------------------------<br />");
        stringBuilder.append("中国心理与认知能力测评管理平台<br />");
        stringBuilder.append("Chinese psychological and cognitive ability test management platform<br />");

        MailUtils.send(SystemConstant.EMAIL_SMTP_ADDRESS, SystemConstant.EMAIL_SEND_FROM_USER, admin.getEmail(), "订单被拒绝", stringBuilder.toString(), SystemConstant.EMAIL_LOGIN_ACCOUNT, SystemConstant.EMAIL_LOGIN_PWD);
    }

    /**
     * 发送邮件通知审核被拒绝
     * @param order
     */
    public void refusedEmail(Orders order){
        Integer adminId = order.getAdminId();
        Admin admin = adminDao.findOne(adminId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("尊敬的");
        stringBuilder.append(admin.getName()+"(先生/女士)：<br /><br />");
        stringBuilder.append("非常抱歉，您的申请因缺少资料没有通过，请发邮件至“ceping@fcbrains.com”与工作人员联系。<br /><br />");
        stringBuilder.append("---------------------------<br />");
        stringBuilder.append("中国心理与认知能力测评管理平台<br />");
        stringBuilder.append("Chinese psychological and cognitive ability test management platform<br />");

        MailUtils.send(SystemConstant.EMAIL_SMTP_ADDRESS, SystemConstant.EMAIL_SEND_FROM_USER, admin.getEmail(), "订单被拒绝", stringBuilder.toString(), SystemConstant.EMAIL_LOGIN_ACCOUNT, SystemConstant.EMAIL_LOGIN_PWD);
    }
}

