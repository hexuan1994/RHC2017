package edu.thu.rlab.datamanager;

import java.io.PrintStream;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataManager
{
  public String LoginEvent(String message)
  {
    LoginManager loginManager = new LoginManager();
    JSONObject json = new JSONObject(message);
    String returnMessage = "";

    if (!json.has("dataType")) {
      JSONObject json2 = new JSONObject();
      json2.put("dataType", "error");
      json2.put("resultInfo", "operationError");
      return json2.toString();
    }
    String dataType = json.getString("dataType");
    String str1;
    switch ((str1 = dataType).hashCode()) { case -815366715:
      if (str1.equals("getPermission")) break; case 103149417:
      if ((goto 182) && (str1.equals("login")))
      {
        String name = json.getString("username");
        String password = json.getString("password");
        returnMessage = loginManager.loginEvent(name, password);
        break label218;

        String userID = json.getString("userID");
        returnMessage = loginManager.getPermissionEvent(userID);
      }break;
    }

    JSONObject json2 = new JSONObject();
    json2.put("dataType", "error");
    json2.put("resultInfo", "operationError");
    returnMessage = json2.toString();

    label218: return returnMessage;
  }

  public String AdminEvent(String message)
  {
    AdminManager adminManager = new AdminManager();
    JSONObject json = new JSONObject(message);
    String returnMessage = "";

    if (!json.has("dataType")) {
      JSONObject json2 = new JSONObject();
      json2.put("dataType", "error");
      json2.put("resultInfo", "operationError");
      return json2.toString();
    }
    String dataType = json.getString("dataType");
    String str1;
    switch ((str1 = dataType).hashCode()) { case -1316007489:
      if (str1.equals("removeCourse"));
      break;
    case -894056164:
      if (str1.equals("allCourse"));
      break;
    case -874641577:
      if (str1.equals("allDevice"));
      break;
    case -461731812:
      if (str1.equals("addCourse"));
      break;
    case -244085058:
      if (str1.equals("removeTeacher"));
      break;
    case -48495871:
      if (str1.equals("allTeacher"));
      break;
    case 468657153:
      if (str1.equals("addTeacher")) break; break;
    case 1745533564:
      if (!str1.equals("courseAddTeacher")) { break label462;

        String user = json.getString("user");
        String name = json.getString("name");
        String mail = json.getString("mail");
        String telphone = json.getString("telephone");
        returnMessage = adminManager.addTeacherEvent(user, name, mail, telphone);
        break label498;

        String teacherID = json.getString("teacherID");
        returnMessage = adminManager.removeTeacherEvent(teacherID);
        break label498;

        String courseID = json.getString("courseID");
        String courseName = json.getString("courseName");
        int courseType = json.getInt("courseType");
        int courseYear = json.getInt("courseYear");
        int courseSeason = json.getInt("courseSeason");
        returnMessage = adminManager.addCourseEvent(courseID, courseName, courseType, courseYear, courseSeason);
        break label498;

        String courseID2 = json.getString("courseID");
        returnMessage = adminManager.removeCourseEvent(courseID2);
        break label498;

        returnMessage = adminManager.allTeacherEvent();
        break label498;

        returnMessage = adminManager.allCourseEvent();
        break label498;
      } else
      {
        String courseID3 = json.getString("courseID");
        String teacherID2 = json.getString("teacherID");
        returnMessage = adminManager.courseAddTeacherEvent(courseID3, teacherID2);
        break label498;

        returnMessage = adminManager.allDeviceEvent();
      }break;
    }

    label462: JSONObject json2 = new JSONObject();
    json2.put("dataType", "error");
    json2.put("resultInfo", "operationError");
    returnMessage = json2.toString();

    label498: return returnMessage;
  }

  public String TeacherEvent(String message)
  {
    TeacherManager teacherManager = new TeacherManager();
    JSONObject json = new JSONObject(message);
    String returnMessage = "";

    if (!json.has("dataType")) {
      JSONObject json2 = new JSONObject();
      json2.put("dataType", "error");
      json2.put("resultInfo", "operationError");
      return json2.toString();
    }
    String dataType = json.getString("dataType");
    String str1;
    switch ((str1 = dataType).hashCode()) { case -1371237041:
      if (str1.equals("teacherGetCourse")) break; break;
    case -683653865:
      if (str1.equals("removeStudent"));
      break;
    case -488064678:
      if (str1.equals("allStudent"));
      break;
    case 29088346:
      if (str1.equals("addStudent"));
      break;
    case 1445096978:
      if (!str1.equals("importExcel")) { break label372;

        String teacherID = json.getString("teacherID");
        returnMessage = teacherManager.teacherGetCourseEvent(teacherID);
        break label408;
      } else
      {
        String courseID = json.getString("courseID");
        String excelPath = json.getString("excelPath");
        returnMessage = teacherManager.importExcelEvent(courseID, excelPath);
        break label408;

        String courseID0 = json.getString("courseID");
        String studentID = json.getString("studentID");
        String name = json.getString("name");
        String department = json.getString("department");
        String class0 = json.getString("class");
        String mail = json.getString("mail");
        String telephone = json.getString("telephone");
        returnMessage = teacherManager.addStudentEvent(courseID0, studentID, name, department, class0, mail, telephone);
        break label408;

        String courseID1 = json.getString("courseID");
        String studentID1 = json.getString("studentID");
        returnMessage = teacherManager.removeStudentEvent(courseID1, studentID1);
        break label408;

        String courseID2 = json.getString("courseID");
        returnMessage = teacherManager.getAllStudentEvent(courseID2);
      }break;
    }

    label372: JSONObject json2 = new JSONObject();
    json2.put("dataType", "error");
    json2.put("resultInfo", "operationError");
    returnMessage = json2.toString();

    label408: return returnMessage;
  }

  public String StudentEvent(String message)
  {
    StudentManager studentManager = new StudentManager();
    JSONObject json = new JSONObject(message);
    String returnMessage = "";

    if (!json.has("dataType")) {
      JSONObject json2 = new JSONObject();
      json2.put("dataType", "error");
      json2.put("resultInfo", "operationError");
      return json2.toString();
    }
    String dataType = json.getString("dataType");
    String str1;
    switch ((str1 = dataType).hashCode()) { case -1309871338:
      if (str1.equals("studentGetCourse")) break; break;
    case -1025968882:
      if (str1.equals("studentUploadInput"));
      break;
    case -960812036:
      if (str1.equals("studentGetOutput"));
      break;
    case -890208436:
      if (str1.equals("studentGetRecord"));
      break;
    case -571855943:
      if (str1.equals("changeStudentInfo"));
      break;
    case -412232070:
      if (str1.equals("studentUploadRbf"));
      break;
    case 375434205:
      if (str1.equals("reloadRbf"));
      break;
    case 497585164:
      if (str1.equals("studentToDevice"));
      break;
    case 1141628868:
      if (str1.equals("studentSramInput"));
      break;
    case 1713769555:
      if (!str1.equals("getStudentInfo")) { break label851;

        String studentID = json.getString("studentID");
        returnMessage = studentManager.studentGetCourseEvent(studentID);
        break label887;

        String courseID = json.getString("courseID");
        String studentID0 = json.getString("studentID");
        String expID = json.getString("expID");
        returnMessage = studentManager.studentGetRecordEvent(studentID0, courseID, expID);
        break label887;

        String studentID1 = json.getString("studentID");
        String operationType = json.getString("operationType");
        returnMessage = studentManager.studentToDeviceEvent(studentID1, operationType);
        break label887;

        String studentID2 = json.getString("studentID");
        String expID0 = json.getString("expID");
        String courseID0 = json.getString("courseID");
        String rbfPath = json.getString("rbfPath");
        returnMessage = studentManager.studentUploadRbfEvent(studentID2, courseID0, expID0, rbfPath);
        break label887;

        String studentID3 = json.getString("studentID");
        String expID1 = json.getString("expID");
        JSONArray input = json.getJSONArray("inputData");
        returnMessage = studentManager.studentUploadInputEvent(studentID3, expID1, input);
        break label887;

        String studentIDSRAM = json.getString("studentID");
        int startAddr = json.getInt("startAddr");
        int endAddr = json.getInt("endAddr");
        String inputData = json.getString("inputData");
        System.out.printf("SRAM: %s, %d, %d, %s\n", new Object[] { studentIDSRAM, Integer.valueOf(startAddr), Integer.valueOf(endAddr), inputData });
        returnMessage = studentManager.studentSRAMInputEvent(studentIDSRAM, startAddr, endAddr, inputData);
        break label887;

        String studentID4 = json.getString("studentID");
        String expID2 = json.getString("expID");
        returnMessage = studentManager.studentGetOutputEvent(studentID4, expID2);
        break label887;
      } else
      {
        String studentID5 = json.getString("studentID");
        returnMessage = studentManager.getStudentInfoEvent(studentID5);
        break label887;

        String studentID6 = json.getString("studentID");
        String oldPassword = json.getString("oldPassword");
        String newPassword = json.getString("newPassword");
        String mail = json.getString("mail");
        String tel = json.getString("tel");
        System.out.println("mail=" + mail + "tel=" + tel);
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

          break label887;

          String studentID7 = json.getString("studentID");
          String courseID2 = json.getString("courseID");
          String expID3 = json.getString("expID");
          String recordID = json.getString("recordID");
          returnMessage = studentManager.reloadRbfEvent(studentID7, courseID2, expID3, recordID); } 
      }break;
    }

    label851: JSONObject json2 = new JSONObject();
    json2.put("dataType", "error");
    json2.put("resultInfo", "operationError");
    returnMessage = json2.toString();

    label887: return returnMessage;
  }
}