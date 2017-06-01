/*
 * 获取用户上传历史
 */

$(function () {
	console.log("get history");
	studentID = "null";
	if (sessionStorage.chooseStudentID != null) {
		studentID = sessionStorage.chooseStudentID;
	}

	$.ajax({  
        url : '../Servlet/DataServlet', 
        type : 'get',
        dataType : 'text',
        data : {
            dataCategory:"studentGetRecord",
            courseID : sessionStorage.courseID,
            expID : sessionStorage.exprID,
            studentID : sessionStorage.chooseStudentID
        },  
        success : function(result){
            console.log(result);
            var result_json = eval('(' + result + ')'); 
            var record_list = result_json.recordList;
            for (var i = 0; i < record_list.length; i++) {
                $("#history").append(createHistoryTable(record_list[i].expTime, record_list[i].recordID, i+1));
            }
        }  
    });  
});

function createHistoryTable(expTime, recordID, i) {
    var curTr = $("<tr></tr>");
    curTr.append($('<td>' + i + '</td>'));
    curTr.append($('<td>' + expTime + '</td>'));
    curTr.append($('<td class="row"><button id="' + recordID + '">提交' + '</button></td>'));
    if (sessionStorage.userType == "teacher") {
        $("#history").on("click", "#"+recordID, {value:recordID}, function (event){
            teacherReload(recordID);
        });
    }
    else {
        $("#history").on("click", "#"+recordID, {value:recordID}, function (event){
            studentReload(recordID);
        });
    }
    return curTr;
}

function teacherReload(recordID) {
	console.log("teacher recordID " + recordID);
    $.ajax({
        url  : "../Servlet/DataServlet",
        type : "get",
        dataType : "text",
        data : {
            dataCategory : "reloadRbf",
            studentID    : sessionStorage.studentID,
            courseID     : sessionStorage.courseID,
            expID        : sessionStorage.exprID,
            recordID     : recordID
        },
        success: function (result) {
            var jsonResult = eval('(' + result + ')');
            if (jsonResult.result == "succeed") {
                alert("重新上传成功");
            } else {
                alert(jsonResult.resultInfo);
            }
            // console.log(result);
        }
    });
}

function studentReload(recordID) {
   console.log("student recordID " + recordID);
   $.ajax({
        url  : "../Servlet/DataServlet",
        type : "get",
        dataType : "text",
        data : {
            dataCategory : "reloadRbf",
            courseID     : sessionStorage.courseID,
            expID        : sessionStorage.exprID,
            recordID     : recordID
        },
        success: function (result) {
            var jsonResult = eval('(' + result + ')');
            if (jsonResult.result == "succeed") {
                alert("重新上传成功");
            } else {
                alert(jsonResult.resultInfo);
            }
            // console.log(result);
        }
    });
}