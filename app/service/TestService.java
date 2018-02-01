package service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.*;
import models.*;
import models.dto.OrganizeTestDto;
import models.dto.ResponseDto;
import models.dto.UserDto;
import models.rowMapper.TestMapper;
import models.vo.TestVo;
import models.vo.UserVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.DynamicSpecifications;
import persistence.SearchFilter;
import play.mvc.Http;
import utils.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tina on 2015/8/10.
 */
@Service
public class TestService {
    @Autowired
    private TestDao testDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserTestDao userTestDao;
    @Autowired
    private AdmissionInfoDao admissionInfoDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private EduExperienceDao eduExperienceDao;
    @Autowired
    private VTestDao vTestDao;

    /**
     *
     * @param test
     */
    @Transactional
    public void save(Test test) {
        test.setDateCreated(new Date());
        test.setUseStatus("1");
        test.setStatus("0");
        testDao.save(test);
    }

    /** 修改考试
     *
     * @param objectNode
     * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
    @Transactional
    public void update(ObjectNode objectNode) {
        try {
            Test test = testDao.findOne(objectNode.findPath("tid").asInt());
            test.setName(objectNode.findPath("testName").asText());
            test.setOrgCode(objectNode.findPath("orgCode").asInt());
            test.setPid(objectNode.findPath("tpid").asInt());
            SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd hh:mm" );
            test.setStartTime(sdf.parse(objectNode.findPath("testTime").asText()));
            test.setNormData(objectNode.findPath("testNormJsonStr").asText());
            test.setPopulation(objectNode.findPath("testPopulation").asInt());
            test.setTurn(utils.StringUtils.stringToNumber(objectNode.findPath("testTurn").asText()));
            String report = objectNode.findPath("testReport").asText();
            if(report.equals("")){
                test.setReport("0");
            }else if(report.length()==3){
                test.setReport("3");
            }else {
                test.setReport(report);
            }
            testDao.save(test);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 修改考试
     *
     * @param objectNode
     * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
    @Transactional
    public void orgUpdateTest(ObjectNode objectNode) {
        try {
            Test test = testDao.findOne(objectNode.findPath("tid").asInt());
            SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
            test.setStartTime(sdf.parse(objectNode.findPath("testTime").asText()));
            testDao.save(test);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**删除考试
     *
     * @param tid 考试id
     * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
    @Transactional
    public void delete(Integer tid) {
        Test test = testDao.findOne(tid);
        test.setUseStatus("0");
        testDao.save(test);
    }

    public List<TestVo> findByOrgCode(Integer orgCode){
        return  testDao.findTestByOrgCode(orgCode);
    }

    /**
     * 获取能导考生的考试
     * @param orgCode
     * @return
     */
    public List<String> findByOrgCodeAndPopulation(Integer orgCode){
        String sql = "SELECT NAME FROM TEST\n" +
                "WHERE IMPORTED_POPULATION < POPULATION\n" +
                "AND ORG_CODE = ?\n" +
                "AND USE_STATUS = 1 GROUP BY NAME ORDER BY DATE_CREATED DESC";
        List<String> tests = jdbcTemplate.queryForList(sql, String.class, orgCode);
        return tests;
    }

    /**删除考试的学生
     *
     * @param tid 考试id
     * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
    @Transactional
    public void deleteUser(Integer tid,Integer uid) {
        UserTest userTest = userTestDao.findByTidAndUid(tid, uid);
        userTestDao.delete(userTest);
    }

    /**添加考试学生
     *
     * @param tid 考试id
     * @pdOid d5016059-5eed-4c9d-93fb-185fd611a21a */
    @Transactional
    public void addUser(Integer tid,String userStr) {
        String[] user = userStr.split(",");
        List<UserTest> userTests = new ArrayList<UserTest>();
        for (int i = 0; i < user.length; i++) {
            UserTest userTest = new UserTest();
            userTest.setTid(tid);
            userTest.setUid(Integer.parseInt(user[i]));
            userTests.add(userTest);
        }
        userTestDao.save(userTests);
    }

