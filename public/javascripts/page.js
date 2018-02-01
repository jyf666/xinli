/* 分页控制
* @param currentPage 当前页数
* @param totalPage 总页数
* href 当前页面地址
* */
function pageControl(currentPage,totalPage,href){
    href = location.href.substring(0,location.href.lastIndexOf("/")+1);
    if(currentPage==1){
        $("#pre").attr("class","am-disabled");
    }else{
        $("#pre").find("a").attr("href",href+1);
    }
    if(currentPage==totalPage){
        $("#next").attr("class","am-disabled");
    }else{
        $("#next").find("a").attr("href",href+totalPage)
    }
    if(totalPage<=5){
        for(var i = 1;i <= totalPage;i++) {
            $("#page").find("li").eq(i).find("a").attr("href",href+i).html(i);
        }
        for(var i = totalPage+1; i<=5;i++){
            $("#page").find("li").eq(totalPage+1).remove();
        }
    }else{
        if(currentPage<=3) {
            for(var i = 1;i <= 5;i++) {
                $("#page").find("li").eq(i).find("a").attr("href",href+i).html(i);
            }
        }else if(currentPage>3 && currentPage<=totalPage-3){
            $("#page").find("li").eq(1).find("a").attr("href",href+(currentPage*1-2)).html((currentPage*1-2));
            $("#page").find("li").eq(2).find("a").attr("href",href+(currentPage*1-1)).html((currentPage*1-1));
            $("#page").find("li").eq(3).find("a").attr("href",href+(currentPage*1)).html((currentPage*1));
            $("#page").find("li").eq(4).find("a").attr("href",href+(currentPage*1+1)).html((currentPage*1+1));
            $("#page").find("li").eq(5).find("a").attr("href",href+(currentPage*1+2)).html((currentPage*1+2));
        }else{
           $("#page").find("li").eq(1).find("a").attr("href",href+(totalPage*1-4)).html((totalPage*1-4));
           $("#page").find("li").eq(2).find("a").attr("href",href+(totalPage*1-3)).html((totalPage*1-3));
           $("#page").find("li").eq(3).find("a").attr("href",href+(totalPage*1-2)).html((totalPage*1-2));
           $("#page").find("li").eq(4).find("a").attr("href",href+(totalPage*1-1)).html((totalPage*1-1));
           $("#page").find("li").eq(5).find("a").attr("href",href+(totalPage*1)).html((totalPage*1));
        }
    }
    $("#page").find("li").find("a").each(function(){
        if($(this).html()==currentPage){
            $(this).parent().attr("class","am-active");
            $(this).removeAttr("href");
        }
    })
}