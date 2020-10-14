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


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	$("#phone").blur(function () {
		hideError("phone");
		var phone=$("#phone").val();
		if (!/^1[1-9]\\d{9}$/.test(phone)){
			showError("phone","请输入正确格式的手机号！");
		}else {
			$.get(
				"/loan/page/checkPhone",{phone:phone},function (data) {
					if (data.code==1001){
						showSuccess("phone");
					}else {
						showError("phone",data.message);
					}
				}
			)
		}
	});

	$("#loginPassword").blur(function () {
		hideError("loginPassword");
		var loginPassword=$("#loginPassword").val();
		if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
			showError("loginPassword","密码必须同时包含数字和英文字母");
			return;
		}
		showSuccess("loginPassword");
	});


});
