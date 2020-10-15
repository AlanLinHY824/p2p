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
	$.ajax({
		url:"/005-p2p-web/loan/page/login",
		data:{phone:$("#phone").val(),
			loginPassword:$("#loginPassword").val(),
			captcha:$("#captcha").val()},
		type:"post",
		success:function (data) {
			if (data.code==200){
				window.location.href="/005-p2p-web/index";
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
