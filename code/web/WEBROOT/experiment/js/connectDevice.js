$("#connect-device").click(function () {
	
    if (sessionStorage.userType == "teacher") {
        alert("抱歉，暂不允许教师用户连接设备与上传文件");
        return;
    }

	$.ajax({  
        url : '../Servlet/DataServlet', 
        type : 'get',
        dataType : 'text',
        data : {
            dataCategory:"studentToDevice",
            courseID : sessionStorage.courseID,
            exprID : sessionStorage.exprID
        },  
        success : function(result){
            console.log(result);
            // 根据result返回信息判断是否登录成功
            var result_json = eval('(' + result + ')'); 
            console.log(result_json);
            if (result_json.result == "succeed") {
                var temp = result_json.resultInfo;
                alert("connect succeed");
            } else {
                alert(result_json.resultInfo);
            }
        }  
    });  
})