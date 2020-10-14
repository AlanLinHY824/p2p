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

	var phone_tag=0;
	var loginPassword_tag=0;
	$("#phone").blur(function () {
		phone_tag=0;
		hideError("phone");
		var phone=$("#phone").val();
		if (!/^1[1-9]\\d{9}$/.test(phone)){
			showError("phone","请输入正确格式的手机号！");
		}else {
			$.ajax({
				url:"/loan/page/checkPhone",
				data:{phone:phone},
				type:"get",
				async:true,
				success:function (data) {
					if (data.code==1001){
						showSuccess("phone");
						phone_tag=1;
					}else {
						showError("phone",data.message);
						phone_tag=-1;
					}
				},
				error:function () {
					showError("phone","服务器繁忙，请稍后再试");
				}
			});
		}
	});

	$("#loginPassword").blur(function () {
		loginPassword_tag=0;
		hideError("loginPassword");
		var loginPassword=$("#loginPassword").val();
		if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
			showError("loginPassword","密码必须同时包含数字和英文字母");
			loginPassword_tag=-1;
			return;
		}
		showSuccess("loginPassword");
		loginPassword_tag=1;
	});

	$("#messageCodeBtn").click(function () {
		$("#phone").blur();
		if (phone_tag==1){
			settime($("#messageCodeBtn"));
		}
		$.ajax({
			url:"/loan/page/messageCode",
			data:{phone:phone},
			type:"get",
			success:function (data) {
				if (data.code==1001){
					showSuccess("phone");
					phone_tag=1;
				}else {
					showError("phone",data.message);
				}
			},
			error:function () {
				showError("phone","服务器繁忙，请稍后再试");
			}
		})
	});


	$("#btnRegist").click(function () {
		$("#phone").blur();
		$("#loginPassword").blur();
		if (phone_tag==1&&loginPassword_tag==1){
			$.ajax({
				url:"",
				data:{phone:$("#phone").val(),
					loginPassword:$("#loginPassword").val(),
					messageCode:$("#messageCode").val()},
				type:"post",
				success:function () {
					if (data.code==1001){
						window.location.href=[[${#request.getContextPath()}]]+"/index";
					}else {
						alert("注册失败，请稍后再试")
					}
				}
			})
		}
	})

});

var countdown=60;
function settime(obj) {
	if (countdown == 0) {
		obj.removeAttr("disabled");
		obj.val("免费获取验证码");
		countdown = 60;
		return;
	} else {
		obj.attr("disabled", true);
		obj.val("重新发送(" + countdown + ")");
		countdown--;
	}
	setTimeout(function() {
			settime(obj) }
		,1000)
}
