var pageNum_showed;
/**
 * 设置cookie
 * @param c_name
 * @param value
 * @param expireminutes 过期时间（分）
 */
function setCookie(c_name, value, expireminutes) {
	if (expireminutes && expireminutes != null) {
		var exdate = new Date();
		exdate.setTime(exdate.getTime() + expireminutes * 60 * 1000);
	}
	document.cookie = c_name + "=" + value + ((expireminutes == null) ? "" : ";expires=" + exdate.toGMTString());
}

/**
* 根据name获取cookie的值
*/
function getCookieByName(name) {
     //获取cookie字符串
	var strCookie = document.cookie;
     //将多cookie切割为多个名/值对 
	var arrCookie = strCookie.split("; ");
	var value;
     //遍历cookie数组，处理每个cookie对 
	for (var i = 0; i < arrCookie.length; i++) {
		var arr = arrCookie[i].split("=");
         //找到名称为name的cookie，并返回它的值 
		if (name == arr[0]) {
			value = arr[1];
			break;
		}
	}
	return value;
}

var isAnserwEnd=false;
/**
 * 上一页
 */
function show_prev() {
	$(".arrow-right").css("display", "block");
	if (!isAnserwEnd) {
		$("#submit_button").css("display", "none");
	}
	var obj = $("#content-index >div");
	var pageNum = getPageNum();// 当前页码
	if (pageNum - 1 > 0) {
		saveAnswerDetailByLocalStorage("", "2");// 记录向前切题的时间
		setCookie("testingPage", pageNum - 1, 120);
		$(obj).eq(pageNum - 1).css("display", "none");
		$(obj).eq(pageNum - 2).css("display", "block");
		var str = pageNum - 1 + "/" + obj.length;
		$("#page_content").html(str);
	}
}

function show_prev_practice() {
	$(".arrow-right").css("display", "block");
	var obj = $("#content-index >div");
	var pageNum = getPageNum();// 当前页码
	if (pageNum - 1 > 0) {
		$(obj).eq(pageNum - 1).css("display", "none");
		$(obj).eq(pageNum - 2).css("display", "block");
		var str = pageNum - 1 + "/" + obj.length;
		$("#page_content").html(str);
	}
}

/**
 * 下一页
 */
function show_next() {
	var obj = $("#content-index > div.panel-body");
	var pageNum = getPageNum();// 当前页码
	if (pageNum < obj.length) {
		saveAnswerDetailByLocalStorage("", "3");// 记录向后切题的时间
		setCookie("testingPage", pageNum + 1, 120);
		$(obj).eq(pageNum - 1).css("display", "none");
		$(obj).eq(pageNum).css("display", "block");
		var str = pageNum + 1 + "/" + obj.length;
		$("#page_content").html(str);
		if(pageNum_showed && pageNum+1>pageNum_showed){
			setCookie("pageNum_showed", ++pageNum_showed, 120);
		}
	}
	if (pageNum == obj.length - 1) {
		$(".arrow-right").css("display","none");
	}
}

function show_next_practice(url) {
	var obj = $("#content-index > div.panel-body");
	var flag = 0;
	for (var i = 0; i < obj.length; i++) {
		if ($(obj).eq(i).css("display") == "block") {
			flag = i;
		}
	}
	if (flag < obj.length - 1) {
		$(obj).eq(flag).css("display", "none");
		$(obj).eq(flag + 1).css("display", "block");
		var str = flag + 2 + "/" + obj.length;
		$("#page_content").html(str);
	} else if (flag == obj.length - 1) {
		location.replace(url);
	}

}

function get_layer(url) {
	url = url;
	$.layer({
		type:2, 
		shadeClose:true, 
		title:false, 
		closeBtn:[0, false], 
		shade:[0.8, "#000"],
		border:[0],
		offset:["0px", ""],
		area:[($(window).width() - 100) + "px", ($(window).height()) + "px"],
		iframe: {src:url}
	});
}

/**
* 点击继续，关闭帮助窗口，继续答题
*/
function closeHelpWindow(){
	var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
    parent.layer.close(index); //再执行关闭
}

/**
 * 获取当前系统时间
 * @returns {string}
 * @constructor
 */
function curentTime() {
	var now = new Date();

	var year = now.getFullYear();       //年
	var month = now.getMonth() + 1;     //月
	var day = now.getDate();            //日

	var hh = now.getHours();            //时
	var mm = now.getMinutes();          //分
	var ss = now.getSeconds();          //秒
	var S = now.getMilliseconds()		//毫秒

	var clock = year + "-";

	if(month < 10) clock += "0";
	clock += month + "-";

	if(day < 10) clock += "0";
	clock += day + " ";

	if(hh < 10) clock += "0";
	clock += hh + ":";
	if (mm < 10) clock += '0';
	clock += mm + ":";

	if (ss < 10) clock += '0';
	clock += ss;
	clock += "." + S;
	return(clock);
}

/**
 * 获取当前页码
 * @returns {number}
 */
function getPageNum(){
	var obj = $("#content-index >div");
	for (var i = 0; i < obj.length; i++) {
		if ($(obj).eq(i).css("display") == "block") {
			return i + 1;
		}
	}
}

$(document).bind("contextmenu", function () {
	return false;
});
$(document).bind("selectstart", function () {
	return false;
});
$(document).keydown(function (event) {
         //屏蔽 Alt+ 方向键 ←
         //屏蔽 Alt+ 方向键 →
	if ((event.altKey) && ((event.keyCode == 37) || (event.keyCode == 39))) {
		event.returnValue = false;
		return false;
	}
         //屏蔽退格删除键
	if (event.keyCode == 8) {
		if(event.target.type=='text'){
			return true;
		}
		return false;
	}
         //屏蔽F5刷新键
	if (event.keyCode == 116) {
		return false;
	}
        //屏蔽alt+R 
	if ((event.ctrlKey) && (event.keyCode == 82)) {
		return false;
	}
});

window.history.forward(1);

/**
var explorer = window.navigator.userAgent ;
window.onbeforeunload = function() {  
	var isIE="ActiveXObject" in window;  
	if(isIE){//IE浏览器  
		var n = window.event.screenX - window.screenLeft;  
		var b = n > document.documentElement.scrollWidth-20;  
		if(b && window.event.clientY<0 || window.event.altKey){ 
			return "关闭浏览器可能会造成您本页面的答案丢失！"; // 可以阻止关闭  
		} 
	} else{//火狐浏览器  
		return "关闭浏览器可能会造成您本页面的答案丢失！"; // 可以阻止关闭  
	} 
}*/