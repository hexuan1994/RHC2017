package edu.thu.rlab.servlet;

import java.io.PrintStream;
import java.util.ArrayList;
import javax.servlet.http.Cookie;

public class CookieData
{
  private static ArrayList<UserInfo> userInfoList = new ArrayList();

  public static void addList(Cookie idCookie, Cookie passCookie, String curUserType) {
    UserInfo genUserInfo = new UserInfo(idCookie, passCookie, curUserType);
    userInfoList.add(genUserInfo);
    System.out.println("Cookie: Add done");
  }

  public static int getSize() {
    return userInfoList.size();
  }

  public static String getID(int user_number) {
    if ((user_number >= 0) && (user_number < getSize())) {
      return ((UserInfo)userInfoList.get(user_number)).getID();
    }
    System.out.println(user_number);
    return "failed";
  }

  public static String getPass(int user_number)
  {
    if ((user_number >= 0) && (user_number < getSize())) {
      return ((UserInfo)userInfoList.get(user_number)).getPass();
    }
    System.out.println(user_number);
    return "failed";
  }

  public static String getType(int user_number)
  {
    if ((user_number >= 0) && (user_number < getSize())) {
      return ((UserInfo)userInfoList.get(user_number)).getType();
    }
    System.out.println(user_number);
    return "failed";
  }

  public static String getCourseID(int number)
  {
    String courseID = ((UserInfo)userInfoList.get(number)).getCourseID();
    if (courseID == null) {
      return "";
    }
    return courseID;
  }

  public static void setexprID(int number, String id) {
    ((UserInfo)userInfoList.get(number)).setexprID(id);
  }

  public static String getexprID(int number) {
    String exprID = ((UserInfo)userInfoList.get(number)).getexprID();
    if (exprID == null) {
      return "";
    }
    return exprID;
  }

  public static void setCourseID(int number, String id) {
    ((UserInfo)userInfoList.get(number)).setCourseID(id);
  }

  public static int getNumber(Cookie[] userCookie)
  {
    String user_id = "";
    int user_number = -1;
    String user_pass = "";

    if (userCookie != null) {
      Cookie[] arrayOfCookie = userCookie; int j = userCookie.length; for (int i = 0; i < j; i++) { Cookie c = arrayOfCookie[i];
        String str1;
        switch ((str1 = c.getName()).hashCode()) { case -292412867:
          if (str1.equals("user_number"));
          break;
        case -147132913:
          if (str1.equals("user_id")) break; break;
        case 339400709:
          if (!str1.equals("user_pass")) { continue;

            user_id = c.getValue();
            continue;

            user_number = Integer.parseInt(c.getValue());
          } else
          {
            user_pass = c.getValue();
          }
          break;
        }
      }

    }

    if ((user_number < 0) || (user_id == "") || (user_pass == ""))
    {
      System.out.print("{result:failed-1}");
      return -1;
    }if (user_number >= getSize()) {
      System.out.print("{result:failed-2, cookieSize : " + getSize() + "}");
      return -1;
    }if (!user_id.equals(getID(user_number))) {
      System.out.print("{result:failed-3}");
      return -1;
    }if (!user_pass.equals(getPass(user_number))) {
      System.out.println(user_number + " " + user_pass + " " + user_id);
      System.out.print("{result:failed-4}");
      return -1;
    }

    return user_number;
  }
}