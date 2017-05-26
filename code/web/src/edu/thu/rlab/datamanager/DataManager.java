package edu.thu.rlab.datamanager;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataManager
{
  public String LoginEvent(String message)
  {
		LoginManager loginManager = new LoginManager();
		JSONObject json = new JSONObject(message);	//encode inputMessage
		String returnMessage = "";		//decode returnMessage
		
		//illegal requests
		if (!json.has("dataType")) {
			JSONObject json2 = new JSONObject();
			json2.put("dataType", "error");
			json2.put("resultInfo", "operationError");
			return json2.toString();
		}
		String dataType = json.getString("dataType");
		
		switch (dataType) {
			case "login":
				//all users request for login
				String name = json.getString("username");
				String password = json.getString("password");
				returnMessage = loginManager.loginEvent(name, password);
				break;
			case "getPermission":
				//all users request for permission
				String userID = json.getString("userID");
				returnMessage = loginManager.getPermissionEvent(userID);
				break;
			default:
				//illegal operations
				JSONObject json2 = new JSONObject();
				json2.put("dataType", "error");
				json2.put("resultInfo", "operationError");
				returnMessage = json2.toString();
		}
		
		return returnMessage;
  }

  public String AdminEvent(String message)
  {
    
	AdminManager adminManager = new AdminManager();
	JSONObject json = new JSONObject(message); //encode inputMessage
	String returnMessage = "";		//decode returnMessage
	
	//illegal requests
	if (!json.has("dataType")) {
		JSONObject json2 = new JSONObject();
		json2.put("dataType", "error");
		json2.put("resultInfo", "operationError");
		return json2.toString();
	}
	String dataType = json.getString("dataType");
   
	
    switch (dataType) {
	case "addTeacher":
		//add a new teacher
		String userName = json.getString("username");
		String name = json.getString("name");
		String mail = json.getString("mail");
		String telphone = json.getString("telephone");
		returnMessage = adminManager.addTeacherEvent(userName, name, mail, telphone);
		break;
	case "removeTeacher":
		//remove a teacher
		String teacherID = json.getString("teacherID");
		returnMessage = adminManager.removeTeacherEvent(teacherID);
		break;
	case "addCourse":
		//add a new course
		String courseID = json.getString("courseID");
		String courseName = json.getString("courseName");
		int courseType = json.getInt("courseType");
		int courseYear = json.getInt("courseYear");
		int courseSeason = json.getInt("courseSeason");
		returnMessage = adminManager.addCourseEvent(courseID, courseName, courseType, courseYear, courseSeason);
		break;
	case "removeCourse":
		//remove a course
		String courseID2 = json.getString("courseID");
		returnMessage = adminManager.removeCourseEvent(courseID2);
		break;
	case "allTeacher":
		//show all teachers to admin
		returnMessage = adminManager.allTeacherEvent();
		break;
	case "allCourse":
		//show all courses to admin
		returnMessage = adminManager.allCourseEvent();
		break;
	case "courseAddTeacher":
		//add a teacher to a course
		String courseID3 = json.getString("courseID");
		String teacherID2 = json.getString("teacherID");
		returnMessage = adminManager.courseAddTeacherEvent(courseID3, teacherID2);
		break;
	case "allDevice":
		//show all devices to admin
		returnMessage = adminManager.allDeviceEvent();
		break;
	default:
		//illegal operations
		JSONObject json2 = new JSONObject();
		json2.put("dataType", "error");
		json2.put("resultInfo", "operationError");
		returnMessage = json2.toString();
    }

    return returnMessage;
  }

  public String TeacherEvent(String message)
  {
		TeacherManager teacherManager = new TeacherManager();
		JSONObject json = new JSONObject(message);	 //encode inputMessage
		String returnMessage = "";		//decode returnMessage
		
		//illegal requests
		if (!json.has("dataType")) {
			JSONObject json2 = new JSONObject();
			json2.put("dataType", "error");
			json2.put("resultInfo", "operationError");
			return json2.toString();
		}
		String dataType = json.getString("dataType");
		
    
	switch(dataType) {
	case "teacherGetCourse":
		//a teacher gets his courses
		String teacherID = json.getString("teacherID");
		returnMessage = teacherManager.teacherGetCourseEvent(teacherID);
		break;
	case "importExcel":
		//import students from excel files
		String courseID = json.getString("courseID");
		String excelPath = json.getString("excelPath");
		returnMessage = teacherManager.importExcelEvent(courseID, excelPath);
		break;
	case "addStudent":
		//add a student to a course
		String courseID0 = json.getString("courseID");
		String studentID = json.getString("studentID");
		String name = json.getString("name");
		String department = json.getString("department");
		String class0 = json.getString("class");
		String mail = json.getString("mail");
		String telephone = json.getString("telephone");
		returnMessage = teacherManager.addStudentEvent(courseID0, studentID, name, department, class0, mail, telephone);
		break;
	case "removeStudent":
		//remove a student in a course
		String courseID1 = json.getString("courseID");
		String studentID1 = json.getString("studentID");
		returnMessage = teacherManager.removeStudentEvent(courseID1, studentID1);
		break;
	case "allStudent":
		//show all student in a course
		String courseID2 = json.getString("courseID");
		returnMessage = teacherManager.getAllStudentEvent(courseID2);
		break;
	default:
		//illegal operations
		JSONObject json2 = new JSONObject();
		json2.put("dataType", "error");
		json2.put("resultInfo", "operationError");
		returnMessage = json2.toString();
}

return returnMessage;
  }

  public String StudentEvent(String message)
  {
		StudentManager studentManager = new StudentManager();
		JSONObject json = new JSONObject(message); //encode inputMessage
		String returnMessage = "";		//decode returnMessage
		
		//illegal requests
		if (!json.has("dataType")) {
			JSONObject json2 = new JSONObject();
			json2.put("dataType", "error");
			json2.put("resultInfo", "operationError");
			return json2.toString();
		}
		String dataType = json.getString("dataType");
		
    switch (dataType) {
	case "studentGetCourse":
		//a student gets his courses
		String studentID = json.getString("studentID");
		returnMessage = studentManager.studentGetCourseEvent(studentID);
		break;
	case "studentGetRecord":
		//a student gets his records
		String courseID = json.getString("courseID");				
		String studentID0 = json.getString("studentID");
		String expID = json.getString("expID");
		returnMessage = studentManager.studentGetRecordEvent(studentID0, courseID, expID);
		break;
	case "studentToDevice":
		//a student connects to a device
		String studentID1 = json.getString("studentID");
		String operationType = json.getString("operationType");
		returnMessage = studentManager.studentToDeviceEvent(studentID1, operationType);
		break;
	case "studentUploadRbf":
		//a student uploads an RBF file
		String studentID2 = json.getString("studentID");
		String expID0 = json.getString("expID");
		String courseID0 = json.getString("courseID");
		String rbfPath = json.getString("rbfPath");
		returnMessage = studentManager.studentUploadRbfEvent(studentID2, courseID0, expID0, rbfPath);
		break;
	case "studentUploadInput":
		//a student uploads input data
		String studentID3 = json.getString("studentID");
		String expID1 = json.getString("expID");
		JSONArray input = json.getJSONArray("inputData");
		returnMessage = studentManager.studentUploadInputEvent(studentID3, expID1, input);
		break;
	case "studentUploadBin" :
		String studentID8 = json.getString("studentID");
		String courseID1 = json.getString("courseID");
		int startAddr = json.getInt("startAddr");
		String binPath = json.getString("binPath");
		System.out.println("StudentUploadBin: startAddr = " + startAddr + " binPath = "+ binPath);
		returnMessage = studentManager.studentUploadbinEvent(studentID8, courseID1, startAddr, binPath);
		break;
	case "studentDownloadBin" :
		String studentID9 = json.getString("studentID");
		String courseID3 = json.getString("courseID");
		int startAddr2 = json.getInt("startAddr");
		int endAddr2 = json.getInt("endAddr");
		returnMessage = studentManager.studentDownloadBinEvent(studentID9, courseID3, startAddr2, endAddr2);
		break;
	case "studentGetOutput":
		//a student gets output data
		String studentID4 = json.getString("studentID");
		String expID2 = json.getString("expID");
		returnMessage = studentManager.studentGetOutputEvent(studentID4, expID2);
		break;
	case "getStudentInfo":
		//a student gets his information
		String studentID5 = json.getString("studentID");
		returnMessage = studentManager.getStudentInfoEvent(studentID5);
		break;
	case "changeStudentInfo":
		//a student changes his information
		String studentID6 = json.getString("studentID");
		String oldPassword = json.getString("oldPassword");
		String newPassword = json.getString("newPassword");
		String mail = json.getString("mail");
		String tel = json.getString("tel");
		JSONObject jsonIn = new JSONObject();
		jsonIn.put("dataType", "login");
		jsonIn.put("username", studentID6);
		jsonIn.put("password", oldPassword);
		JSONObject jsonOut = new JSONObject(LoginEvent(jsonIn.toString()));
		if (jsonOut.getString("resultInfo").equals("student")) {
			returnMessage = studentManager.changeStudentInfoEvent(studentID6, newPassword, mail, tel);
		}
		else {
			returnMessage = studentManager.changeStudentInfoEvent(studentID6, "", mail, tel);					
		}
		break;
	case "reloadRbf":
		//a student reload an old RBF file from records
		String studentID7 = json.getString("studentID");
		String courseID2 = json.getString("courseID");
		String expID3 = json.getString("expID");
		String recordID = json.getString("recordID");
		returnMessage = studentManager.reloadRbfEvent(studentID7, courseID2, expID3, recordID);
		break;
	case "studentSramInput":
        String studentIDSRAM = json.getString("studentID");
        int startAddr1 = json.getInt("startAddr");
        int endAddr = json.getInt("endAddr");
        String inputData = json.getString("inputData");
        System.out.printf("SRAM: %s, %d, %d, length = %d\n", new Object[] { studentIDSRAM, Integer.valueOf(startAddr1), Integer.valueOf(endAddr), inputData.length() });
        returnMessage = studentManager.studentSRAMInputEvent(studentIDSRAM, startAddr1, endAddr, inputData);
        break;
	default:
		//illegal operations
		JSONObject json2 = new JSONObject();
		json2.put("dataType", "error");
		json2.put("resultInfo", "operationError");
		returnMessage = json2.toString();
}

return returnMessage;
  }
}