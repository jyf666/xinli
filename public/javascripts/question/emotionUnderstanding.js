/**
 * Created by XIAODA on 2015/11/5.
 */
/**
 * 选中选项 （所有情绪分数总和为10点）
 * @param obj
 */
function selected(obj) {
    if($(obj).attr("class") == "optionBtn"){
        var temp = 0;// 所选项所在行内已选的按钮点数
        if($(obj).parent().find(":button.optionBtnSelected").length > 0){
            temp = parseInt($(obj).parent().find(":button.optionBtnSelected").val());
        }
        var total = 0;
        $(obj).parent().parent().find(":button.optionBtnSelected").each(function(j){
            total += parseInt($(this).val());
        })
        total = total + parseInt($(obj).val()) - temp;
        if (total <= 10) {
            $(obj).parent().children(":button").each(function(j){
                $(this).attr("class", "optionBtn");
            })
            $(obj).attr("class", "optionBtnSelected");

            $("#say").html("您已经选择了" + total + "点，还有" + (10 - total) + "点未选择");
        }else {
            alert("所有情绪分数总和不能超过10点");
        }
    } else if($(obj).attr("class") == "optionBtnSelected"){
        $(obj).attr("class", "optionBtn");

        var total = 0;
        $(obj).parent().parent().find(":button.optionBtnSelected").each(function(j){
            total += parseInt($(this).val());
        })
        if (total <= 10) {
            $("#say").html("您已经选择了" + total + "点，还有" + (10 - total) + "点未选择");
        }
    }
}

/**
 * 设置切换题目的向右箭头是否可用
 */
function showOrHideArrowRight() {
    var pageNum = getPageNum();
    var total = 0;
    $("#content-index >div").eq(pageNum - 1).find(':button.optionBtnSelected').each(function(j){
        total += parseInt($(this).val());
    })
    showArrowRight();
    if(total == 10){
        showArrowRight();
        if(pageNum == $("#content-index >div").length){
            $("#submit_button").css("display","block");
        }
    } else if(total < 10){
        //hideArrowRight();
        //alert("您所选择的分数不够10分！");
        if(pageNum == $("#content-index >div").length){
            $("#submit_button").css("display","none");
        }
    }
}

/**
 * 设置切换题目的向右箭头为绿色可用
 */
function showArrowRight(){
    $(".arrow-right").attr("onclick","show_next()");
    $(".arrow-right").css("border-left","50px solid #48C9A3");
}

/**
 * 设置切换题目的向右箭头为灰色不可用
 */
function hideArrowRight(){
    $(".arrow-right").attr("onclick","javascript:void(0)");
    $(".arrow-right").css("border-left","50px solid grey");
}

function show_next() {
    var pageNum = getPageNum();
    var total = 0;
    $("#content-index >div").eq(pageNum - 1).find(':button.optionBtnSelected').each(function(j){
        total += parseInt($(this).val());
    })
    if(total < 10){
        alert("您所选择的分数不够10分！");
        return false;
    }
    var obj = $("#content-index >div");
    var pageNum = getPageNum();// 当前页码
    if(pageNum == obj.length - 1){
        $(".arrow-right").css("display","none");
    }
    if (pageNum < obj.length) {
        hideArrowRight();// 让下一页的切题箭头不可用
        saveAnswerDetailByLocalStorage("", "3");// 记录向后切题的时间
        setCookie("testingPage", pageNum + 1, 120);
        $(obj).eq(pageNum - 1).css("display", "none");
        $(obj).eq(pageNum).css("display", "block");
        var str = pageNum + 1 + "/" + obj.length;
        $("#page_content").html(str);
    }
}