/**
 * Created by ballma on 16/4/6.
 */
$(function(){
    if($(".admissionCard").length==0){
        $(".container").before("<div class='admissionCard'>" +
            "<div class='ad-column'> <label for='username'>姓名: </label><span id='username'></span></div>" +
            "<div class='ad-column'> <label for='sex'>性别: </label><span id='sex'></span></div>" +
            "<div class='ad-column'> <label for='admissionId'>准考证号: </label> <span id='admissionId'></span> </div>" +
            "<ul class='topbar-nav' style='margin-top:0;'><li id='admissionCard_logout'><a href='#' onclick='logout()'>退出系统</a></li></ul>" +
            "</div>");
    }
    $.ajax({
        url: "/user/admissioninfo",
        timeout: 3000,
        type:"get",
        contentType:"application/json",
        success: function(result){
            $("#username").html(result["name"]);
            $("#sex").html(result["sex"]);
            $("#admissionId").html(result["testRoom"]+'-'+result["testNum"]);
        }
    });
});
function logout(){
    if(confirm("确定要退出考试吗？")){
        location.replace('/showOffLineAnswer');
    }
}
