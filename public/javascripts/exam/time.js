i = 0;
count = 0;
MM = 0;
SS = 60;  // 秒 90s
MS = 0;
totle = (MM+1)*600;
d = 180*(MM+1);
MM = "0" + MM;
var gameTime = 60;
var maxtime = 1200;
//count down
var showTime = function(){
    totle = totle - 1;
    if (totle == 0) {
        clearInterval(s);
        clearInterval(t);
        $(".pie2").css("-o-transform", "rotate(360deg)");
        $(".pie2").css("-moz-transform", "rotate(360deg)");
        $(".pie2").css("-webkit-transform", "rotate(360deg)");

        $(".pie1").css("-o-transform", "rotate(180deg)");
        $(".pie1").css("-moz-transform", "rotate(180deg)");
        $(".pie1").css("-webkit-transform", "rotate(180deg)");

    } else {
        if (totle > 0 && MS > 0) {
            MS = MS - 1;
            if (MS < 10) {
                MS = "0" + MS
            }
        }
        if (MS == 0 && SS > 0) {
            MS = 10;
            SS = SS - 1;
            if (SS < 10) {
                SS = "0" + SS
            }
        }

        if (SS == 0 && MM > 0) {
            SS = 60;
            MM = MM - 1;
            if (MM < 10) {
                MM = "0" + MM
            }
        }
    }
    //$(".time_cart").html(SS + "s");
};

var start = function(){
    //i = i + 0.6;
    i = i + 360/((gameTime)*10);  //旋转的角度  90s 为 0.4  60s为0.6
    //count = count + 1;
    if(count++ <= (gameTime/2*10)){  // 一半的角度  90s 为 450
        $(".pie1").css("-o-transform","rotate(" + i + "deg)");
        $(".pie1").css("-moz-transform","rotate(" + i + "deg)");
        $(".pie1").css("-webkit-transform","rotate(" + i + "deg)");
    }else{
        $(".pie2").css("backgroundColor", "#5bd2b2");
        $(".pie2").css("-o-transform","rotate(" + i + "deg)");
        $(".pie2").css("-moz-transform","rotate(" + i + "deg)");
        $(".pie2").css("-webkit-transform","rotate(" + i + "deg)");
    }
};

var daojishi = function(){
    hours= Math.floor(maxtime/60/60);
    minutes = Math.floor((maxtime-hours*60*60)/60);
    seconds = Math.floor(maxtime%60);
    msg = minutes+"分"+seconds+"秒";
    $(".time_cart").html(msg);
    if( maxtime >= 10 ){
        --maxtime;
        setCookie("maxTime_" + uid + "_" + qtype, maxtime, 120);
        //document.cookie = "maxTime_" + uid + "_" + qtype + "=" + maxtime;
    }else if(maxtime <= 10 && maxtime > 0){
        $(".time_cart").html(msg);
        $(".time_cart").css("color","red");
        --maxtime;
        setCookie("maxTime_" + uid + "_" + qtype, maxtime, 120);
        //document.cookie = "maxTime_" + uid + "_" + qtype + "=" + maxtime;
    } else{
        clearInterval(timer);
        submitAnswer();
    }
}

var countDown = function(time) {
    gameTime = time;
    maxtime = time;

    //80*80px 时间进度条
    i = 0;
    count = 0;
    MM = 0;
    SS = gameTime;
    MS = 0;
    totle = (MM + 1) * gameTime * 10;
    d = 180 * (MM + 1);
    MM = "0" + MM;

    showTime();

    s = setInterval("showTime()", 100);
    start();
    t = setInterval("start()", 100);

    timer = setInterval("daojishi()",1000);
}

/** 倒计时 */
var maxTimeTwo = getCookieByName("maxTimeTwo");
if(maxTimeTwo && maxTimeTwo>0){
    maxtime = maxTimeTwo;
}

var hours= Math.floor(maxtime/60/60);
var minutes = Math.floor((maxtime-hours*60*60)/60);
var seconds = Math.floor(maxtime%60);
var msg = minutes+"分"+seconds+"秒";
$(function(){
    hours= Math.floor(maxtime/60/60);
    minutes = Math.floor((maxtime-hours*60*60)/60);
    seconds = Math.floor(maxtime%60);
    msg = minutes+"分"+seconds+"秒";
    $(".time_cart").html(msg);
})


