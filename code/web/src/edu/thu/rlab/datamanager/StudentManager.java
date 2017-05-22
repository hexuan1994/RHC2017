package edu.thu.rlab.datamanager;

import edu.thu.rlab.database.CourseInfo;
import edu.thu.rlab.database.DataBase;
import edu.thu.rlab.database.RecordInfo;
import edu.thu.rlab.database.UserInfo;
import edu.thu.rlab.device.DeviceService;
import edu.thu.rlab.servlet.ContextListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentManager
{
  private DataBase db;
  private DeviceService service;

  public StudentManager()
  {
    this.db = DataBase.getInstance();
    this.service = ContextListener.getService();
  }

  public String studentGetCourseEvent(String studentID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentGetCourse");

    List allCourse = this.db.getCourseByStudentID(studentID);
    JSONArray jsonArray = new JSONArray();
    for (CourseInfo course : allCourse) {
      JSONObject json2 = new JSONObject();
      json2.put("courseID", course.getID());
      json2.put("courseName", course.getName());
      json2.put("courseTeacher", course.getProfessor());
      json2.put("courseType", course.getType());
      json2.put("courseYear", course.getYear());
      json2.put("courseSeason", course.getSeason());
      jsonArray.put(json2);
    }
    json.put("courseList", jsonArray);
    return json.toString();
  }

  public String studentGetRecordEvent(String studentID, String courseID, String expID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentGetRecord");

    JSONArray jsonArray = new JSONArray();
    List recordList = this.db.getRecords(courseID, expID, studentID);
    for (RecordInfo record : recordList) {
      JSONObject json2 = new JSONObject();
      json2.put("recordID", record.getRecordID());
      json2.put("expTime", record.getTime());

      jsonArray.put(json2);
    }
    json.put("recordList", jsonArray);
    return json.toString();
  }

  public String studentToDeviceEvent(String studentID, String operationType)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentToDevice");

    System.out.println("DEBUG:In StudentManager, before service.connect.");

    if (operationType.equals("connect")) {
      int res = this.service.connect(studentID);
      if (res == 0) {
        json.put("result", "succeed");
      }
      else {
        json.put("result", "fail");
        json.put("resultInfo", "noDevice");
      }
    }
    else if (operationType.equals("disconnect")) {
      this.service.disconnect(studentID);
      json.put("result", "succeed");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "operationError");
    }
    return json.toString();
  }

  public String studentUploadRbfEvent(String studentID, String courseID, String expID, String rbfPath)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentUploadRbf");
    json.put("result", "succeed");
    try
    {
      InputStream inputStream = new FileInputStream(rbfPath);

      int res = this.service.sendRbf(studentID, inputStream, expID);
      inputStream.close();
      if (res == 0) {
        String path = this.db.addRecords(courseID, expID, studentID);

        InputStream inputStream2 = new FileInputStream(rbfPath);
        System.out.print(path);
        OutputStream outputStream = new FileOutputStream(path);
        byte[] buf = new byte[1024];
        int bytes = inputStream2.read(buf);
        while (bytes != -1) {
          outputStream.write(buf, 0, bytes);
          bytes = inputStream2.read(buf);
        }
        inputStream2.close();
        outputStream.close();
        json.put("result", "succeed");
      }
      else {
        json.put("result", "fail");
        json.put("resultInfo", "sendRbfFailed");
      }
    } catch (IOException e) {
      e.printStackTrace();
      json.put("result", "fail");
      json.put("resultInfo", "IOError");
    }

    return json.toString();
  }

  public String studentUploadInputEvent(String studentID, String expID, JSONArray input)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentUploadInput");

    int size = input.length();
    byte[] inputData = new byte[size];
    for (int i = 0; i < size; i++) {
      inputData[i] = ((byte)input.getInt(i));
    }
    boolean res = this.service.setInput(studentID, inputData, expID);
    if (res) {
      json.put("result", "succeed");
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "sendInputFail");
    }
    return json.toString();
  }

  public String studentSRAMInputEvent(String studentID, int startAddr, int endAddr, String data)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studenSRAMInput");

    byte[] readData = null;
    StringBuilder res = new StringBuilder("");

    if (data.length() == 0) {
      readData = this.service.setSRAMInput(studentID, startAddr, endAddr, null);
      if (readData == null) {
        System.out.println("readData == null");
        return null;
      }
      for (int i = 0; i < readData.length / 4; i++) {
        System.out.printf("length = %d, i*4+3 = %d", new Object[] { Integer.valueOf(readData.length), Integer.valueOf(i * 4 + 3) });
        res.append(Integer.toHexString(readData[(i * 4 + 3)] >> 4 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 3)] >> 0 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 2)] >> 4 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 2)] >> 0 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 1)] >> 4 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 1)] >> 0 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 0)] >> 4 & 0xF));
        res.append(Integer.toHexString(readData[(i * 4 + 0)] >> 0 & 0xF));
        res.append(' ');
      }
    }
    else {
      data = data.toUpperCase();
      String temp = "0123456789ABCDEF";
      int len = data.length() / 2;
      byte[] writeData = new byte[len];
      char[] hexChars = data.toCharArray();
      System.out.println(data);
      for (int i = 0; i < len; i++) {
        int pos = i / 4 * 4 + (3 - i % 4);
        byte bt = (byte)(temp.indexOf(hexChars[(i * 2)]) << 4 | temp.indexOf(hexChars[(i * 2 + 1)]));
        writeData[pos] = bt;
        System.out.printf("[%d]=%s", new Object[] { Integer.valueOf(pos), Integer.toHexString(bt) });
      }
      readData = this.service.setSRAMInput(studentID, startAddr, endAddr, writeData);
    }

    System.out.printf("Read Finish, res= %s \n", new Object[] { res });
    if (res.length() != 0) {
      json.put("result", "succeed");
      json.put("outputData", res);
    }
    else {
      json.put("result", "fail");
      json.put("resultInfo", "sendSRAMInputFail");
    }
    return json.toString();
  }

  public String studentGetOutputEvent(String studentID, String expID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "studentGetOutput");

    JSONArray jsonArray = new JSONArray();
    byte[] output = this.service.getResult(studentID, expID);
    if (output != null) {
      json.put("result", "succeed");
      int size = output.length;
      for (int i = 0; i < size; i++)
        jsonArray.put(output[i]);
    }
    else
    {
      json.put("result", "fail");
      json.put("resultInfo", "getOutputFail");
    }
    json.put("outputData", jsonArray);
    return json.toString();
  }

  public String getStudentInfoEvent(String studentID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "getStudentInfo");

    UserInfo student = this.db.getUserInfo(studentID);
    json.put("studentID", student.getUser());
    json.put("studentName", student.getName());
    json.put("studentDepartment", student.getDepartment());
    json.put("studentClass", student.getHisClass());
    json.put("studentEmail", student.getEmail());
    json.put("studentPhone", student.getTel());
    json.put("result", "succeed");

    return json.toString();
  }

  public String changeStudentInfoEvent(String studentUser, String newPassword, String mail, String tel)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "changeStudentInfo");

    UserInfo old = this.db.getUserInfo(studentUser);
    UserInfo student = new UserInfo();
    student.setUser(studentUser);
    student.setName(old.getName());
    student.setDepartment(old.getDepartment());
    student.setHisClass(old.getHisClass());

    if (!newPassword.equals("")) {
      student.setPassword(newPassword);
    }
    else {
      student.setPassword(old.getPassword());
    }
    if (!mail.equals("")) {
      student.setEmail(mail);
    }
    else {
      student.setEmail(old.getEmail());
    }
    if (!tel.equals("")) {
      student.setTel(tel);
    }
    else {
      student.setTel(old.getTel());
    }

    System.out.println("tel= " + student.getTel() + "mails=" + student.getEmail());
    int res = this.db.addStudents(1, student, "-");
    if (res == 1) {
      json.put("result", "succeed");
    }
    else {
      json.put("result", "fail");
    }
    return json.toString();
  }

  public String reloadRbfEvent(String studentID, String courseID, String expID, String recordID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "reloadRbf");

    String filepath = "fail";
    List recordList = this.db.getRecords(courseID, expID, studentID);
    for (RecordInfo record : recordList) {
      if (recordID.equals(record.getRecordID())) {
        filepath = record.getPath();
        break;
      }
    }
    if (filepath.equals("fail")) {
      json.put("result", "fail");
      json.put("resultInfo", "record not found");
      return json.toString();
    }

    int res = this.service.connect(studentID);
    if (res != 0) {
      json.put("result", "fail");
      json.put("resultInfo", "connection error");
      return json.toString();
    }
    try
    {
      Object inputStream = new FileInputStream(filepath);

      res = this.service.sendRbf(studentID, (InputStream)inputStream, expID);
      ((InputStream)inputStream).close();
      if (res == 0) {
        json.put("result", "succeed");
      }
      else {
        json.put("result", "fail");
        json.put("resultInfo", "reload rbf error");
      }
    } catch (IOException e) {
      e.printStackTrace();
      json.put("result", "fail");
      json.put("resultInfo", "IO Error");
    }

    return json.toString();
  }
}