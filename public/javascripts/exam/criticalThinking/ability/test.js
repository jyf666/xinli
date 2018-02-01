/**
 * Created by XIAODA on 2016/3/24.
 */
var userInfo = {
    "uid" : uid,
    "tid" : tid,
    "qtype" : qtype,
    "subType" : subType
}
var userInfoStr = JSON.stringify(userInfo);

var daojishi = function(){
    hours= Math.floor(maxtime/60/60);
    minutes = Math.floor((maxtime-hours*60*60)/60);
    seconds = Math.floor(maxtime%60);
    msg = minutes+"分"+seconds+"秒";
    $(".time_cart").html(msg);
    if( maxtime >= 10 ){
        --maxtime;
        var exdate = new Date();
        exdate.setTime(exdate.getTime() + 120 * 60 * 1000);
        setCookie("maxTime_" + uid + "_" + qtype, maxtime + ";path=/criticalThinking/ability;", 120);
    }else if(maxtime <= 10 && maxtime > 0){
        $(".time_cart").html(msg);
        $(".time_cart").css("color","red");
        --maxtime;
        setCookie("maxTime_" + uid + "_" + qtype, maxtime + ";path=/criticalThinking/ability;", 120);
    } else{
        clearInterval(timer);
        submitAnswer();
    }
}

var submitAnswer = function(){
    submitAns("/criticalThinking/ability/endTime?tid=" + tid);
}

var submitAns = function(href){
    $(".marking").each(function(index){
        var qid = $(this).attr("data");
        if (json[qid] != undefined) {
            json[qid].type = $(this).attr("mark");
        }
    })

    for(var key in json){
        answerList.push(json[key]);
    }
    var json_str = JSON.stringify(answerList);// 提交的json数据
    var url = "/criticalThinking/ability/submitAnswer";// ajax提交的地址
    var flag = true;// 是否要提示断网信息,并尝试重新提交答案
    if(Boolean(isOffLine)){
        submitAnswerByLocalStorage(json_str, href);
    } else {
        submitAnswerByAjax(url, json_str, href, flag);
    }
}