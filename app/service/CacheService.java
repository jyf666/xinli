package service;

import models.Questiontype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.cache.Cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XIAODA on 2015/12/3.
 */
@Service
public class CacheService {

    @Autowired
    private QuestionTypeService questionTypeService;

    public void initCache(){

        /* 将所有题型对应的时间装入map中并保存在缓存里 */
        Map qtypeTimeMap = new HashMap<>();
        List<Questiontype> questiontypeList = questionTypeService.getAllQuestionType();
        for(Questiontype questiontype:questiontypeList){
            Integer qtype = questiontype.getId();
            Integer time = questiontype.getLimitTime();
            qtypeTimeMap.put(qtype, time);
        }
        Cache.set("qtypeTimeMap", qtypeTimeMap);
    }
}
