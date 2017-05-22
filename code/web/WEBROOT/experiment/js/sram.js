
    $("#readsram-btn").click(function () {
    	
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	var endAddr = parseInt($("#sram-endaddr").val(),16);
    	var originData = document.getElementById("sram-data").value;
    	
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
    			console.log(result);
    			var jsResult = eval("(" + result + ")" );
    			if (jsResult.result == "succeed") {
    				document.getElementById("sram-data").value=jsResult.outputData;
    				document.getElementById("sram-data_b").value=jsResult.outputData;
    			}
    		}
    });
    });
    
    $("#writesram-btn").click(function () {
    	var patt = new RegExp("[^0-9a-fA-F ]");		//包含除了0-9,a-f,A-Z,空格,以外的字符
    	
    	var startAddr = parseInt($("#sram-startaddr").val(),16);
    	var endAddr = parseInt($("#sram-endaddr").val(),16);
    	var originData = document.getElementById("sram-data").value;
    	
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
    			console.log(result);
    		},
    		error: function(result){
    			result = eval('(' + result + ')');
    			alert(result.resultInfo);
    		}
    });
    });