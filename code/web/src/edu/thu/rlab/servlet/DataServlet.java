package edu.thu.rlab.servlet;

import edu.thu.rlab.datamanager.DataManager;
import java.io.IOException;
import java.io.PrintStream;
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
    else
    {
      label530: label662: String return_value;
      if (user_type.equals("admin"))
      {
        String str1;
        switch ((str1 = dataType).hashCode()) { case -1402828220:
          if (str1.equals("updateCourse"));
          break;
        case -686798957:
          if (str1.equals("displayService"));
          break;
        case -461731812:
          if (str1.equals("addCourse"));
          break;
        case 184437952:
          if (str1.equals("displayTeacher"));
          break;
        case 468657153:
          if (str1.equals("addTeacher")) break; break;
        case 2022951805:
          int courSeason;
          if (!str1.equals("displayCourse")) { break label1147;

            dataJson.put("dataType", "addTeacher");
            dataJson.put("user", request.getParameter("user"));
            dataJson.put("name", request.getParameter("name"));
            dataJson.put("mail", request.getParameter("mail"));
            dataJson.put("telephone", request.getParameter("telephone"));
            out.print(dataManager.AdminEvent(dataJson.toString()));
            break label2816;

            dataJson.put("dataType", "allTeacher");
            out.print(dataManager.AdminEvent(dataJson.toString()));
            break label2816;

            dataJson.put("dataType", "addCourse");
            dataJson.put("courseID", request.getParameter("courseID"));
            dataJson.put("courseName", request.getParameter("courseName"));

            int courType = 0;
            String str2;
            switch ((str2 = request.getParameter("courseType")).hashCode()) { case -379834069:
              if (str2.equals("计算机原理")) break; break;
            case -379382649:
              if (str2.equals("计算机设计"));
            case 797265117:
              if ((goto 530) && (str2.equals("数字逻辑")))
              {
                courType = 0;
                break label530;

                courType = 1;
                break label530;

                courType = 2;
              }
              break; }
            dataJson.put("courseType", courType);
            dataJson.put("courseYear", request.getParameter("courseYear"));

            courSeason = -1;
            String str3;
            switch ((str3 = request.getParameter("courseSeason")).hashCode()) { case 22799:
              if (str3.equals("夏"));
              break;
            case 26149:
              if (str3.equals("春")) break; break;
            case 31179:
              if (!str3.equals("秋")) { break label662;

                courSeason = 0;
                break label662;

                courSeason = 1;
              } else
              {
                courSeason = 2;
              }
              break; }
            dataJson.put("courseSeason", courSeason);
            out.print(dataManager.AdminEvent(dataJson.toString()));
            break label2816;
          }
          else
          {
            System.out.println("Function: DisplayCourse");
            dataJson.put("dataType", "allCourse");
            return_value = dataManager.AdminEvent(dataJson.toString());
            System.out.println("--------:Display_course " + return_value);
            out.print(return_value);
            break label2816;

            dataJson.put("dataType", "addCourse");

            int courType = 0;
            switch ((courSeason = request.getParameter("courseType")).hashCode()) { case -379834069:
              if (courSeason.equals("计算机原理")) break; break;
            case -379382649:
              if (courSeason.equals("计算机设计"));
            case 797265117:
              if ((goto 866) && (courSeason.equals("数字逻辑")))
              {
                courType = 0;
                break label866;

                courType = 1;
                break label866;

                courType = 2;
              }
              break; }
            label866: dataJson.put("courseType", courType);
            dataJson.put("courseYear", request.getParameter("courseYear"));

            int courSeason = -1;
            String str4;
            switch ((str4 = request.getParameter("courseSeason")).hashCode()) { case 22799:
              if (str4.equals("夏"));
              break;
            case 26149:
              if (str4.equals("春")) break; break;
            case 31179:
              if (!str4.equals("秋")) { break label998;

                courSeason = 0;
                break label998;

                courSeason = 1;
              } else
              {
                courSeason = 2;
              }
              break; }
            label998: dataJson.put("courseName", request.getParameter("courseName"));
            dataJson.put("courseType", courType);
            dataJson.put("courseYear", request.getParameter("courseYear"));
            dataJson.put("courseSeason", courSeason);
            dataJson.put("courseID", request.getParameter("courseID"));
            dataJson.put("teacherID", request.getParameter("teacher"));
            dataManager.AdminEvent(dataJson.toString());

            dataJson.put("dataType", "courseAddTeacher");
            out.print(dataManager.AdminEvent(dataJson.toString()));
            break label2816;

            dataJson.put("dataType", "allDevice");
            out.print(dataManager.AdminEvent(dataJson.toString()));
          }break;
        }

        label1147: dataJson.put("result", "failed");
        dataJson.put("resultInfo", "dataType not found");
        out.print(dataJson.toString());
      }
      else
      {
        String courseID;
        if (user_type.equals("teacher")) {
          switch ((return_value = dataType).hashCode()) { case -890208436:
            if (return_value.equals("studentGetRecord"));
            break;
          case -255130855:
            if (return_value.equals("displayStudent"));
            break;
          case 29088346:
            if (return_value.equals("addStudent")) break; break;
          case 341443345:
            if (return_value.equals("getCourse"));
            break;
          case 375434205:
            if (!return_value.equals("reloadRbf")) { break label1745;

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
              break label2816;

              courseID = request.getParameter("courseID");
              response.addCookie(new Cookie("courseID", courseID));

              CookieData.setCourseID(user_number, courseID);
              dataJson.put("dataType", "allStudent");
              dataJson.put("courseID", courseID);
              out.print(dataManager.TeacherEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "teacherGetCourse");
              dataJson.put("teacherID", user_id);
              out.print(dataManager.TeacherEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "studentGetRecord");
              dataJson.put("studentID", request.getParameter("studentID"));
              dataJson.put("courseID", request.getParameter("courseID"));
              dataJson.put("expID", request.getParameter("expID"));
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;
            } else
            {
              dataJson.put("dataType", "reloadRbf");
              dataJson.put("studentID", request.getParameter("studentID"));
              dataJson.put("courseID", request.getParameter("courseID"));
              dataJson.put("expID", request.getParameter("expID"));
              dataJson.put("recordID", request.getParameter("recordID"));
              System.out.println(dataJson.toString());
              out.print(dataManager.StudentEvent(dataJson.toString()));
            }break;
          }

          label1745: dataJson.put("result", "failed");
          dataJson.put("resultInfo", "dataType not found");
          out.print(dataJson.toString());
        }
        else if (user_type.equals("student")) {
          switch ((courseID = dataType).hashCode()) { case -1025968882:
            if (courseID.equals("studentUploadInput"));
            break;
          case -890208436:
            if (courseID.equals("studentGetRecord"));
            break;
          case -571855943:
            if (courseID.equals("changeStudentInfo"));
            break;
          case -571659492:
            if (courseID.equals("changeStudentPass"));
            break;
          case 341443345:
            if (courseID.equals("getCourse"));
            break;
          case 375434205:
            if (courseID.equals("reloadRbf"));
            break;
          case 497585164:
            if (courseID.equals("studentToDevice")) break; break;
          case 1141628868:
            if (courseID.equals("studentSramInput"));
            break;
          case 1713769555:
            if (!courseID.equals("getStudentInfo")) { break label2787;

              System.out.println(dataType);
              dataJson.put("dataType", dataType);
              dataJson.put("studentID", user_id);
              CookieData.setCourseID(user_number, request.getParameter("courseID"));
              CookieData.setexprID(user_number, request.getParameter("exprID"));
              response.addCookie(new Cookie("courseID", request.getParameter("courseID")));
              response.addCookie(new Cookie("exprID", request.getParameter("exprID")));
              dataJson.put("operationType", "connect");
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", dataType);
              dataJson.put("studentID", user_id);
              dataJson.put("expID", request.getParameter("expID"));
              JSONArray tj = new JSONArray(request.getParameterValues("inputData[]"));
              System.out.println(tj.toString());
              dataJson.put("inputData", tj);
              String uploadResult = dataManager.StudentEvent(dataJson.toString());
              JSONObject upJson = new JSONObject(uploadResult);
              if (upJson.get("result").equals("succeed")) {
                dataJson.put("dataType", "studentGetOutput");
                out.print(dataManager.StudentEvent(dataJson.toString()));
                break label2816;
              }out.print(uploadResult);

              break label2816;

              dataJson.put("dataType", "studentSramInput");
              dataJson.put("studentID", user_id);
              dataJson.put("startAddr", request.getParameter("startAddr"));
              dataJson.put("endAddr", request.getParameter("endAddr"));
              dataJson.put("inputData", request.getParameter("inputData"));
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "studentGetCourse");
              dataJson.put("studentID", user_id);
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;
            } else
            {
              dataJson.put("dataType", "getStudentInfo");
              dataJson.put("studentID", user_id);
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "changeStudentInfo");
              dataJson.put("studentID", user_id);
              dataJson.put("mail", request.getParameter("email"));
              dataJson.put("tel", request.getParameter("phone"));
              dataJson.put("oldPassword", "");
              dataJson.put("newPassword", "");
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "changeStudentInfo");
              dataJson.put("studentID", user_id);
              dataJson.put("mail", "");
              dataJson.put("tel", "");
              dataJson.put("oldPassword", request.getParameter("oldPass"));
              dataJson.put("newPassword", request.getParameter("newPass"));
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "studentGetRecord");
              dataJson.put("studentID", user_id);
              dataJson.put("courseID", request.getParameter("courseID"));
              dataJson.put("expID", request.getParameter("expID"));
              out.print(dataManager.StudentEvent(dataJson.toString()));
              break label2816;

              dataJson.put("dataType", "reloadRbf");
              dataJson.put("studentID", user_id);
              dataJson.put("courseID", request.getParameter("courseID"));
              dataJson.put("expID", request.getParameter("expID"));
              dataJson.put("recordID", request.getParameter("recordID"));
              out.print(dataManager.StudentEvent(dataJson.toString()));
            }break;
          }

          label2787: dataJson.put("result", "failed");
          dataJson.put("resultInfo", "dataType not found");
          out.print(dataJson.toString());
        }
      }
    }
    label2816: out.flush();
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