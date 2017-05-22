package edu.thu.rlab.database;

public class UserInfo
{
  private String user;
  private String password;
  private int permission;
  private String name;
  private String department;
  private String hisClass;
  private String email;
  private String tel;

  public UserInfo()
  {
  }

  public UserInfo(String user, String password, int permission, String name, String department, String hisClass, String email, String tel)
  {
    setUser(user);
    setPassword(password);
    setPermission(permission);
    setName(name);
    setDepartment(department);
    setHisClass(hisClass);
    setEmail(email);
    setTel(tel);
  }

  public String getUser()
  {
    return this.user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getPermission()
  {
    return this.permission;
  }

  public void setPermission(int permission) {
    this.permission = permission;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail()
  {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTel() {
    return this.tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getDepartment() {
    return this.department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getHisClass() {
    return this.hisClass;
  }

  public void setHisClass(String hisClass) {
    this.hisClass = hisClass;
  }
}