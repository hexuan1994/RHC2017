package edu.thu.rlab.datamanager;

import java.io.PrintStream;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataManagerTest
{
  public DataManager dm;

  public DataManagerTest()
  {
    this.dm = new DataManager();
  }

  public void testAll()
  {
    testAllLoginEvent();

    testAllGetPermissionEvent();

    testAllAddTeacherEvent();

    testAllDevice();

    JSONObject json = new JSONObject();
    json.put("dataType", "changeStudentInfo");
    json.put("studentID", "student");
    json.put("oldPassword", "");
    json.put("newPassword", "");
    json.put("mail", "abc.abc.com");
    json.put("tel", "1234567");

    System.out.println("abbb= " + this.dm.StudentEvent(json.toString()));
  }

  private void testAllLoginEvent()
  {
    System.out.println("Test all login event:");
    testLoginEventAdminLogin();
    testLoginEventTeacherLogin();
    testLoginEventStudentLogin();
    testLoginEventPasswordWrong();
    testLoginEventUsernameWrong();
    System.out.println("Test end.\n");
  }

  private void testAllGetPermissionEvent()
  {
    System.out.println("Test all get permission event:");
    testGetPermissionEventAdminPermission();
    testGetPermissionEventTeacherPermission();
    testGetPermissionEventStudentPermission();
    testGetPermissionEventNoSuchPerson();
    System.out.println("Test end.\n");
  }

  private void testAllAddTeacherEvent()
  {
    System.out.println("Test all add teacher event:");
    testAddTeacherEventSucceed();
    testAddTeacherEventFail();
    System.out.println("Test end.\n");
  }

  private void testAllDevice()
  {
    System.out.println("@test1\nall device event : get all devices");
    JSONObject json = new JSONObject();
    json.put("dataType", "allDevice");
    System.out.println("result= " + this.dm.AdminEvent(json.toString()));
  }

  public void testAllExp() {
    testAdderExp();
    testDecoderExp();
    testMultiplexerExp();
  }

  public void testLoginEventAdminLogin()
  {
    System.out.println("@test1\nlogin event : admin login");
    JSONObject json = new JSONObject();
    json.put("dataType", "login");
    json.put("username", "admin");
    json.put("password", "admin");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  public void testLoginEventTeacherLogin()
  {
    System.out.println("@test2\nlogin event : teacher login");
    JSONObject json = new JSONObject();
    json.put("dataType", "login");
    json.put("username", "Bugboy");
    json.put("password", "77777");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  public void testLoginEventStudentLogin()
  {
    System.out.println("@test3\nlogin event : student login");
    JSONObject json = new JSONObject();
    json.put("dataType", "login");
    json.put("username", "Bugboy2");
    json.put("password", "12345");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  public void testLoginEventPasswordWrong()
  {
    System.out.println("@test4\nlogin event : password wrong");
    JSONObject json = new JSONObject();
    json.put("dataType", "login");
    json.put("username", "Bugboy2");
    json.put("password", "123456");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  public void testLoginEventUsernameWrong()
  {
    System.out.println("@test5\nlogin event : username wrong");
    JSONObject json = new JSONObject();
    json.put("dataType", "login");
    json.put("username", "Bugboy3");
    json.put("password", "12345");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  private void testGetPermissionEventNoSuchPerson()
  {
    System.out.println("@test4\nlogin event : no such person");
    JSONObject json = new JSONObject();
    json.put("dataType", "getPermission");
    json.put("userID", "avcd");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  private void testGetPermissionEventStudentPermission()
  {
    System.out.println("@test3\nlogin event : student permission");
    JSONObject json = new JSONObject();
    json.put("dataType", "getPermission");
    json.put("userID", "Bugboy2");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  private void testGetPermissionEventTeacherPermission()
  {
    System.out.println("@test2\nlogin event : teacher permission");
    JSONObject json = new JSONObject();
    json.put("dataType", "getPermission");
    json.put("userID", "Bugboy");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  private void testGetPermissionEventAdminPermission()
  {
    System.out.println("@test1\nlogin event : admin permission");
    JSONObject json = new JSONObject();
    json.put("dataType", "getPermission");
    json.put("userID", "admin");
    System.out.println("result= " + this.dm.LoginEvent(json.toString()));
  }

  private void testAddTeacherEventSucceed()
  {
    System.out.println("@test1\nadd teacher event : succeed");
    JSONObject json = new JSONObject();
    json.put("dataType", "addTeacher");
    json.put("username", "wwx14");
    json.put("name", "水哥");
    json.put("mail", "1234@126.com");
    json.put("telephone", "10086");
    System.out.println("result= " + this.dm.AdminEvent(json.toString()));
  }

  private void testAddTeacherEventFail()
  {
    System.out.println("@test2\nadd teacher event : fail");
    JSONObject json = new JSONObject();
    json.put("dataType", "addTeacher");
    json.put("username", "3.141592653589793&3.141592653589793&3.141592653589793");
    json.put("name", "水哥");
    json.put("mail", "1234@126.com");
    json.put("telephone", "10086");
    System.out.println("result= " + this.dm.AdminEvent(json.toString()));
  }

  public void testAdderExp()
  {
    System.out.println("@exptest1\nexpriment operation : adder expriment");

    JSONObject json0 = new JSONObject();
    json0.put("dataType", "studentToDevice");
    json0.put("studentID", "Bugboy2");
    json0.put("operationType", "connect");
    System.out.println(this.dm.StudentEvent(json0.toString()));

    JSONObject json1 = new JSONObject();
    json1.put("dataType", "studentUploadRbf");
    json1.put("studentID", "Bugboy2");
    json1.put("courseID", "a201034");
    json1.put("expID", "Adder");
    json1.put("rbfPath", "E:\\SEproject\\prj9_nullnull\\a.rbf");
    System.out.println(this.dm.StudentEvent(json1.toString()));

    JSONObject json2 = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    jsonArray.put(0);
    jsonArray.put(1);
    jsonArray.put(0);
    jsonArray.put(0);
    jsonArray.put(1);
    jsonArray.put(0);
    jsonArray.put(1);
    jsonArray.put(0);
    jsonArray.put(1);
    json2.put("dataType", "studentUploadInput");
    json2.put("studentID", "Bugboy2");
    json2.put("expID", "Adder");
    json2.put("inputData", jsonArray);
    System.out.println(this.dm.StudentEvent(json2.toString()));

    JSONObject json3 = new JSONObject();
    json3.put("dataType", "studentGetOutput");
    json3.put("studentID", "Bugboy2");
    json3.put("expID", "Adder");
    System.out.println(this.dm.StudentEvent(json3.toString()));
  }

  public void testDecoderExp()
  {
    System.out.println("@exptest2\nexpriment operation : decoder expriment");

    JSONObject json0 = new JSONObject();
    json0.put("dataType", "studentToDevice");
    json0.put("studentID", "Bugboy2");
    json0.put("operationType", "connect");
    System.out.println(this.dm.StudentEvent(json0.toString()));

    JSONObject json1 = new JSONObject();
    json1.put("dataType", "studentUploadRbf");
    json1.put("studentID", "Bugboy2");
    json1.put("courseID", "a201034");
    json1.put("expID", "Decoder");
    json1.put("rbfPath", "E:\\SEproject\\prj9_nullnull\\a.rbf");
    System.out.println(this.dm.StudentEvent(json1.toString()));

    JSONObject json2 = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    jsonArray.put(1);
    jsonArray.put(1);
    jsonArray.put(0);
    jsonArray.put(0);
    json2.put("dataType", "studentUploadInput");
    json2.put("studentID", "Bugboy2");
    json2.put("expID", "Decoder");
    json2.put("inputData", jsonArray);
    System.out.println(this.dm.StudentEvent(json2.toString()));

    JSONObject json3 = new JSONObject();
    json3.put("dataType", "studentGetOutput");
    json3.put("studentID", "Bugboy2");
    json3.put("expID", "Decoder");
    System.out.println(this.dm.StudentEvent(json3.toString()));
  }

  public void testMultiplexerExp()
  {
    System.out.println("@exptest3\nexpriment operation : multiplexer expriment");

    JSONObject json0 = new JSONObject();
    json0.put("dataType", "studentToDevice");
    json0.put("studentID", "Bugboy2");
    json0.put("operationType", "connect");
    System.out.println(this.dm.StudentEvent(json0.toString()));

    JSONObject json1 = new JSONObject();
    json1.put("dataType", "studentUploadRbf");
    json1.put("studentID", "Bugboy2");
    json1.put("courseID", "a201034");
    json1.put("expID", "MultiPlexer");
    json1.put("rbfPath", "E:\\SEproject\\prj9_nullnull\\a.rbf");
    System.out.println(this.dm.StudentEvent(json1.toString()));

    JSONObject json2 = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    jsonArray.put(1);
    jsonArray.put(1);
    jsonArray.put(0);
    jsonArray.put(0);
    jsonArray.put(1);
    jsonArray.put(0);
    json2.put("dataType", "studentUploadInput");
    json2.put("studentID", "Bugboy2");
    json2.put("expID", "MultiPlexer");
    json2.put("inputData", jsonArray);
    System.out.println(this.dm.StudentEvent(json2.toString()));

    JSONObject json3 = new JSONObject();
    json3.put("dataType", "studentGetOutput");
    json3.put("studentID", "Bugboy2");
    json3.put("expID", "MultiPlexer");
    System.out.println(this.dm.StudentEvent(json3.toString()));
  }

  public static void main(String[] args)
  {
    DataManagerTest dmt = new DataManagerTest();
    try {
      while (true) {
        dmt.testAll();
        dmt.testAllExp();
        Thread.sleep(3000L);
      }
    }
    catch (Exception localException)
    {
    }
  }
}