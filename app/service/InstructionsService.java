package service;

import dao.QuestionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.SystemConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XIAODA on 2015/8/11.
 */
@Service
public class InstructionsService {

    @Autowired
    private QuestionTypeService questionTypeService;

    /**
     * 获取测试系统整体介绍页面的缓存文件配置信息
     * @return
     */
    public List<String> getIndexManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.INSTRUCTIONS_HEAD_CACHE);
        cacheList.add("/assets/images/common/logo_xinli.png");
        return cacheList;
    }

    /**
     * 获取测试系统操作说明页面的缓存文件配置信息
     * @return
     */
    public List<String> getInstructionsManifest(){
        List<String> cacheList = new ArrayList<String>();
        cacheList.addAll(SystemConstant.TEST_HEAD_CACHE);
        for (int i = 1; i <= 4; i++) {
            cacheList.add("/assets/images/common/testInstructions" + i + ".png");
        }
        cacheList.add("/assets/javascripts/exam/routes.js");
        cacheList.add("/assets/enjoyhint/enjoyhint.min.js");
        cacheList.add("/assets/enjoyhint/enjoyhint.css");
        cacheList.add("/assets/images/common/clock1.png");
        cacheList.add("/assets/images/common/exchange1.png");
        cacheList.add("/assets/images/common/tag1.png");
        cacheList.add("/assets/images/common/mark.png");
        cacheList.add("/assets/images/common/seemark.png");
        cacheList.add("/assets/images/common/smallClock.png");
        cacheList.add("/assets/enjoyhint/Casino_Hand/casino_hand-webfont.woff");
        return cacheList;
    }
}
