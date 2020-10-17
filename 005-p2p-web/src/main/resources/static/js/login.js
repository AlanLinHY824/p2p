var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

function login() {
	hideError("phone");
	hideError("loginPassword");
	$("#showId").html("");
	var phone=$("#phone").val();
	var loginPassword=$("#loginPassword").val();
	var captcha=$("#captcha").val();
	if (!/^1[1-9]\d{9}$/.test(phone)){
		showError("phone","请输入正确格式的手机号！");
		return;
	}
	if (phone==""){
		showError("phone","手机号不能为空");
		return;
	}
	if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
		showError("loginPassword","密码必须同时包含数字和英文字母");
		return;
	}
	if (loginPassword==""){
		showError("loginPassword","密码不能为空");
		return;
	}
	if (captcha==""){
		$("#showId").html("验证码不能为空");
		return;
	}
	$.ajax({
		url:rootPath +"/loan/page/login",
		data:{phone:$("#phone").val(),
			loginPassword:$("#loginPassword").val(),
			captcha:$("#captcha").val()},
		type:"post",
		success:function (data) {
			if (data.code==200){
				// window.location.href="/005-p2p-web/index";
				window.location.href=$("#redictURL").val();
			}else if (data.code==1006){
				showError("phone",data.message);
			}else if (data.code==1007){
				showError("loginPassword",data.message);
			} else {
				alert("服务器繁忙，请稍后再试")
			}
		},
		error:function () {
			showError("phone","服务器繁忙，请稍后再试");
		}
	})

}