    /**
     * 根据ID查找考试
     * @param tid
     * @return
     */
    public Test findById(Integer tid) {
        return testDao.findOne(tid);
    }

    /**
     * 分页查找考试
     * @param request
     * @param cols 分页列表的列名对应的字段
     * @return
     */
    public Page<VTest> findAll(Http.Request request, String[] cols) {
        Pageable pageable = PageUtils.getPageRequest(request, cols);
        Specification<VTest> spec = DynamicSpecifications.fromRequest(request, VTest.class);
        return vTestDao.findAll(spec, pageable);
    }

    /**检索考试
     * @param page 页数
     * @param orgCode 招生机构
     * @param testTime 考试时间
     * @param startTime 考试起始日期
     * @pdOid 52d557af-32fa-4bf5-8ac4-473c2b72cb82 */
    public Page searchTest(Integer orgCode,String testTime,String startTime,Integer page) {
//        Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
//        StringBuilder sql = new StringBuilder("SELECT  new models.vo.TestVo(t.id,t.pid,t.name,t.start_time,t.useStatus,ao.orgName,ao.id,t.status,t.population,t.turn) FROM Test t,AdmissionsOrg ao WHERE t.orgCode = ao.id AND t.useStatus='1'");
//
//        if(orgCode!=0){
//            sql.append(" AND ao.id=" + orgCode);
//        }
//        if(!testName.equals("0")){
//            sql.append(" AND t.name='" + testName + "'");
//        }
//        if(!testTime.equals("0")){
//            sql.append(" AND date(t.start_time)='" + testTime + "'");
//        }else{
//            if(!startTime.equals("0")) {
//                sql.append(" AND date(t.start_time)>='" + startTime + "'");
//            }
//            if(!endTime.equals("0")){
//                sql.append(" AND date(t.start_time)<='" + endTime + "'");
//            }
//        }
//        return testDao.findAll(TestVo.class, pageable, sql.toString());
        Pageable pageable = new PageRequest(page-1,SystemConstant.PAGESIZE);
        StringBuilder countSqlBuilder = new StringBuilder("SELECT COUNT(*) FROM \n" +
                "(\n" +
                "        SELECT t.id tid,t.pid,t.name,t.start_time,t.use_status,ao.org_name,ao.id,t.status,t.population,t.turn,t.report\n" +
                "        FROM test t,admissions_org ao\n" +
                "        WHERE t.org_code = ao.id \n" +
                "        AND t.use_status='1' \n" +
                "        AND t.use_status=ao.use_status \n");
        StringBuilder sqlBuilder = new StringBuilder("SELECT a.tid,a.pid,a.name,a.start_time,a.use_Status,a.org_Name,a.id,a.status,a.population,a.turn,a.report, b.cou,b.sums FROM \n" +
                "(\n" +
                "        SELECT t.id tid,t.pid,t.name,t.start_time,t.use_status,ao.org_name,ao.id,t.status,t.population,t.turn,t.report\n" +
                "        FROM test t,admissions_org ao\n" +
                "        WHERE t.org_code = ao.id \n" +
                "        AND t.use_status='1' \n" +
                "        AND t.use_status=ao.use_status \n" );
        if(orgCode!=0){
            countSqlBuilder.append(" AND ao.id=" + orgCode+"\n");
            sqlBuilder.append(" AND ao.id=" + orgCode+"\n");
        }
        if(!testTime.equals("0")){
            countSqlBuilder.append(" AND date(t.start_time)='" + testTime + "'\n");
            sqlBuilder.append(" AND date(t.start_time)='" + testTime + "'\n");
        }else{
            if(!startTime.equals("0")) {
                countSqlBuilder.append(" AND date(t.start_time)>='" + startTime + "'\n");
                sqlBuilder.append(" AND date(t.start_time)>='" + startTime + "'\n");
            }
        }
        String countSql = ") a\n" +
                "left join (\n" +
                "       SELECT cc.tid,cc.cou,dd.sums\n" +
                "       FROM(\n" +
                "           SELECT ut.tid, COUNT(a.uid) cou FROM user_test ut\n" +
                "           LEFT JOIN (\n" +
                "               SELECT uid FROM answer\n" +
                "               GROUP BY uid\n" +
                "           ) a ON ut.uid = a.uid\n" +
                "           GROUP BY ut.tid\n" +
                "           ) cc,\n" +
                "       (\n" +
                "       SELECT ut.tid,COUNT(ut.tid) sums FROM user_test ut GROUP BY ut.tid\n" +
                "       )dd \n" +
                "       WHERE dd.tid=cc.tid\n" +
                "   ) b ON a.tid = b.tid\n" +
                "   GROUP BY a.tid,a.pid,a.name,a.start_time,a.use_status,a.org_name,a.id,a.status,a.population,a.turn,a.report, b.cou\n" +
                "   ORDER BY a.tid DESC\n" +
                "\n";
        countSqlBuilder.append(countSql);
        int count = jdbcTemplate.queryForList(countSqlBuilder.toString()).size();
        int start = (page-1) * SystemConstant.PAGESIZE;
        String sql = ") a\n" +
                "left join (\n" +
                "       SELECT cc.tid,cc.cou,dd.sums\n" +
                "       FROM(\n" +
                "           SELECT ut.tid, COUNT(a.uid) cou FROM user_test ut\n" +
                "           LEFT JOIN (\n" +
                "               SELECT uid FROM answer\n" +
                "               GROUP BY uid\n" +
                "           ) a ON ut.uid = a.uid\n" +
                "           GROUP BY ut.tid\n" +
                "           ) cc,\n" +
                "       (\n" +
                "       SELECT ut.tid,COUNT(ut.tid) sums FROM user_test ut GROUP BY ut.tid\n" +
                "       )dd \n" +
                "       WHERE dd.tid=cc.tid\n" +
                "   ) b ON a.tid = b.tid\n" +
                "   GROUP BY a.tid,a.pid,a.name,a.start_time,a.use_status,a.org_name,a.id,a.status,a.population,a.turn,a.report, b.cou\n" +
                "   ORDER BY a.tid DESC LIMIT "+ start +"," +  SystemConstant.PAGESIZE  +"\n" +
                "\n";
        sqlBuilder.append(sql);
        List<TestVo> testVos = jdbcTemplate.query(sqlBuilder.toString(), new TestMapper());
        PageImpl<TestVo> pageImpl = new PageImpl<TestVo>(testVos,pageable,count);
        return pageImpl;
    }

