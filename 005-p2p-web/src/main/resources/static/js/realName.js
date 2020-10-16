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


//同意实名认证协议
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
	var idCard_tag=0;
	var realName_tag=0;
	var messageCode_tag=0;
	$("#phone").blur(function () {
		phone_tag=0;
		hideError("phone");
		var phone=$("#phone").val();
		if (!/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","请输入正确格式的手机号！");
		}else {
			$.ajax({
				url:"/005-p2p-web/loan/page/checkPhone",
				data:{phone:phone},
				type:"get",
				async:false,
				success:function (data) {
					if (data.code==200){
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

	$("#realName").blur(function () {
		realName_tag=0;
		hideError("realName");
		var realName=$("#realName").val();
		if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","请输入中文汉字，暂不支持非中国公民校验");
			realName_tag=-1;
			return;
		}
		showSuccess("realName");
		realName_tag=1;
	});

	$("#idCard").blur(function () {
		idCard_tag=0;
		hideError("idCard");
		var idCard=$("#idCard").val();
		if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","身份证号格式错误");
			idCard_tag=-1;
			return;
		}
		showSuccess("idCard");
		idCard_tag=1;
	});


	$("#messageCodeBtn").click(function () {
		$("#phone").blur();
		$("#idCard").blur();
		$("#realName").blur();
		if (phone_tag!=1||realName_tag!=1||idCard_tag!=1){
			return;
		}
		settime($("#messageCodeBtn"));
		var phone=$("#phone").val();
		$.ajax({
			url:"/005-p2p-web/loan/page/messageCode",
			data:{phone:phone},
			type:"get",
			success:function (data) {
				if (data.code==200){
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

	function messageCode(){
		messageCode_tag=0;
		hideError("messageCode");
		if ($("#messageCode").val()==""){
			showError("messageCode","验证码不能为空");
			messageCode_tag=1;
		}
	}

	$("#btnRegist").click(function () {
		$("#phone").blur();
		$("#idCard").blur();
		$("#realName").blur();
		messageCode();
		if (phone_tag!=1||idCard_tag!=1||realName_tag!=1||messageCode_tag!=1){
			return;
		}
		$.ajax({
			url:"/005-p2p-web/loan/page/realName",
			data:{phone:$("#phone").val(),
				realName:$.md5($("#realName").val()),
				idCard:$.md5($("#idCard").val()),
				messageCode:$("#messageCode").val()},
			type:"post",
			success:function (data) {
				if (data.code==200){
					window.location.href="/005-p2p-web/index";
				}else {
					alert(data.message);
				}
			},
			error:function () {
				showError("phone","服务器繁忙，请稍后再试");
			}
		})

	})
});

var countdown=60;
function settime(obj) {
	if (countdown == 0) {
		obj.removeAttr("disabled");
		obj.val("获取验证码");
		countdown = 60;
		return;
	} else {
		obj.attr("disabled", true);
		obj.text("重新发送(" + countdown + ")");
		countdown--;
	}
	setTimeout(function() {
			settime(obj) }
		,1000)
}
