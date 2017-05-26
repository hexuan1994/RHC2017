/*
 * 教师用户添加学生
 */

$(function() {
	$("#verify").click(function() {
		$.ajax({
			url : "../Servlet/DataServlet",
			dataType : "text",
			type : "get",
			data : {
				dataCategory : "addStudent",
				courseID : sessionStorage.courseID,
				studentID: $("#input_id").val(),
				name : $("#input_name").val(),
				department : $("#input_department").val(),
				class : $("#input_class").val(),
				mail : $("#input_email").val(),
				telephone : $("#input_phone").val()
			},
			success : function(result) {
				console.log("result = " + result);
				var result_var = eval('('+result+')');
				if (result_var.result == "succeed") {
					alert("submit succeed");
					location.href = "./studentList.html";
				}
				else {
					alert(result_var.resultInfo);
				}
			}
		});
	});

	$("#cancle").click(function() {
		console.log("cancle");
		document.location.href = "./studentList.html";
	});
});