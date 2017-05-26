/*
 * 上传文件(excel名单, rbf文件, bin文件)
 */

 $("#upload-btn").click(function() {

	$.ajax({
		url: "../Servlet/FileUploadServlet",
		type: "POST",
		data: new FormData($("#upload-form")[0]),
		processData:false,
		contentType:false,
		success : function(result){
			alert("upload succeed");
		},
		error: function(result){
			result = eval('(' + result + ')');
			alert(result.resultInfo);
		}
 	});
 });
