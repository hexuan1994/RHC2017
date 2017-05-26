package edu.thu.rlab.servlet;

import edu.thu.rlab.datamanager.DataManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

public class FileDownloadServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
  private String filePath;

  public FileDownloadServlet()
  {
	  super();
  }

  public void destroy()
  {
    super.destroy();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    int user_number = CookieData.getNumber(request.getCookies());
    String userID = CookieData.getID(user_number);
    String courseID = "";

    Cookie[] allCookie = request.getCookies();
    for (Cookie c : allCookie) {
      if (c.getName().equals("courseID")){
        courseID = c.getValue();
        break;
      }
    }

	  JSONObject dataJson = new JSONObject();
	  dataJson.put("dataType", "studentDownloadBin");
	  dataJson.put("startAddr",request.getParameter("startAddr"));
	  dataJson.put("endAddr",request.getParameter("endAddr"));
      dataJson.put("studentID", userID);
      dataJson.put("courseID", courseID);
	  
	  DataManager dataManager  = new DataManager();
	  String outStr = dataManager.StudentEvent(dataJson.toString());

	  JSONObject json = new JSONObject(outStr);
	  String curFileName = json.getString("binPath");
	  
	  try
	  {
	  
	  File file = new File(curFileName);

	  String filename = URLEncoder.encode(file.getName(), "utf-8");
	  response.reset();
	  response.setContentType("application/x-msdownload");
	  response.addHeader("Content-Disposition", "attachment;filename="+ new String(filename.getBytes()));
	  int fileLength = (int) file.length();
	  response.setContentLength(fileLength);
	  
	  InputStream inStream = new FileInputStream(file);
	  byte[] buf = new byte[1024];
	  
	  ServletOutputStream servletOS = response.getOutputStream();
      int readLength;
      while (((readLength = inStream.read(buf)) != -1)) {
          servletOS.write(buf, 0, readLength);
      }
      inStream.close();
      servletOS.flush();
      servletOS.close();
	  } catch(FileNotFoundException e){
		  System.out.println("File Not Found");
	  } catch(Exception e1){
		  System.out.println("something wrong");
	  }
  }

  public void init()
    throws ServletException
  {
  }
}