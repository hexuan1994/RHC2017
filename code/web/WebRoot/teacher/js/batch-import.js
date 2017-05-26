/*
 * 上传excel文件以批量导入
 */

$("#upload-excel").click(function() {
	console.log("upload-excel");

	$.ajax({
		url: "../Servlet/FileUploadServlet",
		type: "POST",
		data: new FormData($("#upload-form")[0]),
		processData:false,
		contentType:false,
		success : function(result){
			result = eval('(' + result + ')');
			alert(result.resultInfo);
			location.href = "./studentList.html";
		},
		error: function(result){
			alert("wrong");
		}
 	});
});