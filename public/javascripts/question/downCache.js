/**
 * Created by XIAODA on 2015/8/11.
 */
var srcArray = new Array();
// # 测试系统整体介绍页面
srcArray.push("/instructions/index/1?tid=" + tid);
srcArray.push("/instructions/instructions?tid=" + tid);
http://localhost:9000/instructions/index/1?tid=8
var qtypeList = JSON.parse(qtypeListJson);
for(var i = qtypeList.length - 1 ;i >= 0; i--){
    switch (qtypeList[i]*1){
        case 1:
            // 材料记忆
            srcArray.push("/materialMemory/instructions?tid=" + tid);
            srcArray.push("/materialMemory/test?tid=" + tid);
            srcArray.push("/materialMemory/endTime?tid=" + tid);
            break;
        case 2:
            // 符号运算
            srcArray.push("/symbolicOperation/instructions?tid=" + tid);
            srcArray.push("/symbolicOperation/practice?tid=" + tid);
            srcArray.push("/symbolicOperation/practiceEnd?tid=" + tid);
            srcArray.push("/symbolicOperation/test?tid=" + tid);
            srcArray.push("/symbolicOperation/endTime?tid=" + tid);
            break;
        case 3:
            // 顺序记忆
            srcArray.push("/spatialMemory/instructions?tid=" + tid);
            srcArray.push("/spatialMemory/practice?tid=" + tid);
            srcArray.push("/spatialMemory/practiceEnd?tid=" + tid);
            srcArray.push("/spatialMemory/test?tid=" + tid);
            srcArray.push("/spatialMemory/endTime?tid=" + tid);
            break;
        case 4:
            // 图形搜索
            srcArray.push("/grapfSearch/instructions?tid=" + tid);
            srcArray.push("/grapfSearch/practice?tid=" + tid);
            srcArray.push("/grapfSearch/practiceEnd?tid=" + tid);
            srcArray.push("/grapfSearch/test?tid=" + tid);
            srcArray.push("/grapfSearch/endTime?tid=" + tid);
            break;
        case 5:
            // 形状连线
            srcArray.push("/shapelinking/instructions?tid=" + tid);
            srcArray.push("/shapelinking/practice?tid=" + tid);
            srcArray.push("/shapelinking/practiceEnd?tid=" + tid);
            srcArray.push("/shapelinking/test?tid=" + tid);
            srcArray.push("/shapelinking/endTime?tid=" + tid);
            break;
        case 6:
            // 段落推理
            srcArray.push("/paragraphReasoning/instructions?tid=" + tid);
            srcArray.push("/paragraphReasoning/practice?tid=" + tid);
            srcArray.push("/paragraphReasoning/practiceEnd?tid=" + tid);
            srcArray.push("/paragraphReasoning/test?tid=" + tid);
            srcArray.push("/paragraphReasoning/mark?tid=" + tid);
            srcArray.push("/paragraphReasoning/endTime?tid=" + tid);
            break;
        case 7:
            // 材料提取
            srcArray.push("/memoryExtract/instructions?tid=" + tid);
            srcArray.push("/memoryExtract/test?tid=" + tid);
            srcArray.push("/memoryExtract/mark?tid=" + tid);
            srcArray.push("/memoryExtract/endTime?tid=" + tid);
            break;
        case 8:
            // 类比推理
            srcArray.push("/analogicReasoning/instructions?tid=" + tid);
            srcArray.push("/analogicReasoning/practice?tid=" + tid);
            srcArray.push("/analogicReasoning/practiceEnd?tid=" + tid);
            srcArray.push("/analogicReasoning/test?tid=" + tid);
            srcArray.push("/analogicReasoning/mark?tid=" + tid);
            srcArray.push("/analogicReasoning/endTime?tid=" + tid);
            break;
        case 9:
            // 矩阵推理
            srcArray.push("/matrixReasoning/instructions?tid=" + tid);
            srcArray.push("/matrixReasoning/practice?tid=" + tid);
            srcArray.push("/matrixReasoning/practiceEnd?tid=" + tid);
            srcArray.push("/matrixReasoning/test?tid=" + tid);
            srcArray.push("/matrixReasoning/mark?tid=" + tid);
            srcArray.push("/matrixReasoning/endTime?tid=" + tid);
            break;
        case 10:
            // 人格测试
            srcArray.push("/personality/instructions?tid=" + tid);
            //srcArray.push("/personality/practice?tid=" + tid);
            srcArray.push("/personality/practiceEnd?tid=" + tid);
            srcArray.push("/personality/test?tid=" + tid);
            srcArray.push("/personality/mark?tid=" + tid);
            srcArray.push("/personality/endTime?tid=" + tid);
            break;
        case 11:
            // 家庭问卷
            srcArray.push("/familyQuestionnaire/instructions?tid=" + tid);
            srcArray.push("/familyQuestionnaire/practiceEnd?tid=" + tid);
            srcArray.push("/familyQuestionnaire/test?tid=" + tid);
            srcArray.push("/familyQuestionnaire/mark?tid=" + tid);
            srcArray.push("/familyQuestionnaire/endTime?tid=" + tid);
            break;
        case 12:
            // 空间旋转
            srcArray.push("/spaceRotation/instructions?tid=" + tid);
            //srcArray.push("/spaceRotation/practice?tid=" + tid);
            srcArray.push("/spaceRotation/practiceEnd?tid=" + tid);
            srcArray.push("/spaceRotation/test?tid=" + tid);
            srcArray.push("/spaceRotation/mark?tid=" + tid);
            srcArray.push("/spaceRotation/endTime?tid=" + tid);
            break;
        case 13:
            // 情绪识别
            srcArray.push("/emotionRecognition/instructions?tid=" + tid);
            srcArray.push("/emotionRecognition/practice?tid=" + tid);
            srcArray.push("/emotionRecognition/practiceEnd?tid=" + tid);
            srcArray.push("/emotionRecognition/test?tid=" + tid);
            srcArray.push("/emotionRecognition/endTime?tid=" + tid);
            break;
        case 14:
            // 情绪理解
            srcArray.push("/emotionUnderstanding/instructions?tid=" + tid);
            srcArray.push("/emotionUnderstanding/practice?tid=" + tid);
            srcArray.push("/emotionUnderstanding/practiceEnd?tid=" + tid);
            srcArray.push("/emotionUnderstanding/test?tid=" + tid);
            srcArray.push("/emotionUnderstanding/endTime?tid=" + tid);
            break;
        case 15:
            // 远距离联想
            srcArray.push("/remoteAssociation/instructions?tid=" + tid);
            srcArray.push("/remoteAssociation/practice?tid=" + tid);
            srcArray.push("/remoteAssociation/practiceEnd?tid=" + tid);
            srcArray.push("/remoteAssociation/test?tid=" + tid);
            srcArray.push("/remoteAssociation/endTime?tid=" + tid);
            break;
    }
}

