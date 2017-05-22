/**
 * 与后台数据进行交互，显示课程、教师等信息
 */


// 根据页面body的id判断显示什么信息
$(function() {
	switch($("body").attr("id")) {
		case("display-teacher"):
			displayTeacher();
			break;
		case("display-course"):
			displayCourse();
			break;
		case("display-device"):
			displayDevice();
			break;
		 case("display-student"):
		 	displayStudent();
		 	break;
		default:
	}
});


// 在管理员界面中展示所有教师用户信息
function displayTeacher() {
	var table = $("#add-teacher");

	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text", 
		data : {
			dataCategory : "displayTeacher"
		},
		success : function(result) {
			console.log(result);
			var jsResult = eval("(" + result + ")");
			jsResult = jsResult.teacherList;
			for (var i = 0; i <= jsResult.length - 1; i++){
				table.append(createTr(
					jsResult[i].teacherID,
					jsResult[i].name,
					jsResult[i].mail,
					jsResult[i].tel,
					jsResult[i].courseList
				));
			}
		}
	});
}

function createTr(id, name, email, phone, course) {
	// create a row of teacher table
	var temp_tr = $("<tr></tr>");
	temp_tr.append($("<td>"+id+"</td>"));
	temp_tr.append($("<td>"+name+"</td>"));
	temp_tr.append($("<td>"+email+"</td>"));
	temp_tr.append($("<td>"+phone+"</td>"));
	if (course == null || course == "") {
		temp_tr.append($("<td>No Course</td>"));
	} else {
		temp_tr.append($("<td>"+course[0].courseName+"</td>"));
	}
	return temp_tr;
}


// 向管理员用户与教师用户展示课程信息
function displayCourse() {
	var parent = $("#course_view");

	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text", 
		data : {
			dataCategory : "displayCourse"
		},
		success : function(result) {

			var jsResult = eval("(" + result + ")");
			
			if (jsResult.result == "failed") {
				console.log(jsResult.resultInfo);
				if (jsResult.resultInfo == "cookieError") {
					location.href = "../login.html";
					return;
				} else {
					alert(jsResult.resultInfo);
				}
			}

			// 将获得的json数据展示在前台
			jsResult = jsResult.courseList;
			for (var i = 0; i <= jsResult.length - 1; i++){
				var courSeason = "-";
				if(jsResult[i].courseSeason == 0){
					courSeason = "春";
				} else if(jsResult[i].courseSeason == 1){
					courSeason = "夏";
				} else if(jsResult[i].courseSeason == 2){
					courSeason = "秋";
				}
				parent.append(createCourse(
					jsResult[i].courseID,
					jsResult[i].courseName,
					jsResult[i].courseType,
					jsResult[i].courseYear,
					courSeason,
					jsResult[i].courseTeacher
				));
			}
		}
	});
}

