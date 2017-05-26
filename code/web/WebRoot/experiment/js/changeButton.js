/*
 * 实验页面点击按钮页面交互
 */

$(function() {

	// 记录按钮状态，初始时数组均为0
	var buttonState = [0,0,0,0,0,0,0,0,0];

	// 为所有按钮绑定点击事件
	// 每次点击后更改 buttonState 并向后台发送数据
	$('input[name="switch0"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(0, state, buttonState);
	});

	$('input[name="switch1"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(1, state, buttonState);
	});

	$('input[name="switch2"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(2, state, buttonState);
	});

	$('input[name="switch3"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(3, state, buttonState);
	});

	$('input[name="switch4"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(4, state, buttonState);
	});

	$('input[name="switch5"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(5, state, buttonState);
	});

	$('input[name="switch6"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(6, state, buttonState);
	});

	$('input[name="switch7"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(7, state, buttonState);
	});

	$('input[name="switch8"]').on('switchChange.bootstrapSwitch', function(event, state) {
		changeButton(8, state, buttonState);
	});
});


function changeButton(button_number, state, buttonState) {
	// 更改 buttonState
	if (state == true) {
		buttonState[button_number] = 1;
	} else {
		buttonState[button_number] = 0;
	}

	sendButton(buttonState);
}

function sendButton(buttonState) {
	var expID = $("body").attr("id");
	console.log(buttonState);

	sendState = [];
	switch (expID){
		case("Adder"):
			sendState = buttonState;
			break;
		case("Decoder"):
			sendState = buttonState.slice(0,4);
			break;
		case("MultiPlexer"):
			sendState = buttonState.slice(0,6);
			break;
		case("Decimal"):
			sendState = buttonState.slice(0,8);
			break;
		default:
	}
	console.log(sendState);

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

				// 获得成功返回结果后更新实验状态
				switch (expID){
					case("Adder"):
						changeLight(jsResult.outputData);
						break;
					case("Decoder"):
						changeDecoder(jsResult.outputData);
						break;
					case("MultiPlexer"):
						changeLight(jsResult.outputData);
						break;
					case("Decimal"):
						changeDecoder(jsResult.outputData.slice(0,28));
						changeLight(jsResult.outputData.slice(28,36));
						break;
					default:
				}
			} else {
				alert("您当前未连接！");
				// 下面是前端测试函数，正式版中将删去
				// switch (expID) {
				// 	case "Adder":
				// 		var testData = [1,0,1,1,0,1,1,0,0];
				// 		for (var i = 0; i < testData.length; i++) {
				// 			testData[i] = Math.floor(Math.random() * 2 );
				// 		}
				// 		console.log("Adder  " + sendState);
				// 		changeLight(testData);
				// 		break;
				// 	case "Decoder":
				// 		var testData = [1,0,1,1,0,1,1];
				// 		for (var i = 0; i < testData.length; i++) {
				// 			testData[i] = Math.floor(Math.random() * 2 );
				// 		}
				// 		changeDecoder(testData);
				// 		break;
				// 	case "MultiPlexer":
				// 		var testData = [1,0,1,1,0];
				// 		for (var i = 0; i < testData.length; i++) {
				// 			testData[i] = Math.floor(Math.random() * 2 );
				// 		}
				// 		console.log(sendState);
				// 		changeLight(testData);
				// 		break;
				// 	case "Decimal":
				// 		var testData = [1, 0, 1, 0, 1, 0, 1,
				// 						1, 0, 1, 0, 1, 0, 1,
				// 						1, 0, 1, 0, 1, 0, 1,
				// 						1, 0, 1, 0, 1, 0, 1,
				// 						1, 0, 1, 0,
				// 						0, 1, 1, 0];
				// 		for (var i = 0; i < testData.length; i++) {
				// 			testData[i] = Math.floor(Math.random() * 2 );
				// 		}
				// 		changeDecoder(testData.slice(0,28));
				// 		changeLight(testData.slice(28,36));
				// }
			}
		}
	});
}

function changeLight(result) {
	// 更改灯的状态
	for (var i = 0; i < result.length; i++) {
		if (result[i] == 1) {
			// 设置灯的颜色为红色
			$("#led-" + i).attr("class", "led led-red");
		} else {
			// --         ---白色
			$("#led-" + i).attr("class", "led led-black");
		}
	}
}

function changeDecoder(result) {
	for (var i = 0; i < result.length; i++) {
		if (result[i] == 1) {
			// 显示某节数码管
			$("#digit-"+i).removeClass("turnoff");
			$("#digit-"+i).addClass("turnon");
		} else {
			// 隐藏某节数码管
			$("#digit-"+i).removeClass("turnon");
			$("#digit-"+i).addClass("turnoff");
		}
	}
}