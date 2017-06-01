
    $("#readsram-btn").click(function () {
    	
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	var endAddr = parseInt($("#sram-endaddr").val(),16);
    	
    	if( $("#sram-startaddr").val() == "" || $("#sram-endaddr").val() == "") {
    		alert("请输入要读取的地址");
    		return;
    	}
    	
    	//合法性检查
    	if( startAddr > 1048575 || endAddr > 1048575 || startAddr > endAddr){
    		alert("操作地址超过最大范围");
    		return;
    	}
    	var data = "";

    	$.ajax({
    		url: "../Servlet/DataServlet",
    		type: "GET",
    		dataType : "text", 
    		cache : false,
    		data:{
    			dataCategory :"studentSramInput",
    			startAddr : startAddr,
    			endAddr : endAddr,
    			inputData : data
    		},
    		success : function(result){
    			if( result == null ){
    				alert("服务器无响应");
    			} else {
	    			console.log(result);
	    			var jsResult = eval("(" + result + ")" );
	    			if (jsResult.result == "succeed") {
	    				document.getElementById("sram-data").value=jsResult.outputData;
	    			}
    			}
    		}
    });
    });
    
    $("#writesram-btn").click(function () {
    	var patt = new RegExp("[^0-9a-fA-F ]");		//包含除了0-9,a-f,A-Z,空格,以外的字符
    	
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	var endAddr = parseInt($("#sram-endaddr").val(),16);
    	var originData = document.getElementById("sram-data").value;
    	
    	if( $("#sram-startaddr").val() == "" || $("#sram-endaddr").val() == "" || originData == "") {
    		alert("请输入要读取的地址及数据");
    		return;
    	}
    	
    	//合法性检查
    	if( startAddr > 1048575 || endAddr > 1048575 || startAddr > endAddr){
    		alert("操作地址超过最大范围");
    		return;
    	}
    	if( patt.test(originData) ){
    		alert("输入数据不合法,包含非16进制字符");
  			return;
    	}
    	var data = originData.replace(/ /g,"");
    	if ( startAddr + data.length / 8 != endAddr + 1){
    		alert("输入数据不合法,位数过多");
    		return;
    	}
    	$.ajax({
    		url: "../Servlet/DataServlet",
    		type: "GET",
    		dataType : "text", 
    		cache : false,
    		data:{
    			dataCategory :"studentSramInput",
    			startAddr : startAddr,
    			endAddr : endAddr,
    			inputData : data
    		},
    		success : function(result){
    			if( result == null ){
    				alert("服务器无响应");
    			} else {
    				console.log(result);
    			}
    		},
    		error: function(result){
    			result = eval('(' + result + ')');
    			alert(result.resultInfo);
    		}
    });
    });
   
    $("#upload-btn").click(function() {
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	if( startAddr > 1048575){
    		alert("操作地址超过最大范围");
    		return;
    	}
/*    	
    	$.ajaxFileUpload({
    		url: "../Servlet/FileUploadServlet",
    		type: "POST",
    		data:{
    			startAddr : startAddr
    		},
    		secureri : false,
    		fileElementId : 'upload-form',
    		dataType : 'JSON',
    		success : function(result){
    			alert("文件正在上传");
    		},
    		error: function(result){
    			result = eval('(' + result + ')');
    			alert(result.resultInfo);
    		}
     	});
*/
    	$.ajax({
    		url: "../Servlet/FileUploadServlet",
    		type: "POST",
    		data: {
    			startAddr : startAddr,
    			inputData : new FormData($("#upload-form")[0])
    		},
    		processData:false,
    		contentType:false,
    		success : function(result){
    			alert("文件上传中");
    		},
    		error: function(result){
    			result = eval('(' + result + ')');
    			alert(result.resultInfo);
    		}
     	});
    	
     });
    
    $("#download-btn").click(function() {
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	var endAddr = parseInt($("#sram-endaddr").val(),16);
    	//合法性检查
    	if( startAddr > 1048575 || endAddr > 1048575 || startAddr > endAddr){
    		alert("操作地址超过最大范围");
    		return;
    	}
    	location.href = "../Servlet/FileDownloadServlet?startAddr=" + startAddr + "&endAddr="+ endAddr;
/*    	
    	$.ajax({
    		url: "../Servlet/FileDownloadServlet",
    		type: "POST",
    		data: {
    			startAddr : startAddr,
    			endAddr : endAddr
    		},
    		processData:false,
    		contentType:false,
    		success : function(result){
    			alert("准备下载文件");
    		},
    		error: function(result){
    			result = eval('(' + result + ')');
    			alert(result.resultInfo);
    		}
     	});
 */   	
     });