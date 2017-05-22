package edu.thu.rlab.datamanager;

import java.util.List;

public class Teacher
{
  private String ID;
  private String mail;
  private String tel;
  private List<Course> courseList;

  public Teacher()
  {
  }

  public Teacher(String ID, String mail, String tel, List<Course> courseList)
  {
    setID(ID);
    setMail(mail);
    setTel(tel);
    setCourseList(courseList);
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }

  public String getMail() {
    return this.mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getTel() {
    return this.tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public List<Course> getCourseList() {
    return this.courseList;
  }

  public void setCourseList(List<Course> courseList) {
    this.courseList = courseList;
  }
}