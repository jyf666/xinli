var answerCookies = new Array();
var markCookies = new Array();
function finish321(){console.log("开始321");}// 此方法要在页面中实现
function initPage(){console.log("初始化页面");}// 此方法要在页面中实现
$(function(){
    if(!self.frameElement || self.frameElement.name != "iframeLoad"){
        if(localStorage.getItem("answerReport_" + userInfoStr)) {
            if(maxtime && maxtime>0){
                finish321();// 倒数321
            }
            initPage();// 初始化页面
        } else {
            saveAnswerDetailByLocalStorage("", "1");// 记录呈现题目的时间
            if(maxtime && maxtime>0){
                countBackwards();// 倒数321
            }
        }
        if(Boolean(isOffLine)){
            recordStartTimeByLocalStorage();
        } else {
            recordStartTimeByAjax();
        }
    }
    /** 为标记按钮绑定onclick事件 */
    if($(".marking").length > 0){
        bindClickForMark(marksrc, markedsrc);
    }
})

/** 判断缓存里是否有该考生的倒计时记录，如果有则读取缓存数据 **/
var maxTimeTemp = getCookieByName("maxTime_" + uid + "_" + qtype);
if(maxTimeTemp && maxTimeTemp>0){
    maxtime = maxTimeTemp;
}

var userInfo = {
    "uid" : uid,
    "tid" : tid,
    "qtype" : qtype
}
var userInfoStr = JSON.stringify(userInfo);
var answerDetailArray = new Array();// 答题详情

/**
 * 点击考试页面中的问号，从问号左侧缓慢弹出提示信息
 */
function showReminder(){
    $("#wenhao").animate({width:'toggle'},"slow");
}

/**
 * 将考试答案保存到LocalStorage
 * @param json
 */
function saveAnswerByLocalStorage(json){
    $(".marking").each(function(index){
        var qid = $(this).attr("data");
        if (json[qid] != undefined) {
            json[qid].type = $(this).attr("mark");
        }
    })

    var answerArr = new Array;
    for(var key in json){
        answerArr.push(json[key]);
    }
    var data = JSON.stringify(answerArr);// 提交的json数据
    if(data.length > 2){
        localStorage.setItem("answer_" + userInfoStr, data);
        setCookie("answerJsonCookies", JSON.stringify(json), 120);
        //document.cookie = "answerJsonCookies=" + JSON.stringify(json);
    }
}

/**
 * 将考试答题详情保存到LocalStorage
 * @param pageIndex
 * @param answer
 * @param operationType
 */
function saveAnswerDetailByLocalStorage(answer, operationType){
    var pageNum = getPageNum();// 当前页码
    if(localStorage.getItem("answerDetail_" + userInfoStr)){
        answerDetailArray = JSON.parse(localStorage.getItem("answerDetail_" + userInfoStr));
    }
    var qid = $("#content-index >div").eq(pageNum - 1).attr("data");
    if(qid){
        pushAnswerDetailArray(qid, answer, pageNum, operationType);
    } else {
        pushAnswerDetailArray(0, answer, pageNum, operationType);
    }
    var data = JSON.stringify(answerDetailArray);// 提交的json数据
    if(data.length > 2){
        localStorage.setItem("answerDetail_" + userInfoStr, data);
    }
}

/**
 * 记录答题详情
 * param qid
 * param answer
 * param operationType
 */
function pushAnswerDetailArray(qid, answer, pageNum, operationType){
    answerDetailArray.push({
        "uid":uid,
        "tid":tid,
        "qid":qid,
        "answer":answer,
        "qtype":qtype,
        "fragenummer":pageNum,
        "operationType":operationType,
        "operationTime": new Date().getTime()
    });
}

/**
 *　将答案保存到本地
 * @param uid 考生id
 * @param qtype 题型
 * @param data 答案
 * @param href 保存后跳转到此页面
 */
function submitAnswerByLocalStorage(data, href){
    if(data.length > 2){
        var storage = window.localStorage;
        if (!storage.getItem(("answer_" + userInfoStr))) {
            localStorage.setItem("answer_" + userInfoStr, data);
        }
    }
    recordEndTimeByLocalStorage();
    clearCookies();
    location.replace(href);
}

/**
 * 提交答案
 * url ajax提交的地址
 * json_str 提交的json数据
 * href 提交成功后跳转到此页面
 * flag 是否要提示断网信息,并尝试重新提交答案
 *
 */
function submitAnswerByAjax(url, data, href, flag){
    if(navigator.onLine){
        $.ajax({
            url:url,
            timeout: 1000,
            async:false,
            type:"post",
            contentType:"application/json",
            data:data,
            success:function(result){
                recordEndTimeByAjax(href);
            },
            error:function(){
                if(flag){
                    alert("答案提交失败，请您不要关闭浏览器，我们正在尝试为您重新提交...");
                    submitAnswerByAjax(url, data, href, false);
                } else {
                    submitAnswerByLocalStorage(data, href);
                    recordEndTimeByLocalStorage();
                    location.replace(href);
                }
            }
        });
    } else {
        submitAnswerByLocalStorage(data, href);
        recordEndTimeByLocalStorage();
        location.replace(href);
    }
}

/**
 * 记录考试答题开始的时间,保存到本地
 * @param data
 * @param href
 */
function recordStartTimeByLocalStorage(){
    var userInfoStr = JSON.stringify(userInfo);
    var storage = window.localStorage;
    if (!storage.getItem(("answerReport_" + userInfoStr))) {
        var data = {
            "uid": uid ,
            "tid": tid ,
            "qtype": qtype ,
            "startTime": curentTime()
        };
        storage.setItem("answerReport_" + userInfoStr, JSON.stringify(data));
    }
}

