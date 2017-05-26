package edu.thu.rlab.servlet;

import edu.thu.rlab.datamanager.DataManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataServlet extends HttpServlet
{
	public DataServlet(){
		super();
	}
	
  public void destroy()
  {
    super.destroy();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html;charset=utf-8");
    PrintWriter out = response.getWriter();

    int user_number = CookieData.getNumber(request.getCookies());
    if (user_number == -1) {
      out.print("{'result':'failed', 'resultInfo':'cookieError'}");
      out.flush();
      out.close();
      return;
    }

    String dataType = request.getParameter("dataCategory");
    String user_type = CookieData.getType(user_number);
    String user_id = CookieData.getID(user_number);

    DataManager dataManager = new DataManager();
    JSONObject dataJson = new JSONObject();
    if ((dataType == null) || (dataType.equals("")))
    {
      out.print("{'result':'failed'}");
    }
    else if (user_type.equals("admin"))
      {
        switch(dataType){
		case("addTeacher"):
			// add teacher
			dataJson.put("dataType", "addTeacher");
			dataJson.put("username", request.getParameter("username"));
			dataJson.put("name", request.getParameter("name"));
			dataJson.put("mail", request.getParameter("mail"));
			dataJson.put("telephone", request.getParameter("telephone"));
			out.print(dataManager.AdminEvent(dataJson.toString()));
			break;
		case("displayTeacher"):
			// get all teacher's info
			dataJson.put("dataType", "allTeacher");
			out.print(dataManager.AdminEvent(dataJson.toString()));
			break;
		case("addCourse"): {
			// insert a new course into database
			dataJson.put("dataType", "addCourse");
			dataJson.put("courseID", request.getParameter("courseID"));
			dataJson.put("courseName", request.getParameter("courseName"));
			
			int courType = 0;
			switch(request.getParameter("courseType")){
				case ("数字逻辑"):
					courType = 0;
					break;
				case ("计算机原理"):
					courType = 1;
					break;
				case ("计算机设计"):
					courType = 2;
					break;
			}
			dataJson.put("courseType", courType);
			dataJson.put("courseYear", request.getParameter("courseYear"));
			
			int courSeason = -1;
			switch(request.getParameter("courseSeason")) {
				case("春"):
					courSeason = 0;
					break;
				case("夏"):
					courSeason = 1;
					break;
				case("秋"):
					courSeason = 2;
					break;
			}
			dataJson.put("courseSeason", courSeason);
			out.print(dataManager.AdminEvent(dataJson.toString()));
			break;
		}
		case("displayCourse"):
			// display all course
			System.out.println("displayCourse");
			dataJson.put("dataType", "allCourse");
			String return_value = dataManager.AdminEvent(dataJson.toString());
			System.out.println("display_course" + return_value);
			out.print(return_value);
			break;
		case("updateCourse"): {
			// update course's info
			dataJson.put("dataType", "addCourse");
			
			int courType = 0;
			switch(request.getParameter("courseType")){
				case ("数字逻辑"):
					courType = 0;
					break;
				case ("计算机原理"):
					courType = 1;
					break;
				case ("计算机设计"):
					courType = 2;
					break;
			}
			dataJson.put("courseType", courType);
			dataJson.put("courseYear", request.getParameter("courseYear"));
			
			int courSeason = -1;
			switch(request.getParameter("courseSeason")) {
				case("春"):
					courSeason = 0;
					break;
				case("夏"):
					courSeason = 1;
					break;
				case("秋"):
					courSeason = 2;
					break;
			}
			dataJson.put("courseName", request.getParameter("courseName"));
			dataJson.put("courseType", courType);
			dataJson.put("courseYear", request.getParameter("courseYear"));
			dataJson.put("courseSeason", courSeason);
			dataJson.put("courseID", request.getParameter("courseID"));
			dataJson.put("teacherID", request.getParameter("teacher"));
			dataManager.AdminEvent(dataJson.toString());
			
			dataJson.put("dataType", "courseAddTeacher");
			out.print(dataManager.AdminEvent(dataJson.toString()));
			System.out.println("test");
			break;
		}
		case("displayService"): 
			// display sll server's info
			dataJson.put("dataType", "allDevice");
			out.print(dataManager.AdminEvent(dataJson.toString()));
			break;
		default:
			// not match any type 
			dataJson.put("result", "failed");
			dataJson.put("resultInfo", "dataType not found");
			out.print(dataJson.toString());
        }
        } else if (user_type.equals("teacher")) {
			switch(dataType){
			case("addStudent"):
				// add a new student
				dataJson.put("dataType", "addStudent");
				dataJson.put("courseID", request.getParameter("courseID"));
				dataJson.put("studentID", request.getParameter("studentID"));
				dataJson.put("name", request.getParameter("name"));
				dataJson.put("department", request.getParameter("department"));
				dataJson.put("class", request.getParameter("class"));
				dataJson.put("mail", request.getParameter("mail"));
				dataJson.put("telephone", request.getParameter("telephone"));
				out.print(dataManager.TeacherEvent(dataJson.toString()));
				System.out.println(dataJson);
				break;
			case("displayStudent"):
				// get all student's info of a course
				String courseID = request.getParameter("courseID");
				response.addCookie(new Cookie("courseID", courseID));
				
				CookieData.setCourseID(user_number, courseID);
				dataJson.put("dataType", "allStudent");
				dataJson.put("courseID", courseID);
				out.print(dataManager.TeacherEvent(dataJson.toString()));
				break;
			case("getCourse"):
				// get all courses of current teacher
				dataJson.put("dataType", "teacherGetCourse");
				dataJson.put("teacherID", user_id);
				out.print(dataManager.TeacherEvent(dataJson.toString()));
				break;
			case("studentGetRecord"):
				// get a student's experiment record
				dataJson.put("dataType", "studentGetRecord");
				dataJson.put("studentID", request.getParameter("studentID"));
				dataJson.put("courseID", request.getParameter("courseID"));
				dataJson.put("expID", request.getParameter("expID"));
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("reloadRbf"):
				// reload a student's rbf file to re-experiment
				dataJson.put("dataType", "reloadRbf");
				dataJson.put("studentID", request.getParameter("studentID"));
				dataJson.put("courseID", request.getParameter("courseID"));
				dataJson.put("expID", request.getParameter("expID"));
				dataJson.put("recordID", request.getParameter("recordID"));
				System.out.println(dataJson.toString());
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			default:
				// not match any type
				dataJson.put("result", "failed");
				dataJson.put("resultInfo", "dataType not found");
				out.print(dataJson.toString());
			}
		} else if (user_type.equals("student")) {
			switch(dataType) {
			case("studentToDevice"):
				// connect device
				System.out.println(dataType);
				dataJson.put("dataType", dataType);
				dataJson.put("studentID", user_id);
				CookieData.setCourseID(user_number, request.getParameter("courseID"));
				CookieData.setexprID(user_number, request.getParameter("exprID"));
				response.addCookie(new Cookie("courseID", request.getParameter("courseID")));
				response.addCookie(new Cookie("exprID", request.getParameter("exprID")));
				dataJson.put("operationType", "connect");
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("studentUploadInput"):
				// upload rbf file
				dataJson.put("dataType", dataType);
				dataJson.put("studentID", user_id);
				dataJson.put("expID", request.getParameter("expID"));
				JSONArray tj = new JSONArray(request.getParameterValues("inputData[]"));
				System.out.println(tj.toString());
				dataJson.put("inputData", tj);
				String uploadResult = dataManager.StudentEvent(dataJson.toString());
				JSONObject upJson = new JSONObject(uploadResult);
				if(upJson.get("result").equals("succeed")){
					dataJson.put("dataType", "studentGetOutput");
					out.print(dataManager.StudentEvent(dataJson.toString()));
				} else {
					out.print(uploadResult);
				}
				break;
			case("getCourse"):
				// get student's course
				dataJson.put("dataType", "studentGetCourse");
				dataJson.put("studentID", user_id);
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("getStudentInfo"):
				// get student's info
				dataJson.put("dataType", "getStudentInfo");
				dataJson.put("studentID", user_id);
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("changeStudentInfo"):
				// change student's info
				dataJson.put("dataType", "changeStudentInfo");
				dataJson.put("studentID", user_id);
				dataJson.put("mail", request.getParameter("email"));
				dataJson.put("tel", request.getParameter("phone"));
				dataJson.put("oldPassword", "");
				dataJson.put("newPassword", "");
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("changeStudentPass"):
				// change student's password
				dataJson.put("dataType", "changeStudentInfo");
				dataJson.put("studentID", user_id);
				dataJson.put("mail", "");
				dataJson.put("tel", "");
				dataJson.put("oldPassword", request.getParameter("oldPass"));
				dataJson.put("newPassword", request.getParameter("newPass"));
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("studentSramInput"):
	              dataJson.put("dataType", "studentSramInput");
	              dataJson.put("studentID", user_id);
	              dataJson.put("startAddr", request.getParameter("startAddr"));
	              dataJson.put("endAddr", request.getParameter("endAddr"));
	              dataJson.put("inputData", request.getParameter("inputData"));
	              out.print(dataManager.StudentEvent(dataJson.toString()));
	              break;
			case("studentGetRecord"):
				// get student's record
				dataJson.put("dataType", "studentGetRecord");
				dataJson.put("studentID", user_id);
				dataJson.put("courseID", request.getParameter("courseID"));
				dataJson.put("expID", request.getParameter("expID"));
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			case("reloadRbf"):
				// reload submitted rbf file
				dataJson.put("dataType", "reloadRbf");
				dataJson.put("studentID", user_id);
				dataJson.put("courseID", request.getParameter("courseID"));
				dataJson.put("expID", request.getParameter("expID"));
				dataJson.put("recordID", request.getParameter("recordID"));
				out.print(dataManager.StudentEvent(dataJson.toString()));
				break;
			default:
				// not match any type
				dataJson.put("result", "failed");
				dataJson.put("resultInfo", "dataType not found");
				out.print(dataJson.toString());
			}
		}
		
		out.flush();
		out.close();
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    out.flush();
    out.close();
  }

  public void init()
    throws ServletException
  {
  }
}