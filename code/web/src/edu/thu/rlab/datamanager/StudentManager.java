package edu.thu.rlab.datamanager;

import edu.thu.rlab.database.CourseInfo;
import edu.thu.rlab.database.DataBase;
import edu.thu.rlab.database.RecordInfo;
import edu.thu.rlab.database.UserInfo;
import edu.thu.rlab.device.DeviceService;
import edu.thu.rlab.servlet.ContextListener;
import edu.thu.rlab.servlet.FileUploadServlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
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

    List<CourseInfo> allCourse = this.db.getCourseByStudentID(studentID);
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
    List<RecordInfo> recordList = this.db.getRecords(courseID, expID, studentID);
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

  public String studentUploadbinEvent(String studentID, String courseID, int startAddr,  String  binPath)
  {
	    JSONObject json = new JSONObject();
	    json.put("dataType", "studentUploadBin");
	    json.put("result", "succeed");
	    try
	    {
	      InputStream inputStream = new FileInputStream(binPath);
	      int res = this.service.sendBin(studentID, startAddr,  inputStream);
	      inputStream.close();
	      if (res == 0) {
	        String path = this.db.addRecords(courseID, "SRAM", studentID);

	        InputStream inputStream2 = new FileInputStream(binPath);
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
	        json.put("result", "success");
	      }
	      else if ( res == 4 ){
	    	  json.put("result", "fail");
	    	  json.put("resultInfo", "please check the startAddr and file size.");
	      }else{
	        json.put("result", "fail");
	        json.put("resultInfo", "sendBinFailed");
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	      json.put("result", "fail");
	      json.put("resultInfo", "IOError");
	    }

	    return json.toString();  
  }
  
  public String studentDownloadBinEvent(String studentID, String courseID, int startAddr, int endAddr){
{
		    JSONObject json = new JSONObject();
		    json.put("dataType", "studentDownloadBin");
		    json.put("result", "succeed");
		        File curFile = new File(FileUploadServlet.class.getResource("/").getFile().toString());
		        String filePath = (curFile.getParent() + File.separator + "download" + File.separator);
		        File uploadDir = new File(filePath);
		        System.out.println(uploadDir.getAbsolutePath());
		        if ((!uploadDir.exists()) || (!uploadDir.isDirectory()))
		          uploadDir.mkdir();
		    	
		        //格式化生成文件名
		        Calendar now = Calendar.getInstance();
		        String fileName = studentID +"_" +  courseID + "_" + startAddr + "_" + endAddr + "_" + now.getTimeInMillis() + ".bin";
		        String curFileName = filePath + fileName;
		    	File outputFile = new File(curFileName);
		    	FileOutputStream fos = null;
		    	BufferedOutputStream bos = null;
		        
		        //
		        byte[] readData = null;
		        byte[] data = null;
		        readData = this.service.setSRAMInput(studentID, startAddr, endAddr, data); 
		        
		        //写入到文件
		        try {  
		            fos = new FileOutputStream(outputFile);
		            bos = new BufferedOutputStream(fos);
			        bos.write(readData);
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  finally {  
		            if (bos != null) {  
		                try {  
		                    bos.close();  
		                } catch (IOException e1) {  
		                    e1.printStackTrace();  
		                }  
		            }  
		            if (fos != null) {  
		                try {  
		                    fos.close();  
		                } catch (IOException e1) {  
		                    e1.printStackTrace();  
		                }  
		            }  
		        }
		    json.put("binPath",curFileName );
		    return json.toString();  
	  }
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
      }
      readData = this.service.setSRAMInput(studentID, startAddr, endAddr, writeData);
    }
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
    List<RecordInfo> recordList = this.db.getRecords(courseID, expID, studentID);
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