function createCourse(id, name, type, year, season, teacher) {
	// create a row of course table
	var temp_tr = $("<tr></tr>");
	temp_tr.append($("<td>"+id+"</td>"));
	temp_tr.append($("<td>"+name+"</td>"));
	temp_tr.append($("<td>"+type+"</td>"));
	temp_tr.append($("<td>"+year+"</td>"));
	temp_tr.append($("<td>"+season+"</td>"));
	temp_tr.append($("<td>"+teacher+"</td>"));
	
	
	var modal_dialog = $('<div class="modal-dialog" style="display: inline-block; width: auto;"></div>'); //inline-block; 
	var modal_content = $('<div class="modal-content"></div>');
	var modal_head = $('<div class="modal-header"><button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button><h4>修改课程信息</h4></div>');
	var modal_body = $('<div class="modal-body" style="width:300px;"></div>');
	var modal_footer = $('<div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">关闭</button><button type="button" class="btn btn-primary" id="button'+id+'">提交更改</button></div>');
	
	var id_form = $('<form></form>');
	var id_div = $('<div class="form-group row"></div>');
	id_div.append('<label for="input_code"'+id+' class="col-md-5 col-form-label">课程编号</label>');
	id_div.append('<div class="col-md-7"><input type="text" name="Code" id="input_code'+id+'" class="form-control" value="'+id+'"></div>')
	id_form.append(id_div);
	modal_body.append(id_form);
	
	var name_form = $('<form></form>');
	var name_div = $('<div class="form-group row"></div>');
	name_div.append('<label for="input_name"'+id+' class="col-md-5 col-form-label">课程名</label>');
	name_div.append('<div class="col-md-7"><input type="text" name="Code" id="input_name'+id+'" class="form-control" value="'+name+'"></div>')
	name_form.append(name_div);
	modal_body.append(name_form);
	
	var type_form = $('<form></form>');
	var type_div = $('<div class="form-group row"></div>');
	var type_label = $('<label for="input_type"'+id+' class="col-form-label col-md-5">课程类型</div>');
	var type_select_div = $('<div class = "col-md-7"></div>');
	var type_select = $('<select class="form-control" id="input_type'+id+'"></select>');
	type_select.append($('<option>数字逻辑</option>'));
	type_select.append($('<option>计算机原理</option>'));
	type_select.append($('<option>计算机设计</option>'));
	type_select_div.append(type_select);
	type_div.append(type_label);
	type_div.append(type_select_div);
	type_form.append(type_div);
	modal_body.append(type_form);
	

	var time_form = $('<form></form>');
	var time_div = $('<div class="form-group row"></div>');
	var time_label = $('<label for="input_year"'+id+' class="col-form-label col-md-5">课程年份</div>');
	var time_select_div = $('<div class = "col-md-7"></div>');
	var time_select = $('<select class="form-control" id="input_year'+id+'"></select>');
	time_select.append($('<option>2016</option>'));
	time_select.append($('<option>2017</option>'));
	time_select.append($('<option>2018</option>'));
	time_select.append($('<option>2019</option>'));
	time_select.append($('<option>2020</option>'));
	time_select_div.append(time_select);
	time_div.append(time_label);
	time_div.append(time_select_div);
	time_form.append(time_div);
	modal_body.append(time_form);


	var season_form = $('<form></form>');
	var season_div = $('<div class="form-group row"></div>');
	var season_label = $('<label for="input_season"'+id+' class="col-form-label col-md-5">课程季节</div>');
	var season_select_div = $('<div class = "col-md-7"></div>');
	var season_select = $('<select class="form-control" id="input_season'+id+'"></select>');
	season_select.append($('<option>春</option>'));
	season_select.append($('<option>夏</option>'));
	season_select.append($('<option>秋</option>'));
	season_select_div.append(season_select);
	season_div.append(season_label);
	season_div.append(season_select_div);
	season_form.append(season_div);
	modal_body.append(season_form);


	var teacher_form = $('<form></form>');
	var teacher_div = $('<div class="form-group row"></div>');
	var teacher_label = $('<label for="input_teacher"'+id+' class="col-form-label col-md-5">课程教师lalala</div>');
	var teacher_select_div = $('<div class = "col-md-7"></div>');
	var teacher_select = $('<select class="form-control" id="input_teacher'+id+'"></select>');

	teacherList = [];
	$.ajax({
		url  : "../Servlet/DataServlet",
		type : "get",
		dataType : "text",
		data : {
			dataCategory : "displayTeacher"
		},
		success : function(result) {
			console.log(result);
			var result_var = eval('('+result+')'); 
			teacherList = result_var.teacherList;
			
			for (var i = 0; i < teacherList.length; i++) {
				console.log(teacherList[i].name);
				teacher_select.append($('<option text="'+teacherList[i].teacherID+'">'+teacherList[i].teacherID+'</option>'));
			}
		}
	});


	teacher_select_div.append(teacher_select);
	teacher_div.append(teacher_label);
	teacher_div.append(teacher_select_div);
	teacher_form.append(teacher_div);
	
	modal_body.append(teacher_form);
	
	modal_content.append(modal_head);
	modal_content.append(modal_body);
	modal_content.append(modal_footer);
	
	modal_dialog.append(modal_content);
	var temp_td = $('<td></td>');
	var temp_button = $('<button data-toggle="modal" data-target="#mod'+ id +'" class="btn change-course">修改信息</button>');
	var temp_modal = $('<div class="modal fade text-center" id="mod'+id+'"></div>');
	temp_modal.append(modal_dialog);
	
	temp_td.append(temp_button);
	temp_td.append(temp_modal);
	temp_tr.append(temp_td);
	
	$("#course_view").on("click", "#button"+id, {value:id}, function (event){
		console.log("click change button");
		code = $("#input_code"+id).val();
		name = $("#input_name"+id).val();
		type = $("#input_type"+id).val();
		year = $("#input_year"+id).val();
		season = $("#input_season"+id).val();
		teacher = $("#input_teacher"+id).find("option:selected").text();
		// teacher = id;
		console.log("teacher = " + teacher);

		$.ajax({
			url  : "../Servlet/DataServlet",
			type : "get",
			dataType : "text",
			data : {
				dataCategory : "updateCourse",
				courseID	 : code,
				courseName : name,
				courseType : type,
				courseYear : year,
				courseSeason : season,
				teacher : teacher
			},
			success : function(result) {
				console.log(result);
				var result_var = eval('('+result+')'); 
				if (result_var.result == "succeed") {
					alert("update succeed");
					$("#mod"+id).modal("hide");
					location.reload(true);
				}
				else {
					alert(result_var.resultInfo);
				}
			}
		});
	});
	return temp_tr;
}

