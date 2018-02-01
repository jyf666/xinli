/**
 * Created by XIAODA on 2015/9/12.
 */
var uidlist = new Array();
$(function(){
    var storage = window.localStorage;
    for(var i=0;i<storage.length;i++){
        var key = storage.key(i);
        if(key.split("_")[1]){
            var user = JSON.parse(key.split("_")[1]);
            if(user.uid && uidlist.indexOf(user.uid) < 0){
                uidlist.push(user.uid);
            }
        }
    }
    if(uidlist.length>0){
        ajaxPost(1);// 获取本地缓存里保存的离线考生名单
    }
    $(".submit").each(function(){
        $(this).click(function(){
            var answer = new Array();
            var answerReport = new Array();
            var answerDetail = new Array();
            var keyArr = new Array();
            var uid = this.id;

            for(var i=0;i<storage.length;i++){
                var key = storage.key(i);
                var type = key.split("_")[0];// answer or answerReport
                var keyJ = key.split("_")[1];
                if(keyJ){
                    if(!isJson(keyJ)) continue;
                    var user = JSON.parse(keyJ);
                    if(uid == user.uid) {
                        var value = storage.getItem(key);
                        if(!isJson(value)) continue;
                        var json = JSON.parse(value);
                        if ("answerReport" == type){
                            answerReport.push(json);
                        } else if("answer" == type){
                            for(var j=0;j<json.length;j++){
                                answer.push(json[j]);
                            }
                        } else if ("answerDetail" == type){
                            for(var j=0;j<json.length;j++){
                                answerDetail.push(json[j]);
                            }
                        }
                        keyArr.push(key);
                    }
                }
            }
            var jsonData = {
                answers:answer,
                answerReports:answerReport,
                answerDetails:answerDetail
            }
            var data = JSON.stringify(jsonData);
            submitAnswer(data, keyArr);
            window.location.reload();
        })
    })
})

function submitAnswerAll(){
    var storage = window.localStorage;
    var answer = new Array();
    var answerReport = new Array();
    var answerDetail = new Array();
    var keyArr = new Array();
    for(var i=0;i<storage.length;i++){
        var key = storage.key(i);
        var type = key.split("_")[0];// answer or answerReport

        var value = storage.getItem(key);
        if(isJson(value)){
            var json = JSON.parse(value);
            if ("answerReport" == type){
                answerReport.push(json);
            } else if("answer" == type){
                for(var j=0;j<json.length;j++){
                    answer.push(json[j]);
                }
            } else if ("answerDetail" == type){
                for(var j=0;j<json.length;j++){
                    answerDetail.push(json[j]);
                }
            }
            keyArr.push(key);
        }
    }

    var jsonData = {
        answers:answer,
        answerReports:answerReport,
        answerDetails:answerDetail
    }
    var data = JSON.stringify(jsonData);
    submitAnswer(data, keyArr);
    window.location.reload();
}

function submitAnswer(data, keyArr){
    $('#my-modal-loading').modal();
    $.ajax({
        url:"/answer/upload",
        timeout: 50000,
        async:false,
        type:"post",
        contentType:"application/json",
        data:data,
        success:function(result){
            var storage = window.localStorage;
            for(var i=0;i<keyArr.length;i++){
                var key = keyArr[i];
                storage.removeItem(key);
            }
            $('#my-modal-loading').modal('close');
        },
        error:function(result){
            alert("失败！");
            $('#my-modal-loading').modal('close');
        }
    });
}

function ajaxPost(pageNum){
    $.ajax({
        url:"/user/localStorageUser/" + pageNum,
        timeout: 1000,
        async:false,
        type:"post",
        contentType:"application/json",
        data:JSON.stringify(uidlist),
        success:function(data){
            var userlist = data.content;
            var totalElements = data.totalElements;
            var totalPages = data.totalPages;
            createPagination(totalElements, totalPages, data.number, data.firstPage, data.lastPage);
            createPageContent(userlist);
        },
        error:function(){
            alert("失败");
        }
    });
}

/**
 * 创建分页内容
 * param userlist 考生列表
 */
function createPageContent(userlist){
    var pageContentHtml = "";
    for(var i = 0; i < userlist.length; i++){
        var user = userlist[i];
        var sex = user.sex == 0 ? "女" : "男";
        pageContentHtml += "<tr><td><input type='checkbox' /></td>" +
            "<td>"+(i+1)+"</td>" +
            "<td>"+ user.name +"</td>" +
            "<td>"+ sex +"</td>" +
            "<td>"+ user.birthday +"</td>" +
            "<td>"+ user.phone +"</td>" +
            "<td>"+ user.email +"</td>" +
            "<td>"+ user.idCard +"</td>" +
            "<td><div class='am-btn-group am-btn-group-xs'>" +
            "<button id='" + user.id + "' type='button' onclick='' class='am-btn am-btn-default am-btn-xs am-text-secondary submit'><span class='am-icon-save'></span> 提交</button></div></td></tr>";
    }
    $("#pageContent").html(pageContentHtml);
}

/**
 * 创建分页组件
 * param totalElements 元素总数
 * param totalPages 总页数
 * param number 当前页序号（等于当前页码-1）
 * param firstPage 是否是第一页
 * param lastPage 是否是最后一页
 */
function createPagination(totalElements, totalPages, number, firstPage, lastPage){
    var pageNum = number + 1;
    $("#totalElements").html(totalElements);
    if(totalElements>0){
        var paginationHtml = "";
        if(totalPages > 1 && !firstPage){
            paginationHtml += '<li id="pre"><a href="#" onclick="ajaxPost('+ (pageNum-1) + ');">«</a></li>';
        }
        for(var i=0;i<totalPages;i++){
            if(i == number){
                paginationHtml += '<li class="am-active"><a href="#" onclick="ajaxPost('+ (pageNum + 1) + ');">' + (i+1) + '</a></li>';
            } else {
                paginationHtml += '<li><a href="#" onclick="ajaxPost('+ (i + 1) + ');">' + (i+1) + '</a></li>';
            }
        }
        if(totalPages>1 && !lastPage){
            paginationHtml += '<li id="next"><a href="#"  onclick="ajaxPost('+ (pageNum + 1) + ');">»</a></li>';
        }
        $("#pagination").html(paginationHtml);
    }
}

/***** 判断是否为json对象 *******
 * param obj: 对象
 * return isjson: 是否是json对象 true/false
 */
 function isJson(obj){
    try{
        JSON.parse(obj);
    } catch(e){
        return false;
    }
    return true;
}