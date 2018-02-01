package controllers;

import models.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import play.mvc.Controller;
import service.AnswerReportService;
import service.SessionService;
import service.TestpaperQuestiontypeService;
import controllers.routes;
import service.UserService;
import utils.SystemConstant;
import views.html.exam.common.adminssionCard;


/**
 * Created by XIAODA on 2015/7/23.
 */
@org.springframework.stereotype.Controller
public class IndexController extends Controller {

    @Autowired
    private TestpaperQuestiontypeService testpaperQuestiontypeService;
    @Autowired
    private AnswerReportService answerReportService;
    @Autowired
    private UserService userService;

    /**
     * 测试页面头部-个人信息
     * @return
     */
    public Result adminssionCard(){
        UserVo userVo = userService.findUserVoByAccount(SessionService.getSessionItem(SessionService.SessionItemMark.LOGIN_ACCOUNT).toString());
        return ok(adminssionCard.render(userVo));
    }

    /**
     * 根据所传参数找到将要跳转的页面
     * @param qType 当前页面所对应的题型
     * @return
     */
    public Result forwardTest(Integer qType){

        Integer tpid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TPID);// 试卷id
        if("0".equals(qType)){
            Integer uid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.USER_ID);// 考生id
            Integer tid = SessionService.getSessionItemInteger(SessionService.SessionItemMark.TEST_ID);// 考试表id
            qType = answerReportService.findQtypeLastCommit(uid, tid);
        }
        int next_qtype = testpaperQuestiontypeService.findNextQtypeByQtype(tpid, qType);
        switch (next_qtype){
            case 1:
                return redirect(routes.MaterialMemoryController.instructions(SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY));
            case 2:
                return redirect("/symbolicOperation/instructions");// 符号运算
            case 3:
                return redirect(routes.SpatialMemoryController.instructions());
            case 4:
                return redirect(routes.GrapfSearchController.instructions());
            case 5:
                return redirect(routes.ShapeLinkingController.instructions());
            case 6:
                return redirect(routes.ParagraphReasoningController.instructions());// 段落推理
            case 7:
                return redirect(routes.MaterialMemoryController.instructions(SystemConstant.QUESTION_TYPE_MATERIAL_EXTRACT));
            case 8:
                return redirect(routes.AnalogicReasoningController.instructions());// 类比推理
            case 9:
                return redirect(routes.MatrixReasoningController.instructions());// 矩阵推理
            case 10:
                return redirect(routes.PersonalityController.instructions());// 人格测试
            case 11:
                return redirect(routes.FamilyQuestionnaireController.instructions());// 空间旋转
            case 12:
                return redirect(routes.SpaceRotationController.instructions());// 家庭环境问卷
            case 13:
                return redirect(routes.EmotionRecognitionController.instructions());// 情绪识别
            case 14:
                return redirect(routes.EmotionUnderstandingController.instructions());// 情绪理解
            case 15:
                return redirect(routes.RemoteAssociationController.instructions());// 远距离联想
            case 16:
                return redirect("/criticalThinking/ability/home/instructions");// 远距离联想
            case 17:
                return redirect("/criticalThinking/tendency/instructions");// 远距离联想
            default:
                return redirect(routes.MaterialMemoryController.instructions(SystemConstant.QUESTION_TYPE_MATERIAL_MEMORY));// FIXME 还未想好默认的url
        }
    }
}
