/*
 * 管理员用户添加学生
 */

$(function() {
	$("#verify").click(function(event) {
		event.preventDefault();

		$.ajax({
			url  : "../Servlet/DataServlet",
			type : "get",
			dataType : "text",
			data : {
				dataCategory : "addCourse",
				courseID	 : $("#input_code").val(),
				courseName : $("#input_name").val(),
				courseType : $("#input_type").val(),
				courseYear : $("#input_year").val(),
				courseSeason : $("#input_season").val()
			},
			success : function(result) {
				console.log(result);
				var result_var = eval('('+result+')'); 
				if (result_var.result == "succeed") {
					alert("submit succeed");
					location.href = "./console.html";
				}
				else {
					alert(result_var.resultInfo);
				}
			}
		});
	});
});