var loadCount = 0;
var loadNum = 8;
var interval;
function download(){
    $('#my-modal-loading').modal();
    document.getElementById('probar').style.display="block";
    interval = setInterval("checkOnline()",1000);
    console.log(srcArray[loadCount]);
    var iframe = document.getElementById("iframeLoad");
    iframe.src = srcArray[loadCount++];

    if (iframe.attachEvent){
        $(this).attachEvent("onload", function(){
            processerbar();
        });
    } else {
        iframe.onload = function(){
            processerbar(iframe);
        };
    }
}

function checkOnline(){
    if(!navigator.onLine){
        alert("网络连接断开，请联系监考老师");
        $('#my-modal-loading').modal('close');
        interval = window.clearInterval(interval);
        return;
    }
}

function processerbar(iframe){

    var size = srcArray.length;
    if (loadCount < size) {console.log(srcArray[loadCount]);
        iframe.src = srcArray[loadCount++];
        var num = (loadCount/size*100).toFixed(0);
        if(num < 10){
            process(500, num);
        } else {
            process(1000, num);
        }
        loadNum = num;
    } else if (loadCount == size){
        if(loadNum < 100){
            process(1000, 100);
        }
        finish();
    }
};

function process(time, num){
    $("#line").each(function(i,item){
        $(item).animate({
            width: num +"%"
        },time);
    });
    var si = window.setInterval(function(){
        a=$("#line").width();
        b=(a/350*100).toFixed(0);
        document.getElementById('counter').innerHTML=b+"%";
        document.getElementById('counter').style.left=a-25+"px";
        if(document.getElementById('counter').innerHTML==num+"%") {
            clearInterval(si);
        }
        if(document.getElementById('counter').innerHTML=="100%") {
            document.getElementById('msg').innerHTML="&nbsp;&nbsp;成功";
        }
    },70);
}

function finish(){
    setTimeout("$('#my-modal-loading').modal('close')", 30000);
    interval = window.clearInterval(interval);
    var btn = '<input id="confirm_btn" type="button" class="btn btn-default" style="margin-top:20px;height:40px;border:none;font-size:18px;font-weight:bolder;width:200px;background:#48C9A3;color:white;"'+
        'onclick="openwin(\'/exam/adminssionCard\');" value="确&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;认" />';
    $("#btn_div").html(btn);
}