package service;

import dao.QuestionDao;
import models.Question;
import models.bo.ShapeLinkingQuestion;
import models.bo.ShapeLinkingQuestion.ShapeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.SystemConstant;
import utils.json.JsonMapper;

import java.util.*;

/**
 * Created by mare on 15/7/20.
 */

@Service
public class ShapeLinkingService extends QuestionService{

    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取形状连线说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/shapeLinking/instSample.png");
        cacheList.add("/assets/images/shapeLinking/circleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/rectangleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/plusSymbol.png");
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取形状连线练习页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.PRACTICE_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");

        for (int i = 1; i <= 8; i++) {
            cacheList.add("/assets/images/shapeLinking/a_circle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/a_rectangle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/a_plus" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_circle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_rectangle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_plus" + i + ".png");
        }
        cacheList.add("/assets/images/shapeLinking/circleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/rectangleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/plusSymbol.png");
        cacheList.add("/assets/javascripts/exam/shapelinking/shapelinking.js");

        cacheList.add("/assets/enjoyhint/enjoyhint.min.js");
        cacheList.add("/assets/enjoyhint/enjoyhint.css");
        cacheList.add("/assets/enjoyhint/Casino_Hand/casino_hand-webfont.woff");
        return cacheList;
    }

    /**
     * 获取形状连线练习结束说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getPracticeEndManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        cacheList.add("/assets/images/common/feedback1.png");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange1.png");
        return cacheList;
    }

    /**
     * 获取形状连线考试页面的缓存文件配置信息
     * @return
     */
    public List<String> getTestManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        cacheList.add("/assets/javascripts/question/test.js");

        for (int i = 1; i <= 8; i++) {
            cacheList.add("/assets/images/shapeLinking/a_circle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/a_rectangle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/a_plus" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_circle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_rectangle" + i + ".png");
            cacheList.add("/assets/images/shapeLinking/b_plus" + i + ".png");
        }
        cacheList.add("/assets/images/shapeLinking/circleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/rectangleSymbol.png");
        cacheList.add("/assets/images/shapeLinking/plusSymbol.png");
        cacheList.add("/assets/javascripts/exam/shapelinking/shapelinking.js");
        return cacheList;
    }

    /**
     * 根据机构编码和题型查询题目
     *
     * @param qType
     * @return
     */
    public List<Map<String,List<Map<String, String>>>> findQuestions(Integer tpid, Integer qType,String ispractice){
        List<Map<String,List<Map<String, String>>>> questionList = new ArrayList<Map<String,List<Map<String, String>>>>();

        List<Question> questions = questionDao.findByTpidAndQTypeAndIspractice(tpid, qType, ispractice);
        for (Question question : questions) {
            String ques = question.getQuestion();

            Map questionMap = new HashMap();
            JsonMapper jsonMapper = new JsonMapper();
            List list = jsonMapper.fromJson(ques, List.class);
            List imgList = new ArrayList();
            int count = list.size();// 选项个数
            for (int i = 0; i < count; i++) {
                Map map = (Map)list.get(i);
                String number = map.get("number").toString();
                String shape = map.get("shape").toString();
                String location =  map.get("location").toString();
                String[] arr = location.split("\\*");
                double width = (750d/890d)*Double.valueOf(arr[0]);
                double height = (350d/490d)*Double.valueOf(arr[1]);

                Map imgMap = new HashMap();
                imgMap.put("number", number);
                imgMap.put("shape", shape);
                imgMap.put("width", String.valueOf(width));
                imgMap.put("height", String.valueOf(height));
                imgList.add(imgMap);
            }
            questionMap.put("count",count);// 选项个数
            questionMap.put("qid",question.getId());
            questionMap.put("imgList",imgList);
            questionList.add(questionMap);
        }
        return questionList;
    }

    /**
     * 生成图形连线考试题
     * @param ispractice
     * @param questionNum
     * @param groupNum
     * @return
     */
    public List<Question> generateShapeLinking(String ispractice, int questionNum, int groupNum) {
        JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
        List<Question> questionList = new ArrayList<Question>(questionNum);
        for (int i = 1; i <= questionNum; i++) {
            List<ShapeLinkingQuestion> shapeLinkingQuestionList = getShapeLinking(groupNum);
            String ques = jsonMapper.toJson(shapeLinkingQuestionList);
            Question question = new Question();
            question.setQuestion(ques);// 测试题目
            question.setAnswer("");// 参考答案
            question.setQtype(SystemConstant.QUESTION_TYPE_SHAPE_LINKING);// 题目类型
            question.setIspractice(ispractice);// 子类型
            question.setDateCreated(new Date());// 创建日期
            questionList.add(question);
        }
        if(!questionList.isEmpty()){
            return questionList;
        }
        return null;
    }

