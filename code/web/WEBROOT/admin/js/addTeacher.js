/**
 * 管理员添加教师
 */

 $(function() {
	$("#verify").click(function(event) {
		event.preventDefault();

		$.ajax({
			url  : "../Servlet/DataServlet",
			type : "get",
			dataType : "text",
			data : {
				dataCategory : "addTeacher",
				user : $("#input_username").val(),
				name : $("#input_name").val(),
				mail: $("#input_email").val(),
				telephone: $("#input_phone").val()
			},
			success : function(result) {
				var result_var = eval('('+result+')'); 
				if (result_var.result == "succeed") {
					alert("submit succeed");
					location.href = "./teacher.html";
				}
				else {
					alert("something wrong");
				}
			}
		});
	});
});
