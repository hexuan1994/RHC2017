package edu.thu.rlab.servlet;

import edu.thu.rlab.datamanager.DataManager;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet(description="handle login username & password", urlPatterns={"/LoginServlet"})
public class LoginServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;

  public LoginServlet()
  {
    System.out.println("login servlet init done!!!");
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html;charset=utf-8");
    PrintWriter out = response.getWriter();
    String username = request.getParameter("username");
    String password = request.getParameter("password");

    JSONObject newJson = new JSONObject();
    newJson.put("dataType", "login");
    newJson.put("username", username);
    newJson.put("password", password);

    DataManager loginManager = new DataManager();
    String returnValue = loginManager.LoginEvent(newJson.toString());

    JSONObject verificateJson = new JSONObject(returnValue);
    if (verificateJson.getString("result").equals("succeed"))
    {
      Cookie idCookie = new Cookie("user_id", username);
      String cookiePass = password;
      try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        cookiePass = new BigInteger(1, md.digest()).toString(16);
      } catch (Exception e) {
        System.out.println("MD5 wrong");
      }
      Cookie passCookie = new Cookie("user_pass", cookiePass);
      Cookie numberCookie = new Cookie("user_number", Integer.toString(CookieData.getSize()));

      String curUserType = verificateJson.getString("resultInfo");
      CookieData.addList(idCookie, passCookie, curUserType);

      idCookie.setMaxAge(1800);
      passCookie.setMaxAge(1800);
      numberCookie.setMaxAge(1800);
      response.addCookie(idCookie);
      response.addCookie(passCookie);
      response.addCookie(numberCookie);
    }

    out.print(returnValue);
    out.flush();
    out.close();
  }
}