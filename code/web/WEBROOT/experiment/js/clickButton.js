/*
 * 点击 reset 与 clock 按钮后的页面交互
 */

 $(function() {
	// click reset or clock button
	$("#clock").click(function(event) {
		clickButton("clock");
	});
	$("#reset").click(function(event) {
		clickButton("reset");
	});
});

function clickButton(param) {
	console.log("click");
	expID = $("body").attr("id");
	sendState = [0,1];

	// 点击 clock 按钮发送 11  点击 reset 发送 01
	if (param == "clock") {
		sendState = [1,1];
	} else if (param == "reset") {
		sendState = [0,1];
	}

	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text", 
		data : {
			dataCategory : "studentUploadInput",
			expID : expID,
			inputData : sendState
		},
		success : function(result) {
			console.log(result);
			var jsResult = eval("(" + result + ")" );
			if (jsResult.result == "succeed") {
				console.log(jsResult.outputData);
				// 更新LED与数码管
				switch (expID) {
					case("State"):
						changeLight(jsResult.outputData);
						break;
					case("Counter"):
						changeDigital(jsResult.outputData);
						break;
				}
				changeDigital(jsResult.outputData);
			} else {
				alert("您当前未连接！");
			}
		}
	});
}

function changeDigital(result) {
	console.log(result);
	for (var i = 0; i < result.length; i++) {
		if (result[i] == 1) {
			$("#digit-"+i).removeClass("turnoff");
			$("#digit-"+i).addClass("turnon");
		} else {
			$("#digit-"+i).removeClass("turnon");
			$("#digit-"+i).addClass("turnoff");
		}
	}
}

function changeLight(result) {
	console.log(result);
	for (var i = 0; i < result.length; i++) {
		if (result[i] == 1) {
			$("#led-" + i).attr("class", "led led-red");
		} else {
			$("#led-" + i).attr("class", "led led-black");
		}
	}
}