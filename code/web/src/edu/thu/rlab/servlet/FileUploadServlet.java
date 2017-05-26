package edu.thu.rlab.servlet;

import edu.thu.rlab.datamanager.DataManager;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

public class FileUploadServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private String filePath;
  private int startAddr = 0;
  
  public FileUploadServlet()
  {
    File curFile = new File(FileUploadServlet.class.getResource("/").getFile().toString());
    this.filePath = (curFile.getParent() + "/upload/");
    File uploadDir = new File(this.filePath);
    System.out.println(uploadDir.getAbsolutePath());
    if ((!uploadDir.exists()) || (!uploadDir.isDirectory()))
      uploadDir.mkdir();
  }

  public void destroy()
  {
    super.destroy();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType("text/html;charset=utf-8");
    PrintWriter out = response.getWriter();

    int user_number = CookieData.getNumber(request.getCookies());

    String courseID = "";
    String exprID = "";
    

    Cookie[] allCookie = request.getCookies();
    for (Cookie c : allCookie) {
      if (c.getName().equals("courseID"))
        courseID = c.getValue();
      else if (c.getName().equals("exprID")) {
        exprID = c.getValue();
      }
    }

    if (user_number == -1) {
      out.println("{'result':'failed'}");
      out.flush();
      out.close();
      return;
    }

    String userType = CookieData.getType(user_number);
    String userID = CookieData.getID(user_number);
    try
    {
      DiskFileItemFactory diskFactory = new DiskFileItemFactory();
      diskFactory.setSizeThreshold(104857600);
      diskFactory.setRepository(new File(this.filePath));
      ServletFileUpload upload = new ServletFileUpload(diskFactory);
      upload.setSizeMax(20971520L);
      List<FileItem> list = upload.parseRequest(request);
      for (FileItem item : list) {
    	  if( item.isFormField()){
    		  String name = item.getFieldName();
    		  if (name.equals("startAddr")){
    			  String value = item.getString();
    			  this.startAddr = Integer.parseInt(value);
    			  System.out.println(startAddr);
    		  }
    	  } else {
        String filename = item.getName();
        if (filename == null) {
          out.print("{'result':'failed'}");
          out.flush();
          out.close();
          return;
        }
        System.out.println("FileName: " + filename);

        String curFilePath = this.filePath + item.getName();
        File uploadFile = new File(curFilePath);
        item.write(uploadFile);
    	 
      
        String outStr = "";

        if ((userType.equals("student")) && (filename.endsWith(".rbf"))) {
          JSONObject fileJson = new JSONObject();
          fileJson.put("dataType", "studentUploadRbf");
          fileJson.put("studentID", userID);
          fileJson.put("courseID", courseID);
          fileJson.put("expID", exprID);
          fileJson.put("rbfPath", curFilePath);

          DataManager dataManager = new DataManager();
          outStr = dataManager.StudentEvent(fileJson.toString());
        } else if ((userType.equals("teacher")) && ((filename.endsWith(".xls")) || (filename.endsWith("xlsx")))) {
          JSONObject fileJson = new JSONObject();
          fileJson.put("dataType", "importExcel");
          fileJson.put("courseID", courseID);
          fileJson.put("excelPath", curFilePath);

          DataManager dataManager = new DataManager();
          outStr = dataManager.TeacherEvent(fileJson.toString());
        } else if((userType.equals("student")) && (filename.endsWith(".bin"))) {
        	JSONObject fileJson = new JSONObject();
        	fileJson.put("dataType", "studentUploadBin");
        	fileJson.put("studentID", userID);
        	fileJson.put("courseID", courseID);
        	fileJson.put("startAddr", this.startAddr);
        	fileJson.put("binPath", curFilePath);
        	
        	DataManager dataManager  = new DataManager();
        	outStr = dataManager.StudentEvent(fileJson.toString());
        } else {
          outStr = "{'result':'failed', 'resultInfo':'文件类型错误'}";
        }
        out.print(outStr);
        out.flush();
        out.close();
    	  }
      }
    } catch (Exception e) {
      System.out.println("使用 fileupload 包时发生异常 ...");
      e.printStackTrace();
    }
  }

  public void init()
    throws ServletException
  {
  }
}