    /**检索考生
     * @param page 页数
     * @param orgCode 招生机构
     * @param name 姓名
     * @param account 帐号
     * @param email 邮箱
     * @pdOid 52d557af-32fa-4bf5-8ac4-473c2b72cb82 */
    public Page searchUser(Integer tid,Integer orgCode,String name,String account,String email,Integer page) {
        Pageable pageable = new PageRequest(page-1, SystemConstant.PAGESIZE);
        StringBuilder sql = new StringBuilder("SELECT  new models.vo.UserVo(us.id,us.name,us.account,us.password,us.email,ao.orgName,us.answerCommit) FROM UserView us, AdmissionsOrg ao,AdmissionInfo ai,UserTest ut WHERE us.id=ai.uid AND us.useStatus='1'AND ai.orgCode=ao.id AND us.useStatus=ai.useStatus AND ut.uid=us.id");
        sql.append(" AND ut.tid="+tid);
        List list = new ArrayList();
        if(orgCode!=0){
            sql.append(" AND ao.id=" + orgCode);
        }
        if(!name.equals("0")){
            sql.append(" AND us.name='" + name+"'");
        }
        if(!account.equals("0")){
            sql.append(" AND us.account='" + account+"'");
        }
        if(!email.equals("0")){
            sql.append(" AND us.email='" + email+"'");
        }
        return userDao.findAll(UserVo.class, pageable, sql.toString());
    }

    public List<TestVo> getOrgTestList(Integer orgId){
        return testDao.findTestByOrgCode(orgId);
    }

    /** 获取参加某机构的考试集合
     * @param uid 用户id
     * @pdOid 41e87140-d3f7-4261-9b61-7b17caa4ff4a
     * */
    public Page<TestVo> getUserTestList(Integer uid,Integer page){
        Pageable pageable = new PageRequest(page-1,SystemConstant.PAGESIZE);
        return testDao.findTestByUid(uid, pageable);
    }

