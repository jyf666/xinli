var isError = false;//上传是否出错标志
function importFirstPre(){
    $("#guide").find("span").removeClass("jiacu")
    $("#guide").find("span").eq(2).addClass("jiacu")

    $("#arrange").css("display","block");
    $("#importFirst").css("display","none");
}
function importFirstNext(){
    var user_error_count = $("#user_error").find("tbody").find("tr").length;  //用户错误表格中的tr个数
    if ($("#userFilename").val() == "" || $("#userFilename").val() == null || $("#userFilename").val() == undefined) {
        am_alert("请选择上传文件！");
    } else if ((user_error_count != undefined && user_error_count != 0) || isError) {
        am_alert("数据异常，请检查后重新上传！");
    } else {
        var user_output_count = $("#user_output").find("tbody").find("tr").length;  //用户导出表格中的tr个数
        if (user_output_count == 0) {
            $('#my-confirm').modal({
                onConfirm: function () {
                    $('#my-modal-loading').modal()
                    var userDtos = new Array();

                    var keys = new Array("turn","name","sex","grade","class_","excelBirth","idCard","address","phone","email","testRoom","testNum", "ip");
                    $("#user_right").find("tbody").find("tr").each(function (index) {
                        var user = {
                            "name": $(this).find("td").eq(1).html(),
                            "sex": $(this).find("td").eq(2).html(),
                            "birthday": $(this).find("td").eq(3).html(),
                            "idCard": $(this).find("td").eq(4).html(),
                            "address": $(this).find("td").eq(5).html(),
                            "phone": $(this).find("td").eq(10).html(),
                            "email": $(this).find("td").eq(11).html()
                        };
                        var admissionInfo = {
                            "orgCode": orgCode,
                            "orgName": orgName,
                            "testNum": $(this).find("td").eq(13).html(),
                            "ip":$(this).find("td").eq(14).html(),
                            "testRoom":$(this).find("td").eq(12).html()
                        };
                        var eduExperience = {
                            "schoolName":$(this).find("td").eq(6).html(),
                            "grade":$(this).find("td").eq(7).html(),
                            "class_":$(this).find("td").eq(8).html(),
                            "studentNum":$(this).find("td").eq(9).html()
                        }
                        var userDto = {
                            "turn": getNumFromStr($(this).find("td").eq(0).html()),
                            "user": user,
                            "admissionInfo": admissionInfo,
                            "eduExperience": eduExperience
                        }
                        userDtos.push(userDto);
                    });

                    var organizeTestDto = {
                        "orgCode":orgCode,
                        "testName":$("#test").val(),
                        "userDtos":userDtos
                    };

                    $.ajax({
                        url: "/admin/organizeTest/user/upload",
                        timeout: 1000,
                        async: false,
                        type: "POST",
                        contentType:"application/json",
                        data: JSON.stringify(organizeTestDto),
                        success: function (responseDto) {
                            var success = responseDto.success;
                            if(Boolean(success)){
                                var users = responseDto.result;
                                var size = 0;
                                if (users != null) {
                                    if (users.length > 0 && users.length < 30) {
                                        size = users.length;
                                    } else if (users.length >= 30) {
                                        size = 30;
                                        var importInfo_divHtml = "<span>共计导入<span>" + users.length + "</span>名考生，考生列表的示例如下。示例仅包括前30人，若需查看完整考生列表，请点击“<a href='download/user.xlsx'>考生管理</a>”。</span>";
                                        $("#importInfo_div").html(importInfo_divHtml);
                                    }
                                    for (var i = 0; i < size; i++) {
                                        var password = users[i].idCard.toString();
                                        password = password.substring((password.length - 6), password.length);
                                        $("#user_output").find("tbody").append("<tr>" + getColHtml(users[i].turn) + getColHtml(users[i].name) + getColHtml(users[i].account) + getColHtml(password) + getColHtml(users[i].sex)
                                        + getColHtml(users[i].excelBirth) + getColHtml(users[i].idCard) + getColHtml(users[i].address) + getColHtml(users[i].schoolName) + getColHtml(users[i].grade) + getColHtml(users[i].class_)
                                        + getColHtml(users[i].studentNum) + getColHtml(users[i].phone) + getColHtml(users[i].email) + getColHtml(users[i].testRoom) + getColHtml(users[i].testNum) + getColHtml(users[i].ip) +"</tr>");
                                    }
                                }
                                $('#my-modal-loading').modal('close');
                                $("#importFirst").css("display", "none");
                                $("#importSecond").css("display", "block");
                            } else {
                                am_alert(responseDto.message, "考生导入失败");
                                $('#my-modal-loading').modal('close');
                            }

                        },
                        error: function () {
                            $('#my-modal-loading').modal('close');
                            am_alert("考生导入失败！");
                        }
                    });
                }
            });
        } else {
            $("#importFirst").css("display", "none");
            $("#importSecond").css("display", "block");
        }
    }
}

function getColHtml(text){
    return "<td>"+ text +"</td>";
}

/**
 * 从字符串中提取数字
 * @param text
 * @returns {string}
 */
function getNumFromStr(text){
    if(text == null || text == "" || text == undefined){
        return 0;
    }
    return text.replace(/[^0-9]/ig,"").trim();
}

function reportPre(){
    $("#report").css("display","none");
    $("#importSecond").css("display","block");
}

/**数字转字符串 只支持1000以内的数字
 *
 * @param number 数字
 * @return
 */
function numberToString(number){
    if(number < 10)
        return "第" + singleDigitToString(number) + "轮"

    if(number < 100){
        return "第" + tenDigitToString(number) + "轮"
    }

    if(number < 1000)
        return "第" + hundrendDigitToString(number) + "轮"
}
/**百位数字转换
 *
 * @param number 数字
 * @return
 */
function hundrendDigitToString(number){
    var result = ""
    var tenDigit = parseInt(number%100)
    var hundrendDigit = parseInt(number/100)


    result = digitToString(hundrendDigit) + "百"
    if(tenDigit == 0 && parseInt(tenDigit/10)==0){
        return result
    }
    if(tenDigit < 10) {
        result += "零" + singleDigitToString(tenDigit)
    }else {
        if(parseInt(tenDigit/10) == 1){
            result += "一" + tenDigitToString(tenDigit)
        }else{
            result += tenDigitToString(tenDigit)
        }
    }
    return result
}
/**十位数字转换
 *
 * @param number 数字
 * @return
 */
function tenDigitToString(number){
    var result = "";
    var singleDigit = number%10
    var tenDigit = parseInt(number/10)

    if(tenDigit !=1) {
        result = digitToString(tenDigit) + "十"
    }else{
        result = "十"
    }

    if(singleDigit != 0) {
        result +=singleDigitToString(singleDigit)
    }
    return result
}
/**个位数字转换
 *
 * @param number 数字
 * @return
 */
function singleDigitToString(number){
    return digitToString(number)
}
/**数字转换
 *
 * @param number 数字
 * @return
 */
function digitToString(number){
    var numbers = ["零","一","二","三","四","五","六","七","八","九"];
    return numbers[number]
}