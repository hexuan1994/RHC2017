/*
 * 修改密码
 */

$(function() {
	// change password
	$("#verify-button").click(function(event) {
		verifyChange();
	});

	$("#cancle-button").click(function(event) {
		cancleChange();
	});
});

function verifyChange() {
	var oldPass = $("#input_old_password").val();
	var newPass = $("#input_new_password").val();
	var verifyPass = $("#password_verify").val();

	if (newPass != verifyPass) {
		console.log(newPass);
		console.log(verifyPass);
		alert("两次输入密码不一致");
		return;
	}

	if (newPass == null || newPass == "") {
		alert("密码输入不能为空");
		return;
	}

	$.get(
		"../Servlet/DataServlet", 
		{
			"dataCategory" : "changeStudentPass",
			"oldPass" : oldPass,
			"newPass" : newPass
		},
		function(result){
			// delete cancle button
			jsResult = eval('(' + result + ')');
			console.log(jsResult.resultInfo);
			if (jsResult.result == "succeed") {
				alert("密码修改成功");
				location.href = "./console.html";
			} else{
				alert(jsResult.resultInfo);
			}
		}
	);
}

function cancleChange() {
	location.href = "./console.html";
}