    /**
     * 从考试向导中新建考试
     * @param organizeTestDto
     * @return
     */
    @Transactional
    public List<UserVo> addTestWithUser(ResponseDto responseDto, OrganizeTestDto organizeTestDto){
        Integer orgCode = organizeTestDto.getOrgCode();
        String testName = organizeTestDto.getTestName();
        List<UserDto> userDtos = organizeTestDto.getUserDtos();
        List<Test> tests = testDao.findAll(SearchFilter.eq("orgCode", orgCode), SearchFilter.eq("name", testName), SearchFilter.eq("useStatus", 1));
        String msg = validateImportedPopulation(userDtos, tests);
        if(StringUtils.isNotBlank(msg)){
            responseDto.setMessage(msg);
            responseDto.setSuccess(false);
            return null;
        }

        List<UserVo> userVos = new ArrayList<UserVo>();
        for (int i = 0; i < tests.size(); i++) {
            int testNumber = 0;// 考试人数
            Test test = tests.get(i);
            Integer tid = test.getId();
            Integer tpid = test.getPid();

            for (UserDto userDto : userDtos) {
                int turn = userDto.getTurn();
                if (test.getTurn() == turn) {
                    User user = userDto.getUser();
                    String idCard = user.getIdCard();
                    User oldUser = userDao.findOne(SearchFilter.eq("idCard", idCard));
                    if(oldUser != null){
                        oldUser.setName(user.getName());
                        oldUser.setSex(user.getSex());
                        oldUser.setBirthday(user.getBirthday());
                        oldUser.setAddress(user.getAddress());
                        oldUser.setPhone(user.getPhone());
                        oldUser.setEmail(user.getEmail());
                        oldUser.setUseStatus("1");
                        user = userDao.save(oldUser);
                    } else {
                        user.setDateCreated(new Date());
                        user.setUseStatus("1");
                        user.setLastLogintime(DateUtils.parseDate("1700-01-01"));
                        user.setAccount("-000000000");
                        user.setPassword(this.randomPassword(user.getIdCard()));
                        user = userDao.save(user);
                        user.setAccount(this.getAccount(user.getId()));
                        user = userDao.save(user);
                    }

                    Integer userId = user.getId();
                    if(!admissionInfoDao.exists(SearchFilter.eq("uid", userId), SearchFilter.eq("useStatus", "1"))){
                        AdmissionInfo admissionInfo = userDto.getAdmissionInfo();
                        admissionInfo.setUid(userId);
                        admissionInfo.setUseStatus("1");
                        admissionInfo.setDateCreated(new Date());
                        admissionInfoDao.save(admissionInfo);
                    }

                    if(!userTestDao.exists(SearchFilter.eq("tid", tid), SearchFilter.eq("uid", userId))){
                        UserTest userTest = new UserTest();
                        userTest.setTid(tid);
                        userTest.setUid(userId);
                        userTest.setIsover("0");
                        userTestDao.save(userTest);
                        testNumber++;
                        test.setImportedPopulation(test.getImportedPopulation() + 1);
                        testDao.save(test);
                    }

                    EduExperience eduExperience = userDto.getEduExperience();
                    if(StringUtils.isNotBlank(eduExperience.getSchoolName()) || StringUtils.isNotBlank(eduExperience.getGrade())
                            || StringUtils.isNotBlank(eduExperience.getClass_()) || StringUtils.isNotBlank(eduExperience.getStudentNum())){
                        eduExperience.setUid(userId);
                        eduExperience.setUseStatus("1");
                        eduExperience.setDateCreated(new Date());

                        EduExperience oldEduExperience = eduExperienceDao.findOne(SearchFilter.eq("uid", userId), SearchFilter.eq("useStatus", "1"));
                        if (oldEduExperience != null) {
                            oldEduExperience.setUseStatus("0");
                        }
                        eduExperienceDao.save(eduExperience);
                    }

                    userVos.add(new UserVo(turn, user, eduExperience, userDto.getAdmissionInfo()));
                }
            }

            Orders order = ordersDao.findOne(SearchFilter.eq("tpid", tpid), SearchFilter.eq("status", "1"), SearchFilter.eq("orgCode", orgCode));
            order.setRemainNumber(order.getRemainNumber() - testNumber);
            ordersDao.save(order);
        }
        responseDto.setSuccess(true);
        return userVos;
    }

