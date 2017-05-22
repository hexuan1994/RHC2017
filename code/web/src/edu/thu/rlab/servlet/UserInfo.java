package edu.thu.rlab.servlet;

import javax.servlet.http.Cookie;

class UserInfo
{
  private String idCookie;
  private String passCookie;
  private String userType;
  private String courseID;
  private String exprID;

  public UserInfo(Cookie idCookie, Cookie passCookie, String userType)
  {
    this.idCookie = idCookie.getValue();
    this.passCookie = passCookie.getValue();
    this.userType = userType;
  }

  public String getID() {
    return this.idCookie;
  }

  public String getPass() {
    return this.passCookie;
  }

  public void setType(String userType) {
    this.userType = userType;
  }

  public void setID(String id) {
    this.idCookie = id;
  }

  public void setPass(String pass) {
    this.passCookie = pass;
  }

  public String getType() {
    return this.userType;
  }

  public String getCourseID() {
    return this.courseID;
  }

  public void setCourseID(String courseID) {
    this.courseID = courseID;
  }

  public String getexprID() {
    return this.exprID;
  }

  public void setexprID(String exprID) {
    this.exprID = exprID;
  }
}