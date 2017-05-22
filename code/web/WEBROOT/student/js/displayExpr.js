/*
 * 显示某门课下的所有实验
 */

$(function() {
	var courseType = sessionStorage.courseType;
	console.log(courseType);
	switch(courseType){
		case "0":
			// 显示数字逻辑课程
			displayDigitalLogic();
			break;
		case "1":
			// 显示计算机组成课程
			displayComputerPrinciple();
			break;
		case "2":
			displayComputerScience();
			break;
		default:
			console.log("default");
	}
});

function displayDigitalLogic() {
	exprID = ["Adder", "State", "MultiPlexer", "Counter", "Decoder","Decimal","SRAM"];
	exprName = ["加法器", "状态机", "多路选择器", "计数器", "数码管译码", "十进制加减法计数器","内存读写"];

	console.log("displayDigitalLogic");
	for (var i = 0; i < 7; i++) {
		var exprDiv = $('<div class="col-xs-4 col-sm-2 placeholder"></div>');
		exprDiv.attr("id", exprID[i]);
		var exprImg = $('<img src="../img/icon/'+exprID[i].toLowerCase()+'.ico">');
		var exprTxt = $('<h4>'+exprName[i]+'</h4>');
		exprDiv.append(exprImg);
		exprDiv.append(exprTxt);
		$("#choose-experiment").append(exprDiv);
		$("#choose-experiment").on("click", "#"+exprID[i], {value:exprID[i]}, function (event){
			sessionStorage.exprID = $(this).attr("id");
			console.log($(this).attr("id"));
			location.href = "../experiment/" + $(this).attr("id").toLowerCase() + ".html";
		});
	}
}

function displayComputerPrinciple() {
	// wait for add course
}

function displayComputerScience() {
	// wait for add course
}