//教师用户界面显示每门课程的所有学生
function displayStudent() {
	var courseID = sessionStorage.courseID;
	var table = $("#all-student");
	table.html("");
	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text", 
		data : {
			dataCategory : "displayStudent",
			courseID : courseID
		},
		success : function(result) {
			// console.log(result);
			var jsResult = eval("(" + result + ")");
			if (jsResult.result == "failed") {
				if (jsResult.resultInfo == "cookieError") {
					location.href = "../login.html";
				} else {
					alert(jsResult.resultInfo);
				}
			} 
			else{
				jsResult = jsResult.studentList;
				for (var i = 0; i <= jsResult.length - 1; i++){
					table.append(createStudent(
						jsResult[i].studentID,
						jsResult[i].studentName,
						jsResult[i].department,
						jsResult[i].class,
						jsResult[i].mail,
						jsResult[i].telephone
					));
				}
			}
		}
	});
}

function createStudent(studentID, studentName, department, tclass, mail, telephone) {
	// 学号、姓名、所选课程名、实验完成时间、状态
	var new_child = $('<tr></tr>');
	new_child.append($('<td>' + studentID + '</td>'));
	new_child.append($('<td>' + studentName + '</td>'));
	new_child.append($('<td>' + department + '</td>'));
	new_child.append($('<td>' + tclass + '</td>'));
	new_child.append($('<td>' + mail + '</td>'));
	new_child.append($('<td>' + telephone + '</td>'));
	new_child.append('<td><button class="btn" id="' + studentID + '">查看' + '</button></td>');
	$("#all-student").on("click", "#"+studentID, {value : studentID}, function (event) {
		sessionStorage.chooseStudentID = studentID;
		console.log("teacher choose " + studentID);
		location.href = "./experiment.html"
	})
	return new_child;
}

// 显示所有设备信息
function displayDevice() {
	var table = $("#add-device");

	$.ajax({
		url	 : "../Servlet/DataServlet",
		type : "get",
		dataType : "text", 
		data : {
			dataCategory : "displayService"
		},
		success : function(result) {
			console.log(result);
			// var test_json = "[{id:'1',ip:'2',state:'3',student_id:'4'},{id:'5',ip:'6',state:'7',student_id:'8'}]";
			var result = eval("(" + result + ")");
			var jsResult = result.deviceList;
			console.log(jsResult);
			for (var i = 0; i <= jsResult.length - 1; i++){
				table.append(createTr(
					jsResult[i].id,
					jsResult[i].ip,
					jsResult[i].state,
					jsResult[i].student_id,
					jsResult[i].lastHeartBeatTime
				));
			}
		}
	});
}

function createDevice(id, ip, state, student_id, lastHeartBeatTime) {
	// 在表格中添加新的一行
	// 设备ID、设备ip、设备运行状态、设备使用学生学号
	console.log(lastHeartBeatTime);
	var new_child = $('<tr></tr>');
	new_child.append($('<td>' + id + '</td>'));
	new_child.append($('<td>' + ip + '</td>'));
	new_child.append($('<td>' + state + '</td>'));
	if (student_id == null) {
		new_child.append($('<td>&nbps;</td>'));
	} else {
		new_child.append($('<td>' + student_id + '</td>'));		
	}
	new_child.append($('<td>' + lastHeartBeatTime + '</td>'));
	return new_child;
}
