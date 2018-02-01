var qidArray = qidsStr.split("!");// 本次测试中此类型测试所有题的id组成的数组
var questionArray = questionsStr.split("!");// 本次测试中此类型测试所有题组成的数组
var question = questionArray[0];// 题（每道题中包含要点亮每一个小方格的坐标）
var coordinates = question.split(";");// 坐标（行：列）

var nowquestion = 3;
var prequestion = 3;
var currentIndex = 0;// 当前测试题在questionArray中的位置，默认从第一题开始
var rightNum = 0;// 每组题中点对的个数（一个级别为一组，一组三个题）
var idx=0;// 当前coordinates中，第idx个坐标的方块为正确答案

var answerList = [];
var json = {};// 用来存储answer对象，组成一个answer的list
$(function () {
    setTimeout(startTest, millisecond);

    /**
     * 开始测试
     */
    var i = 0;// i 每道题第几个方块显示
    var testError = false;// testError 是否测试出错
    function startTest() {
        if(i < coordinates.length) {
            $("#stable").find("tr").find("td").css("background", "#A6A6A6");
            var l = coordinates[i].split(":")[0];// 行
            var c = coordinates[i].split(":")[1];// 列
            setTimeout(startTest, 800);
            $("#stable").find("tr").eq(l - 1).find("td").eq(c - 1).css("background", "#BEEBDF");
            i++;
        } else {
            finalPick();
        }
    }

    /**
     * 每题的最后一块的显示
     */
    function finalPick() {
        for (var j = 0; j < coordinates.length; j++) {
            var l = coordinates[j].split(":")[0];
            var c = coordinates[j].split(":")[1];
            $("#stable").find("tr").eq(l - 1).find("td").eq(c - 1).css("background", "#A6A6A6");
            $("#content").html("请按照同样的顺序点亮方块");
            if (!isTest) {
                $("#stable").find("tr").eq(l - 1).find("td").eq(c - 1).css({
                    "background": "#A6A6A6"
                }).html(j + 1);
                if (currentIndex == questionArray.length - 1) {
                    $("#stable").find("tr").find("td").html("");
                }
            }
            if(isTest && j == coordinates.length - 1){
                saveAnswerDetailByLocalStorage("", "5");// 记录顺序记忆每一个试次的熄灭时间
            }
        }
        if (isTest) {
            bindClickTest();
        } else {
            bindClickPractice();
        }
    }
    /**
     * 绑定考试页面中点击方块的事件
     */
    function bindClickTest() {
        $(".square").each(function (index) {
            $(this).click(function () {
                var clickl = Math.floor(index / 5 + 1);// 被点击方块的行
                var clickc = index % 5 + 1;// 被点击方块的列
                var l = coordinates[idx].split(":")[0];// 应点击的行
                var c = coordinates[idx].split(":")[1];// 应点击的列

                var answer = clickl + ":" + clickc + ";";// 用户选择的答案（每一个所选方块的坐标以 列：行；列：行；...的形式拼接成的串）
                var qid = qidArray[currentIndex];
                var curent_time = curentTime();// 当前系统时间
                if (json[qid] == undefined) {
                    json[qid] = {
                        "uid":uid,
                        "tid":tid,
                        "qid":qid,
                        "answer":answer,
                        //"answerDetail":[
                        //    {
                        //        "answer":answer,
                        //        "clickTime": curent_time
                        //    }
                        //],
                        "qtype":qtype,
                        "type":0,// 材料回忆测试中，type代表：考生在切换前是否对题目进行了标记（是-1，否-0）
                        "clickNum":1,
                        "clickTime": curent_time
                    };
                } else {
                    json[qid]["answer"] += answer;
                    json[qid]["clickNum"] ++;
                    json[qid]["clickTime"] = curentTime();
                    //json[qid]["answerDetail"].push({"answer": answer, "clickTime": curent_time});
                }
                saveAnswerDetailByLocalStorage(answer, "4");// 记录答题详情

                if ($(this).attr("ischeck") == 0) {

                } else {
                    nowquestion = Math.floor(currentIndex / 3) + 3;
                    if (clickl == l && clickc == c) {// 如果点击正确
                        $(this).css("background", "#BEEBDF");
                        $(this).attr("ischeck", 0);
                        if (++idx == coordinates.length) {// 如果本题最后一个小方块点击正确
                            json[qid]["rightNum"] = "1";
                            answerList.push(json[qid]);
                            setTimeout(clearPick, 800);
                        }
                    } else {
                        $(".square").each(function (index) {
                            $(this).unbind("click");
                        })
                        $("#stable").find("tr").eq(clickl - 1).find("td").eq(clickc - 1).css("background", "black");
                        testError = true;
                        json[qid]["rightNum"] = "0";
                        answerList.push(json[qid]);
                        if ((currentIndex % 3 + 1 == 3 && rightNum < 1) || currentIndex+1 == questionArray.length) {
                            submitAnswer();
                        }
                        setTimeout(clearPick, 800);
                    }
                    if (prequestion != nowquestion) {
                        rightNum = 0;
                    }
                }
            })
        })

    }

    /**
     * 绑定练习页面中点击方块的事件
     */
    function bindClickPractice() {
        $(".square").each(function (index) {
            $(this).click(function () {
                var clickl = Math.floor(index / 5 + 1);// 被点击方块的行
                var clickc = index % 5 + 1;// 被点击方块的列
                var l = coordinates[idx].split(":")[0];// 应点击的行
                var c = coordinates[idx].split(":")[1];// 应点击的列

                if ($(this).attr("ischeck") == 0) {
                    $(this).unbind("click");
                } else {
                    if (clickl == l && clickc == c) {// 如果点击正确
                        clearTishi(idx);
                        $(this).css("background", "#BEEBDF");
                        $(this).attr("ischeck", 0);
                        if (++idx == coordinates.length) {// 如果本题最后一个小方块点击正确
                            sign("right", clickl - 1, clickc - 1);// 反馈正误
                            setTimeout(clearPick, 800);
                        }
                    } else {
                        testError = true;
                        $(".square").each(function (index) {
                            $(this).unbind("click");
                        })
                        $("#stable").find("tr").eq(clickl - 1).find("td").eq(clickc - 1).css("background", "black");
                        sign("wrong", clickl - 1, clickc - 1);// 反馈正误
                        setTimeout(clearPick, 800);
                    }
                }
            })
        })
    }

    /**
     * 清除选择并切换下个试次
     */
    function clearPick(testError) {
        idx = 0;
        i = 0;
        $(".square").each(function (index) {
            $(this).unbind("click");
        })
        $("#stable").find("tr").find("td").css("background", "#A6A6A6");
        $("#stable").find("tr").find("td").attr("ischeck", 1);
        $("#stable").find("tr").find("td").html("");
        $("#wrong").css("display", "none");
        $("#right").css("display", "none");
        restart();
    }


    /**
     * 清除提示信息
     */
    function clearTishi(idx) {
        for (var j = 0; j < coordinates.length; j++) {
            var l = coordinates[j].split(":")[0];
            var c = coordinates[j].split(":")[1];
            if (idx == 0) {
                $("#stable").find("tr").eq(c - 1).find("td").eq(l - 1).css("background", "#A6A6A6").html("");
            }
        }
    }
    /**
     * 切换试次
     *@param l 行
     *@param c 列
     */
    function restart() {
        if (!testError) {
            currentIndex++;
            rightNum++;
            question = questionArray[currentIndex];
        } else {
            if (isTest) {
                currentIndex++;
                question = questionArray[currentIndex];
            }
        }
        prequestion = nowquestion;
        if (currentIndex == questionArray.length && !isTest) {
            location.replace('/spatialMemory/practiceEnd?tid=' + tid);
        }
        if (currentIndex == questionArray.length && isTest) {
            submitAnswer()
        }
        $("#content").html("请记住方块点亮的顺序");
        testError = false;
        coordinates = question.split(";");
        setTimeout(startTest, 800);
    }

    /**
     * 反馈正误
     * @param line 行
     * @param column 列
     */
    function sign(id, line, column) {
        var square_td = $("#stable").find("tr").eq(line).find("td").eq(column);
        var X = square_td.position().top;
        var Y = square_td.position().left;
        $("#" + id).css({
            "display": "block",
            "left": Y + 35,
            "top": X - 20,
            "position": "absolute"
        });
    }
})

/**
 * 提交答案
 */
function submitAnswer() {
    var json_str = JSON.stringify(answerList);
    var url = "/spatialMemory/submitAnswer ";
    var href = "/spatialMemory/endTime?tid=" + tid;// 提交成功后跳转到此页面
    var flag = true;// 是否要提示断网信息,并尝试重新提交答案
    if (isOffLine == true) {
        submitAnswerByLocalStorage(json_str, href);
    } else {
        submitAnswerByAjax(url, json_str, href, flag);
    }
}

/**
 * 将考试答题详情保存到LocalStorage
 * @param pageIndex
 * @param answer
 * @param operationType
 */
function saveAnswerDetailByLocalStorage(answer, operationType){
    var qid = qidArray[currentIndex];
    pushAnswerDetailArray(qid, answer, 1, operationType);
    var data = JSON.stringify(answerDetailArray);// 提交的json数据
    if(data.length > 2){
        localStorage.setItem("answerDetail_" + userInfoStr, data);
    }
}