/**
 * 记录考试答题结束的时间,保存到本地
 * @param data
 * @param href
 */
function recordEndTimeByLocalStorage(){
    var userInfoStr = JSON.stringify(userInfo);
    var storage = window.localStorage;
    if (storage.getItem(("answerReport_" + userInfoStr))) {
        var data = storage.getItem("answerReport_" + userInfoStr);
        if (data.indexOf("startTime") > 0 && data.indexOf("endTime") == -1) {
            var jsonData = JSON.parse(data);
            jsonData.endTime = curentTime();
            storage.setItem("answerReport_" + userInfoStr, JSON.stringify(jsonData));
        }
    } else {
        var data = {
            "uid": uid ,
            "tid": tid ,
            "qtype": qtype ,
            "endTime": curentTime()
        };
        storage.setItem("answerReport_" + userInfoStr, JSON.stringify(data));
    }
}

/**
 * 记录考试答题开始的时间
 */
function recordStartTimeByAjax(){
    if(navigator.onLine) {
        $.ajax({
            url: "/answerReport/add/" + qtype,
            timeout: 1000,
            async: false,
            type: "post",
            success: function (result) {

            },
            error: function () {
                recordStartTimeByLocalStorage();
            }
        });
    } else {
        recordStartTimeByLocalStorage();
    }
}

/**
 * 记录考试答题结束的时间
 */
function recordEndTimeByAjax(href){
    if(navigator.onLine) {
        $.ajax({
            url: "/answerReport/add/" + qtype,
            timeout: 1000,
            async: false,
            type: "post",
            success: function (result) {
                location.replace(href);
            },
            error: function () {
                recordEndTimeByLocalStorage();
                location.replace(href);
            }
        });
    } else {
        recordEndTimeByLocalStorage();
        location.replace(href);
    }
}

/**
 * 倒数3,2,1
 */
function countBackwards(){
    setTimeout('$(".num_cart").html(3);', 200);
    setTimeout('$(".num_cart").html(2);', 1200);
    setTimeout('$(".num_cart").html(1);', 2200);
    setTimeout('finish321();', 3200);
}

/**
 * 将考试过程中所选的答案保存到cookie中
 * @param answer
 */
function setAnswerCookies(answer){
    var pageNum = getPageNum();// 当前页码
    answerCookies[pageNum-1] = answer;
    setCookie("testingPage", pageNum, 120);
    setCookie("answerCookies", JSON.stringify(answerCookies), 120);
}

/**
 * 将考试过程中所选的答案保存到cookie中
 * @param answer
 */
function setAnswerCookiesByIndex(answer, index){
    console.log(index);
    var pageNum = getPageNum();// 当前页码
    if(answerCookies[pageNum-1] != undefined){
        answerCookies[pageNum-1][index] = answer;
    } else {
        var answerArr = new Array();
        answerArr[index] = answer;
        answerCookies[pageNum-1] = answerArr;
    }
    setCookie("testingPage", pageNum, 120);
    setCookie("answerCookies", JSON.stringify(answerCookies), 120);
}

/**
 * 将考试过程中所做的标记保存到cookie中
 * @param answer
 */
function setMarkCookies(mark){
    var pageNum = getPageNum();// 当前页码
    markCookies[pageNum-1] = mark;
    setCookie("markCookies", JSON.stringify(markCookies), 120);
}

/**
 * 设置当前显示的页面
 **/
function showPageByCookie(testingPage){
    $("#content-index").children().css("display","none");
    $("#content-index >div").eq(testingPage - 1).css("display","block");
}

/**
 * 设置书签
 **/
function showMarkByCookie(markImgSrc){
    if(getCookieByName("markCookies")){
        markCookies = JSON.parse(getCookieByName("markCookies"));
    }
    for(var i = 0; i < markCookies.length; i++) {
        var mark = markCookies[i];
        if(mark != null && mark == 1 ){
            var markDom = $("#content-index >div").eq(i).find('.marking')
            markDom.attr("mark","1");
            markDom.attr("src",markImgSrc);
            markDom.attr("isMark","true");
        }
    }
}

/**
 * 设置当前页的向右箭头是绿色还是灰色
 **/
function showArrowRightByCookie(testingPage) {
    var circles = $("#content-index >div").eq(testingPage - 1).find('div[name="circle_visible"]');
    if (circles.length > 0) {
        $(".arrow-right", window.parent.document).attr("onclick", "show_next()");
        $(".arrow-right", window.parent.document).css("border-left", "50px solid #48C9A3");
    }
}

/**
 * 设置页码
 **/
function showPageNumByCookie(testingPage){
    var pageNum = testingPage + "/" + ($("#content-index >div").length);
    $("#page_content").html(pageNum);
}

/**
 * 为标记按钮绑定onclick事件
 * @param marksrc
 * @param markedsrc
 */
function bindClickForMark(marksrc, markedsrc){
    $(".marking").each(function(index){
        $(this).click(function(){
            if ($(this).attr("isMark") == "false") {
                $(this).attr("mark","1");
                $(this).attr("src", markedsrc);
                $(this).attr("isMark","true");
                setMarkCookies(1);
            } else {
                $(this).attr("src", marksrc);
                $(this).attr("isMark","false");
                setMarkCookies(0);
            }
        })
    })
}

/**
 * 清除cookie
 */
function clearCookies() {
    var cookieNames = new Array("testingPage", "answerCookies", "answerJsonCookies", "markCookies", "pageNum_showed", "maxTime_" + uid + "_" + qtype, "answerEnd");
    for (var i = 0; i < cookieNames.length; i++) {
        if (getCookieByName(cookieNames[i]) != undefined) {
            setCookie(cookieNames[i], "", -24 * 60);
        }
    }
}
