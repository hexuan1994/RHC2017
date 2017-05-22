package edu.thu.rlab.datamanager;

import edu.thu.rlab.database.CourseInfo;
import edu.thu.rlab.database.DataBase;
import edu.thu.rlab.database.UserInfo;
import edu.thu.rlab.device.DeviceService;
import edu.thu.rlab.servlet.ContextListener;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminManager
{
  private DataBase db;
  private DeviceService service;

  public AdminManager()
  {
    this.db = DataBase.getInstance();
    this.service = ContextListener.getService();
  }

  public String addTeacherEvent(String user, String name, String mail, String tel)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "addTeacher");

    UserInfo userInfo = new UserInfo(user, "e10adc3949ba59abbe56e057f20f883e", 1, name, "-", "-", mail, tel);
    int res = this.db.addProfessors(0, userInfo);
    if (res == 1) {
      json.put("result", "succeed");
    }
    else {
      json.put("result", "fail");
      if (res == 0) {
        json.put("resultInfo", "addTeacherError");
      }
      else {
        json.put("resultInfo", "permissionDenied");
      }
    }
    return json.toString();
  }

  public String removeTeacherEvent(String username)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "removeTeacher");

    int res = this.db.removeProfessors(0, username);
    if (res == 1) {
      json.put("result", "succeed");
    }
    else if (res == 0) {
      json.put("result", "fail");
      json.put("resultInfo", "removeTeacherError");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "permissionDenied");
    }
    return json.toString();
  }

  public String addCourseEvent(String courseID, String courseName, int type, int year, int season)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "addCourse");

    CourseInfo courseInfo = new CourseInfo(courseID, courseName, "-", type, year, season);
    int res = this.db.addCourses(0, courseInfo);
    if (res == 1) {
      json.put("result", "succeed");
    }
    else {
      json.put("result", "fail");
      if (res == 0) {
        json.put("resultInfo", "addCourseError");
      }
      else if (res == -2) {
        json.put("resultInfo", "courseInfoError");
      }
      else {
        json.put("resultInfo", "permissionDenied");
      }
    }
    return json.toString();
  }

  public String removeCourseEvent(String courseID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "removeCourse");

    int res = this.db.removeCourses(0, courseID);

    if (res == 1) {
      json.put("result", "succeed");
    }
    else if (res == 0) {
      json.put("result", "fail");
      json.put("resultInfo", "removeCourseError");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "permissionDenied");
    }
    return json.toString();
  }

  public String allTeacherEvent()
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "allTeacher");

    List allUser = this.db.getAllUserInfo(0);
    JSONArray jsonArray = new JSONArray();
    for (UserInfo user : allUser) {
      if (user.getPermission() == 1) {
        JSONObject json2 = new JSONObject();
        json2.put("teacherID", user.getUser());
        json2.put("name", user.getName());
        json2.put("mail", user.getEmail());
        json2.put("tel", user.getTel());
        List courseList = this.db.getCourseByProfessorID(user.getUser());
        JSONArray jsonArray2 = new JSONArray();
        for (CourseInfo course : courseList) {
          JSONObject json3 = new JSONObject();
          json3.put("courseID", course.getID());
          json3.put("courseName", course.getName());
          jsonArray2.put(json3);
        }
        json2.put("courseList", jsonArray2);
        jsonArray.put(json2);
      }
    }
    json.put("teacherList", jsonArray);
    return json.toString();
  }

  public String allCourseEvent()
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "allCourse");

    List allCourse = this.db.getAllCourseInfo(0);
    JSONArray jsonArray = new JSONArray();
    for (CourseInfo course : allCourse) {
      JSONObject json2 = new JSONObject();
      json2.put("courseID", course.getID());
      json2.put("courseName", course.getName());
      json2.put("courseType", course.getType());
      json2.put("courseYear", course.getYear());
      json2.put("courseSeason", course.getSeason());
      json2.put("courseTeacher", course.getProfessor());
      jsonArray.put(json2);
    }
    json.put("courseList", jsonArray);
    return json.toString();
  }

  public String courseAddTeacherEvent(String courseID, String teacherID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "courseAddTeacher");

    List allCourse = this.db.getAllCourseInfo(0);
    for (CourseInfo course : allCourse) {
      if (courseID.equals(course.getID())) {
        course.setProfessor(teacherID);
        this.db.addCourses(0, course);
        json.put("result", "succeed");
        return json.toString();
      }
    }
    json.put("result", "fail");
    json.put("resultInfo", "noSuchCourse");
    return json.toString();
  }

  public String allDeviceEvent()
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "allDevice");

    JSONArray jsonArray = new JSONArray(this.service.listAll().toString());
    json.put("deviceList", jsonArray);
    return json.toString();
  }
}