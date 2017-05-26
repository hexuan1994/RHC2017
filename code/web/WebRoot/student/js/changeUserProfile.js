/*
 * 更改学生用户的信息
 */
 
$(function() {
	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text",
		data : {
			dataCategory : "getStudentInfo"
		},
		success : function(result) {
			console.log(result);
			jsResult = eval('(' + result + ')');
			if (jsResult.result == "failed") {
				console.log(jsResult.resultInfo);
				if (jsResult.resultInfo == "cookieError") {
					location.href = "../login.html";
					return;
				} else {
					alert(jsResult.resultInfo);
				}
			} 

			$("#input_id").val(jsResult.studentID);
			$("#input_name").val(jsResult.studentName);
			$("#input_email").val(jsResult.studentEmail);
			$("#input_department").val(jsResult.studentDepartment);
			$("#input_class").val(jsResult.studentClass);
			$("#input_phone").val(jsResult.studentPhone);
		}
	});

	$("#change-button").click(function() {

		// 判断是否是第一次点击
		if ($("#cancle-button").length == 0) {

			console.log("change button clicked");
			$("#input_email").removeAttr("readonly");
			$("#input_phone").removeAttr("readonly");

			submitButton = $("#change-button");

			// create cancle button
			cancleButton = document.createElement("button");
			cancleButton.setAttribute("id", "cancle-button");
			cancleButton.setAttribute("type", "submit");
			cancleButton.setAttribute("class", "btn btn-primary");
			cancleButton.innerHTML = "取消";

			// add cancle button
			$("#button-form").append(cancleButton);
			submitButton.text("确认");
			console.log("execute finish");
		} else {

			$.get(
				"../Servlet/DataServlet", 
				{
					"dataCategory" : "changeStudentInfo",
					"phone" : $("#input_phone").val(),
					"email" : $("#input_email").val()
				},
				function(result){
					// delete cancle button
					jsResult = eval('(' + result + ')');
					console.log(jsResult.resultInfo);
					if (jsResult.result == "succeed") {
						console.log(jsResult);
						$("#cancle-button").remove();
						$("#input_email").attr({"readonly" : "readonly"});
						$("#input_phone").attr({"readonly" : "readonly"});
						$("#change-button").text("修改个人信息");
						location.href = "./settings.html";
					} else{
						alert(jsResult.resultInfo);
					}
				}
			);
		}
	});

	$("#button-form").on("click", "#cancle-button", function() {
		console.log("cancle button clicked");

		$("#cancle-button").remove();
		$("#input_email").attr({"readonly" : "readonly"});
		$("#input_phone").attr({"readonly" : "readonly"});
		$("#change-button").text("修改个人信息");
	});
});