    /**
     * 组织考试上传考试
     * @param organizeTestDto
     * @return
     */
    @Transactional
    public List<Integer> upload(OrganizeTestDto organizeTestDto){
        List<Integer> testIds = new ArrayList<>();
        List<Test> tests = organizeTestDto.getTests();
        if (tests != null && tests.size() > 0) {
            for(Test test: tests){
                test.setReport("");
                test.setNormData("[{\"qtid\":\"1\",\"qType\":\"材料记忆\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"2\",\"qType\":\"符号运算\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"3\",\"qType\":\"顺序记忆\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"4\",\"qType\":\"图案搜索\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"5\",\"qType\":\"图形连线\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"8\",\"qType\":\"类比推理\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"7\",\"qType\":\"材料回忆\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"6\",\"qType\":\"段落推理\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"9\",\"qType\":\"矩阵推理\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"10\",\"qType\":\"人格\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"11\",\"qType\":\"家庭\",\"average\":\"11\",\"standard\":\"11\"},{\"qtid\":\"12\",\"qType\":\"空间旋转\",\"average\":\"11\",\"standard\":\"11\"}]");
                test.setDateCreated(new Date());
                test.setStatus("0");
                test.setUseStatus("1");
            }
            try{
                tests = testDao.save(tests);
            } catch (Exception e) {
                throw new RuntimeException();
            }
            for(Test test: tests){
                testIds.add(test.getId());
            }
        }
        return testIds;
    }

    /**
     * 生成密码
     * @param idCard
     * @return
     */
    private String randomPassword(String idCard){
        String result= idCard.substring(idCard.length() - 6, idCard.length());
        return MD5Utils.getMD5Code(result);
    }

    /**
     * 生成帐号
     * @param id
     * @return
     */
    private String getAccount(Integer id) {
        String result = "1000000";
        return Integer.toString(Integer.parseInt(result) + id);
    }

    /**
     * 验证考试名称是否存在
     * @param name
     * @param orgCode
     * @return
     */
    public String validateName(String name, Integer orgCode){
        if(testDao.exists(SearchFilter.eq("name", name), SearchFilter.eq("orgCode", orgCode))){
            return "此考试名称已创建过！";
        }
        return "";
    }

    /**
     * 验证要导入的考生是否超出订单数量
     * @return
     */
    public String validateImportedPopulation(List<UserDto> userDtos, List<Test> tests){

        Integer num = 0;
        Map<Integer, Integer> group = new HashMap<>();
        for(UserDto userDto: userDtos){
            int turn = userDto.getTurn();
            num = group.get(turn);
            if(num == null){
                num = 0;
            }
            group.put(turn, ++num);
        }
        String errorMsg = "";

        for (Integer key : group.keySet()) {
            Boolean flag = false;
            for(Test test: tests) {
                Integer turn = test.getTurn();
                if(key.equals(turn)){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                errorMsg += "；您选择的考试中没有第" + key + "轮";
            }
        }
        for(Test test: tests){
            Integer turn = test.getTurn();
            int population = test.getPopulation();
            int importedPopulation = test.getImportedPopulation();
            num = group.get(turn);

            if(num != null && population - importedPopulation < num) {
                errorMsg += "；第" + turn + "轮考试还能导入" + (population - importedPopulation) + "名考生，您上传的excel超出了此范围";
            }
        }
        if(errorMsg != ""){
            return errorMsg.substring(1);
        }
        return errorMsg;
    }

    public int getNumFromStr(String text){
        if(StringUtils.isBlank(text)){
            return 0;
        } else {
            String str = text.replaceAll("[^0-9]ig","").trim();
            if(StringUtils.isBlank(str)){
                return 0;
            } else {
                return Integer.parseInt(str);
            }
        }
    }
}