    /**
     * 获取图形连线题目
     * @param groupNum
     * @return
     */
    public List<ShapeLinkingQuestion> getShapeLinking(int groupNum) {

        final int shapeTypeSize = ShapeType.values().length;
        final int shapeLinkingQuestionSize = groupNum * shapeTypeSize;
        List<ShapeLinkingQuestion> shapeLinkingQuestionList = new ArrayList<>(shapeLinkingQuestionSize);

        List<List<String>> positionTemplateList = getPositionTemplateList();
        int randomNum = new Random().nextInt(positionTemplateList.size());
        List<String> positionTemplate = positionTemplateList.get(randomNum);
        Collections.shuffle(positionTemplate);
        for (int i = 0; i < shapeLinkingQuestionSize; i++) {
            int shapeNo = i % shapeTypeSize;
            int num = i / shapeTypeSize + 1;
            ShapeLinkingQuestion shapeLinkingQuestion = new ShapeLinkingQuestion(num, ShapeType.values()[shapeNo].toString(), positionTemplate.get(i));
            shapeLinkingQuestionList.add(shapeLinkingQuestion);
        }
        return shapeLinkingQuestionList;
    }

    /**
     * 获取图形连线的位置模板
     * @return
     */
    private List getPositionTemplateList(){
        List<List<String>> positionTemplateList = new ArrayList<List<String>>(18);
        positionTemplateList.add(Arrays.asList(new String[]{"283*378","683*107","356*155","110*378","206*115","486*158","761*420","90*255","79*101","656*256","390*295","834*280","614*432","546*326","828*62"}));
        positionTemplateList.add(Arrays.asList(new String[]{"536*234","826*408","674*313","363*92","208*358","123*246","650*176","711*64","550*86","399*227","266*195","538*389","784*231","137*76","54*345"}));
        positionTemplateList.add(Arrays.asList(new String[]{"97*280","434*137","58*54","246*121","512*324","634*341","813*260","749*156","211*426","551*70","636*220","363*257","807*406","410*419","69*411"}));
        positionTemplateList.add(Arrays.asList(new String[]{"75*150","80*438","793*87","308*223","684*141","360*90","754*302","452*282","207*110","575*256","338*439","173*322","603*52","561*415","516*149"}));
        positionTemplateList.add(Arrays.asList(new String[]{"525*54","196*348","732*149","509*322","823*66","343*271","80*234","59*407","632*246","342*413","404*59","758*364","158*134","517*182","625*397"}));
        positionTemplateList.add(Arrays.asList(new String[]{"502*157","126*388","667*373","780*431","739*50","622*228","116*180","275*279","370*373","147*55","531*312","283*89","836*135","754*277","533*434"}));
        positionTemplateList.add(Arrays.asList(new String[]{"103*439","197*307","446*112","716*162","572*241","317*268","533*368","714*409","608*85","319*109","152*112","837*245","322*430","70*255","440*258"}));
        positionTemplateList.add(Arrays.asList(new String[]{"135*194","515*228","789*164","432*68","641*269","283*353","765*331","403*303","152*327","655*424","262*119","614*65","524*375","93*66","64*428"}));
        positionTemplateList.add(Arrays.asList(new String[]{"255*182","660*370","436*372","77*170","570*235","821*299","641*54","290*395","814*70","822*421","174*358","441*165","711*243","322*50","63*406"}));
        positionTemplateList.add(Arrays.asList(new String[]{"707*153","368*267","835*404","64*186","792*67","238*175","273*399","481*99","94*430","166*57","430*403","542*316","736*297","699*431","174*281"}));
        positionTemplateList.add(Arrays.asList(new String[]{"107*210","810*360","59*52","827*230","104*335","477*312","296*83","747*98","233*201","587*65","638*202","455*121","312*310","685*414","391*431"}));
        positionTemplateList.add(Arrays.asList(new String[]{"582*261","246*123","396*251","122*337","247*410","118*148","689*151","547*98","279*291","424*119","407*377","696*380","769*267","840*412","560*395"}));
        positionTemplateList.add(Arrays.asList(new String[]{"823*228","644*83","104*158","315*300","686*436","74*356","563*347","191*277","770*85","553*178","411*428","218*73","440*131","321*154","697*287"}));
        positionTemplateList.add(Arrays.asList(new String[]{"696*429","446*288","98*404","220*410","579*370","446*150","320*335","578*221","160*176","301*207","734*65","722*269","281*83","540*57","840*147"}));
        positionTemplateList.add(Arrays.asList(new String[]{"138*272","482*259","761*418","582*107","773*290","281*259","660*205","387*407","223*400","74*405","197*120","560*407","798*97","457*89","75*163"}));
        positionTemplateList.add(Arrays.asList(new String[]{"611*417","151*117","306*407","635*85","106*440","413*308","512*111","775*209","186*242","752*359","647*261","56*315","806*56","335*121","427*440"}));
        positionTemplateList.add(Arrays.asList(new String[]{"408*212","685*150","386*384","98*415","813*147","269*298","566*376","459*68","87*201","594*57","58*68","765*369","562*237","257*161","220*421"}));
        positionTemplateList.add(Arrays.asList(new String[]{"454*236","85*168","823*267","209*394","762*440","578*83","529*432","351*436","708*175","193*107","811*53","328*80","96*436","576*204","296*287"}));
        return positionTemplateList;
    }
}
