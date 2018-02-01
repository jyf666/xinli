/**
 * 将unix时间戳转换成"YYYY-MM-DD hh:mm:ss"的字符串格式
 * @param dateTime
 * @returns {string}
 */
function formatDateTime(dateTime){
    var date = new Date(dateTime);
    var Y = date.getFullYear() + '-';
    var M = addZero(date.getMonth() + 1) + '-';
    var D = addZero(date.getDate()) + ' ';
    var h = addZero(date.getHours()) + ':';
    var m = addZero(date.getMinutes()) + ':';
    var s = addZero(date.getSeconds());
    return Y+M+D+h+m+s;
}

/**
 * 十以内的数字前补零
 * @param num
 * @returns {*}
 */
function addZero(num){
    if(num){
        return num < 10 ? '0'+ num : num;
    }
    return "00";
}