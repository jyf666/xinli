/**
 * Created by XIAODA on 2016/3/22.
 */
/**
 * 反馈
 */
function sign(src, obj, className){
    var img = document.createElement("img");
    img.src = src;
    img.style = "z-index:9;position:absolute;left:"+ ($(obj).position().left + 78) +"px;top:" + ($(obj).position().top - 8)+"px;";
    img.width = 20;
    $(img).addClass("temp_pic " + className);
    $(obj).parent().append(img);
}

/**
 * 生成解析
 */
function generateAnalytic(prompt, questionNum){
    var pageNum = getPageNum();
    var pageSize = $("#content-index > div.panel-body").length;
    prompt = prompt.replace(/XXX/, questionNum);
    analyticJson[pageNum-1][questionNum-1] = prompt;
}

/**
 * 显示解析
 */
function showAnalytic(){
    var pageNum = getPageNum();
    var prompts = analyticJson[pageNum-1];
    var analytic = "";
    for(var i=0;i< prompts.length;i++){
        analytic += prompts[i];
    }
    $("#analytic").html(analytic);
}

/**
 * 下一页
 */
function show_next_practice() {
    var pageNum = getPageNum();
    var obj = $("#content-index > div.panel-body");
    if (pageNum < obj.length) {
        $(obj).eq(pageNum - 1).css("display", "none");
        $(obj).eq(pageNum).css("display", "block");
        var str = pageNum + 1 + "/" + obj.length;
        $("#page_content").html(str);
    }
    if (pageNum == obj.length - 1) {
        $(".arrow-right").css("display","none");
        $("#page_content").html(btnHtml);
    }
}
