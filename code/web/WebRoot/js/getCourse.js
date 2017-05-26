/** 
 * 学生用户获取所选课程 或 教师用户获取所教课程
 */  
$(function() {
	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text",
		data : {
			dataCategory : "getCourse"
		},
		success : function(result) {
			console.log(result);
			var jsResult = eval("(" + result + ")");
			var courseList = jsResult.courseList;

			if (jsResult.result == "failed") {
				console.log(jsResult.resultInfo);
				// cookie过期时跳转到登录页面
				if (jsResult.resultInfo == "cookieError") {
					location.href = "../login.html";
					return;
				} else {
					alert(jsResult.resultInfo);
				}
			}

			if (courseList != null && courseList.length > 0) {
				for (var i = 0; i <= courseList.length - 1; i++){
					// 新建一个课程div块
					var courseType = courseList[i].courseType;
					var courseName = "  " + courseList[i].courseName;
					var id = courseList[i].courseID;

					courseItem = $('<div class="col-xs-4 col-sm-2 placeholder" id="'+id+'" type="'+courseType+'"></div>');
					courseImg = $('<img>');
					imgSrc = "";
					switch(courseType){
						case(0): 	// 数字逻辑
							imgSrc = "../img/icon/digital-logic.ico";
							break;
						case(1): 	// 计算机原理
							imgSrc = "../img/icon/computer-theory.ico";
							break;
						case(2): 	// 计算机组成
							imgSrc = "../img/icon/computer-science.ico";
							break;
						default:
							imgSrc = "../img/icon/state.ico";
							break;
					}
					courseImg.attr("src", imgSrc);

					courseItem.append(courseImg);
					courseItem.append($('<h4>'+courseName+'</h4>'));

					$("#choose-course").append(courseItem);

					$("#choose-course").on("click", "#"+id, {value:id}, function (event){
						sessionStorage.courseID = $(this).attr("id");
						sessionStorage.courseType = $(this).attr("type");
						console.log(sessionStorage.courseID);
						console.log(sessionStorage.courseType);
						if ($("body").attr("id") == "student-console") {
							location.href = "./experiment.html";
						} else {
							location.href = "./studentList.html";
						}
					});
				}
			}
		}
	});
	
});
