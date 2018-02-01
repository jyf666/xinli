var selRet = []; //最终的口令数组
var selFromId = ""; //上一次选择的节点ID
var btnValue = ['1','4','7','10','13','2','5','8','11','14','3','6','9','12','15']; //每个节点的密码表
/* 节点宽度、节点高度 */
var btnPointW = 50, btnPointH = 50;

/* 点击节点时触发 */
function chkBtn(prmBtnId){
    var objBtn = document.getElementById(prmBtnId); //返回点击的节点DOM对象
    if(objBtn.className == "btnPointOn") return; //如果该节点已经被使用过，则跳出
    objBtn.className = "btnPointOn"; //将节点样式变为使用状态
    selRet.push(objBtn.value); //将节点密码值存入选择数组
    drowLine(prmBtnId, true); //开始画线
    selFromId = prmBtnId; //更新为上一次选择的节点ID
}
/*
 画线
 prmToId : 结束节点ID
 prmType : true - 真正效果, false - 预览效果
 */
function drowLine(prmToId, prmType){
    if(selFromId == "") return; //如果当前是第一个节点，则跳出
    var objFrom = document.getElementById(selFromId), objTo = document.getElementById(prmToId); //返回起始节点、结束节点的DOM对象

    var objFX = parseFloat(objFrom.style.left.substring(0,objFrom.style.left.length-2));
    var objFY = parseFloat(objFrom.style.top.substring(0,objFrom.style.top.length-2));
    var objTX = parseFloat(objTo.style.left.substring(0,objTo.style.left.length-2));
    var objTY = parseFloat(objTo.style.top.substring(0,objTo.style.top.length-2));

    if(objFX == objTX) { //如果是垂直线
        drowYLine(objFX, objFY, objTY, prmType);
        return;
    } else if(objFY == objTY) { //如果是水平线
        drowXLine(objFY, objFX, objTX, prmType);
        return;
    } else { //如果是斜线
        drowFlyLine(objFX, objFY, objTX, objTY, prmType);
    }
}
/* 画垂直线 */
function drowYLine(prmX, prmFromY, prmToY, prmType){
    prmX = addX(prmX); //修正X坐标
    prmFromY = addY(prmFromY); //修正Y坐标
    prmToY = addY(prmToY); //修正Y坐标
    for(var i=getOrder(prmFromY, prmToY)[0], j=getOrder(prmFromY, prmToY)[1]; i<j; i++){
        var tmp = document.createElement("DIV");
        tmp.style.position = "absolute";
        tmp.style.width = "5px";
        tmp.style.height = "5px";
        tmp.style.fontSize = "0pt";
        tmp.style.overflow = "hidden";
        tmp.style.background = getColor(prmType);
        tmp.style.left = prmX - 1 + "px";
        tmp.style.top = i + "px";
        (prmType ? document.getElementById("lineSprite") : document.getElementById("prewLineSprite")).appendChild(tmp);
    }
}
/* 画水平线 */
function drowXLine(prmY, prmFromX, prmToX, prmType){
    prmY = addY(prmY); //修正Y坐标
    prmFromX = addX(prmFromX); //修正X坐标
    prmToX = addX(prmToX); //修正X坐标
    for(var i=getOrder(prmFromX, prmToX)[0], j=getOrder(prmFromX, prmToX)[1]; i<j; i++){
        var tmp = document.createElement("DIV");
        tmp.style.position = "absolute";
        tmp.style.width = "5px";
        tmp.style.height = "5px";
        tmp.style.fontSize = "0pt";
        tmp.style.overflow = "hidden";
        tmp.style.background = getColor(prmType);
        tmp.style.top = prmY - 1 + "px";
        tmp.style.left = i + "px";
        (prmType ? document.getElementById("lineSprite") : document.getElementById("prewLineSprite")).appendChild(tmp);
    }
}
/* 画斜线 */
function drowFlyLine(prmFromX, prmFromY, prmToX, prmToY, prmType){
    prmFromX = addX(prmFromX); //修正X坐标
    prmFromY = addY(prmFromY); //修正Y坐标
    prmToX = addX(prmToX); //修正X坐标
    prmToY = addY(prmToY); //修正Y坐标
    if(Math.abs(prmFromX-prmToX)>Math.abs(prmFromY-prmToY)){
        if(prmFromX > prmToX){ //如果开始节点的X坐标大于结束节点的X坐标，则将两者位置互换
            var tmp = prmToX;
            prmToX = prmFromX;
            prmFromX = tmp;
            tmp = prmToY;
            prmToY = prmFromY;
            prmFromY = tmp;
        }
        var hw = (prmToY - prmFromY) / (prmToX - prmFromX); //倾斜率
        var b =  prmFromY - hw*prmFromX;
        var diff = 0;

        for(var i=prmFromX, j=prmToX; i<j; i++){
            diff ++;
            var tmp = document.createElement("DIV");
            tmp.style.position = "absolute";
            tmp.style.width = "5px";
            tmp.style.height = "5px";
            tmp.style.fontSize = "0pt";
            tmp.style.overflow = "hidden";
            tmp.style.background = getColor(prmType);
            tmp.style.left = i - 1 + "px";
            //tmp.style.top = prmFromY + (diff * hw) + "px";
            tmp.style.top = hw * (i-1) + b + "px";
            (prmType ? document.getElementById("lineSprite") : document.getElementById("prewLineSprite")).appendChild(tmp);
        }
    } else {
        if(prmFromY > prmToY){ //如果开始节点的Y坐标大于结束节点的Y坐标，则将两者位置互换
            var tmp = prmToY;
            prmToY = prmFromY;
            prmFromY = tmp;
            tmp = prmToX;
            prmToX = prmFromX;
            prmFromX = tmp;
        }
        var hw = (prmToY - prmFromY) / (prmToX - prmFromX); //倾斜率
        var b =  prmFromY - hw*prmFromX;
        var diff = 0;

        for(var i=prmFromY, j=prmToY; i<j; i++){
            diff ++;
            var tmp = document.createElement("DIV");
            tmp.style.position = "absolute";
            tmp.style.width = "5px";
            tmp.style.height = "5px";
            tmp.style.fontSize = "0pt";
            tmp.style.overflow = "hidden";
            tmp.style.background = getColor(prmType);
            tmp.style.left = (i - 1-b)/hw + "px";
            tmp.style.top = i - 1 + "px";
            (prmType ? document.getElementById("lineSprite") : document.getElementById("prewLineSprite")).appendChild(tmp);
        }
    }
}
/* 根据真实还是预览模式，返回线条颜色，预览模式时颜色要浅一些 */
function getColor(prmType){
    if(prmType) return "#48C8A3";
    else return "#4BE39F";
}
/* 将传入的两个数值按从小到大的顺序排序 */
function getOrder(prmA, prmB){
    return ((prmA < prmB) ? [prmA, prmB] : [prmB, prmA]);
}
/* 计算线条坐标的水平偏移量 */
function addX(prmX){
    return prmX + (btnPointW / 2) ;
}
/* 计算线条坐标的垂直偏移量 */
function addY(prmY){
    return prmY + (btnPointH / 2) ;
}
/* 鼠标悬停于节点时的处理 */
function overBtn(prmBtnId){
    var objBtn = document.getElementById(prmBtnId);
    if(objBtn.className == "btnPointOn") return; //如果该节点已经被使用过，则跳出
    objBtn.className = "btnPointPrew"; //将节点样式变为预览模式
    drowLine(prmBtnId, false); //画线，按照预览模式
}
/* 鼠标离开节点时的处理 */
function outBtn(prmBtnId){
    var objBtn = document.getElementById(prmBtnId);
    if(objBtn.className == "btnPointOn") return; //如果该节点已经被使用过，则跳出
    objBtn.className = "btnPoint"; //将节点样式变为正常模式
    clearPrewLine(); //清除预览线条
}
/* 清除预览线条 */
function clearPrewLine(){
    document.getElementById("prewLineSprite").innerHTML = "";
}
/* 清除已连线条 */
function clearLineSprite(){
    document.getElementById("lineSprite").innerHTML = "";
}

/* 让所传图片闪烁 */
function flashing(img){
    img.style.visibility = "hidden";
    setTimeout(function(){img.style.visibility = "visible";},300);
    setTimeout(function(){img.style.visibility = "hidden";},600);
    setTimeout(function(){img.style.visibility = "visible";},900);
    //setTimeout(function(){img.style.visibility = "hidden";},1200);
    //setTimeout(function(){img.style.visibility = "visible";},1500);
}
