package edu.thu.rlab.datamanager;

import edu.thu.rlab.database.CourseInfo;
import edu.thu.rlab.database.DataBase;
import edu.thu.rlab.database.UserInfo;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class TeacherManager
{
  private DataBase db;

  public TeacherManager()
  {
    this.db = DataBase.getInstance();
  }

  public String teacherGetCourseEvent(String teacherID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "teacherGetCourse");

    List<CourseInfo> allCourse = this.db.getAllCourseInfo(0);
    JSONArray jsonArray = new JSONArray();
    for (CourseInfo course : allCourse) {
      if (teacherID.equals(course.getProfessor())) {
        JSONObject json2 = new JSONObject();
        json2.put("courseID", course.getID());
        json2.put("courseName", course.getName());
        json2.put("courseType", course.getType());
        json2.put("courseYear", course.getYear());
        json2.put("courseSeason", course.getSeason());
        jsonArray.put(json2);
      }
    }
    json.put("courseList", jsonArray);
    return json.toString();
  }

  public String importExcelEvent(String courseID, String excelPath)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "importExcel");
    ExcelReader excelReader = new ExcelReader(excelPath);
    System.out.print("read xls ok");
    List< List<String> > content = excelReader.readExcelSheet(0);
    int succeedNum = 0;
    int failNum = 0;
    if (!content.isEmpty()) {
      for (List<String> line : content)
      {
        UserInfo student = new UserInfo();
        student.setUser((String)line.get(0));
        student.setPassword((String)line.get(0));
        student.setPermission(2);
        student.setName((String)line.get(1));
        student.setDepartment((String)line.get(2));
        student.setHisClass((String)line.get(3));
        student.setEmail((String)line.get(5));
        student.setTel((String)line.get(6));
        int res = this.db.addStudents(1, student, courseID);
        if (res == 1) {
          succeedNum++;
        }
        else
        {
          failNum++;
        }
      }
      json.put("result", "succeed");
      json.put("resultInfo", "add " + succeedNum + " students successfully, add " + failNum + " students failed.");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "importExcelFail");
    }
    return json.toString();
  }

  public String addStudentEvent(String courseID, String studentID, String name, String department, String class0, String mail, String tel)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "addStudent");

    UserInfo student = new UserInfo(studentID, "e10adc3949ba59abbe56e057f20f883e", 2, name, department, class0, mail, tel);
    int res = this.db.addStudents(1, student, courseID);
    if (res == 1) {
      json.put("result", "succeed");
    }
    else if (res == 0) {
      json.put("result", "fail");
      json.put("resultInfo", "addStudentError");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "permissionDenied");
    }
    return json.toString();
  }

  public String removeStudentEvent(String courseID, String studentID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "removeStudent");

    int res = this.db.removeStudents(1, studentID, courseID);
    if (res == 1) {
      json.put("result", "succeed");
    }
    else if (res == 0) {
      json.put("result", "fail");
      json.put("resultInfo", "removeStudentError");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "permissionDenied");
    }
    return json.toString();
  }

  public String getAllStudentEvent(String courseID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "allStudent");

    List<String> students = this.db.getStudentsInCourse(courseID);
    JSONArray jsonArray = new JSONArray();
    for (String studentUser : students) {
      UserInfo student = this.db.getUserInfo(studentUser);
      JSONObject json2 = new JSONObject();
      json2.put("studentID", student.getUser());
      json2.put("studentName", student.getName());
      json2.put("department", student.getDepartment());
      json2.put("class", student.getHisClass());
      json2.put("mail", student.getEmail());
      json2.put("telephone", student.getTel());
      jsonArray.put(json2);
    }
    json.put("studentList", jsonArray);
    return json.toString();
  }
}