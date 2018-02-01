/**
 * ajax提交数据
 * @param method 提交方法
 * @param data  提交数据
 * @param url  处理地址
 * @param successHref 成功后跳转地址
 */
function addOrUpdataAjaxSubmit(method,data,url,successHref){
    $.ajax({
        type:method,
        url:url,
        data:data,
        contentType:"application/json",
        success:function() {
            if(successHref!=""){
                $('#my-alert').modal({
                    relatedTarget: this,
                    closeViaDimmer: 0,
                    onConfirm: function() {
                        location.href = successHref;
                    }
                });
            }
        }
    })
}

/**
 * ajax提交数据
 * @param method 提交方法
 * @param url  处理地址
 */
function deleteAjaxSubmit(method,url){
    $.ajax({
        url:url,
        timeout: 1000,
        async:false,
        type:method,
        success:function(){
            window.location.reload();
        }
    });
}



/**
 * 日期时间组件中文显示控制
 */
function datatimepickerZn(){
    $.fn.datetimepicker.dates['zh-CN'] = {
        days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"],
        daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六", "周日"],
        daysMin:  ["日", "一", "二", "三", "四", "五", "六", "日"],
        months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        monthsShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        today: "今天",
        suffix: [],
        meridiem: ["上午", "下午"],
        rtl: false // 从右向左书写的语言你可以使用 rtl: true 来设置
    };
}
//返回上一页
function goBack(){
    window.location.href = document.referrer
}
/// /表单验证插件
function formValidator(){
    $('.am-form').validator({
        onValid: function(validity) {
            $(validity.field).closest('.am-form-group').find('.am-alert').hide();
        },
        onInValid: function(validity) {
            var $field = $(validity.field);
            var $group = $field.closest('.am-form-group');
            var $alert = $group.find('.am-alert');
            var msg = $field.data('validationMessage') || this.getValidationMessage(validity);
            if (!$alert.length) {
                $alert = $('<div class="am-alert am-alert-danger"></div>').hide().appendTo($group);
            }
            $alert.html(msg).show();
        }
    });
}
/**
 * 全选
 * @param 点击的元素
 */
function allCheck(element){

    if($(element).is(":checked")){
        $(element).parents("table").find(".checked").each(function(){
            $(this).prop("checked","true")
        })
    }else {
        $(element).parents("table").find(".checked").each(function(){
            $(this).removeAttr("checked")
        })
    }
}

/**
 * 创建一个隐藏的input
 * @param name
 * @param id
 * @param value
 * @returns {Element}
 */
function field(name,id,value){
    var sqlField = document.createElement("input");
    sqlField.setAttribute("type", "hidden");
    sqlField.setAttribute("name", name);
    sqlField.setAttribute("id", id);
    sqlField.setAttribute("value",value);
    return sqlField;
}

function getUnixTime(dateStr) {
    var newstr = dateStr.replace(/-/g,'/');
    var date =  new Date(newstr);
    return date.getTime();
}