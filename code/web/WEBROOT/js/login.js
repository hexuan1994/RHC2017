/** 
 * 设置表单验证规则 
 */  

$(function() { 

    $('#submitButton').click(function(event){  

        event.preventDefault();

        // 发送请求  
        $.ajax({  
            url : './Servlet/LoginServlet', 
            type : 'get',
            dataType : 'text',
            data : {username:$('#username').val(), password: hex_md5($('#password').val()) },  
            success : function(result){  
                console.log(result);
                // 根据result返回信息判断是否登录成功
                var result_json = eval('('+result+')'); 
                console.log(result_json);
                if (result_json.result == "succeed") {
                    var temp = result_json.resultInfo;
                    if (temp == "admin") {
                        sessionStorage.userType = "admin";
                        document.location.href = './admin/console.html';
                    } else if(temp == "teacher") { 
                        sessionStorage.userType = "teacher";
                        document.location.href = './teacher/console.html'; 
                    } else if(temp == "student") {
                        sessionStorage.userType = "student";
                        document.location.href = './student/console.html';
                    } else {
                        document.location.href = './login.html';
                    }
                } else {
                     document.location.href = './login.html';
                }
            }  
        });  
   
    });  
});  