package edu.thu.rlab.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBase
{
  private static DataBase dataBase = null;
  private String filePath;
  private Connection conn = null;

  private DataBase() {
    Initialize();
  }

  private boolean testUserInfo(UserInfo userInfo)
  {
    boolean ret = false;

    if ((userInfo.getUser() != null) && (userInfo.getPassword() != null) && (userInfo.getPermission() >= 0) && (userInfo.getPermission() <= 2)) {
      if (userInfo.getName() == null) {
        userInfo.setName("-");
      }
      if (userInfo.getDepartment() == null) {
        userInfo.setDepartment("-");
      }
      if (userInfo.getHisClass() == null) {
        userInfo.setHisClass("-");
      }
      if (userInfo.getEmail() == null) {
        userInfo.setEmail("-");
      }
      if (userInfo.getTel() == null) {
        userInfo.setTel("-");
      }
      ret = true;
    }

    return ret;
  }

  private boolean testCourseInfo(CourseInfo courseInfo)
  {
    boolean ret = false;

    if ((courseInfo.getID() != null) && (courseInfo.getName() != null) && (courseInfo.getProfessor() != null) && 
      (courseInfo.getType() >= 0) && (courseInfo.getType() <= 2) && (courseInfo.getYear() >= 0) && 
      (courseInfo.getSeason() >= 0) && (courseInfo.getSeason() <= 2)) {
      ret = true;
    }

    return ret;
  }

  private void addUsers(UserInfo userInfo)
    throws SQLException
  {
    if (testUserInfo(userInfo)) {
      Statement stmt = this.conn.createStatement();
      String sql = 
        "insert into users(user, password, permission, name, department, class, Email, Tel) values(\"" + 
        userInfo.getUser() + "\",\"" + userInfo.getPassword() + "\"," + String.valueOf(userInfo.getPermission()) + ",\"" + 
        userInfo.getName() + "\",\"" + userInfo.getDepartment() + "\",\"" + userInfo.getHisClass() + "\",\"" + 
        userInfo.getEmail() + "\",\"" + userInfo.getTel() + "\") " + 
        "on duplicate key update password = \"" + userInfo.getPassword() + "\", permission = " + 
        String.valueOf(userInfo.getPermission()) + ", name = \"" + userInfo.getName() + "\", department = \"" + 
        userInfo.getDepartment() + "\", class = \"" + userInfo.getHisClass() + "\", Email = \"" + 
        userInfo.getEmail() + "\", Tel = \"" + userInfo.getTel() + "\";";
      System.out.println(sql);
      stmt.execute(sql);
    }
  }

  private void removeUsers(String user)
    throws SQLException
  {
    if (user != null) {
      Statement stmt = this.conn.createStatement();
      String sql = "delete from users where user = \"" + user + "\";";
      stmt.execute(sql);
    }
  }

  public static synchronized DataBase getInstance()
  {
    if (dataBase == null)
      dataBase = new DataBase();
    return dataBase;
  }

  private void Initialize()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      System.out.println("The driver of Mysql has been loaded!");
    } catch (ClassNotFoundException e) {
      System.out.println("Can not find the driver of Mysql!");
    }

    String url = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    try
    {
      this.conn = DriverManager.getConnection(url, "root", "RHC2017");
      System.out.println("Successfully connect to database!");
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Can not connect to database!");
    }

    try
    {
      Statement stmt = this.conn.createStatement();
      String[] sqls = { 
        "use rlab;" };

      for (int i = 0; i < sqls.length; i++)
      {
        stmt.execute(sqls[i]);
      }

      File curFile = new File(DataBase.class.getResource("/").getFile().toString());
      this.filePath = (curFile.getParent() + "/records/");
      this.filePath = this.filePath.replaceAll("\\\\", "/");
      File file = new File(this.filePath);
      file.mkdirs();
    }
    catch (SQLException e) {
      System.out.println("Fail to initialize the database!");
    }
  }

  public int logInPermission(String userID, String password)
  {
    int permission = -1;
    try
    {
      Statement stmt = this.conn.createStatement();
      String sql = "select password, permission from users where user=\"" + userID + "\";";
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        String passwordInDataBase = rs.getString(1);
        int permissionInDataBase = rs.getInt(2);
        if (password.equals(passwordInDataBase))
          permission = permissionInDataBase;
      }
      else {
        permission = -2;
      }
    } catch (SQLException e) {
      System.out.println("Wrong in logInPermission()!");
    }

    return permission;
  }

  public int addProfessors(int permission, UserInfo userInfo)
  {
    if (permission == 0) {
      try {
        if (userInfo.getPermission() != 1) {
          userInfo.setPermission(1);
        }
        addUsers(userInfo);
        return 1;
      } catch (SQLException e) {
        System.out.println("Wrong in addProfessors()!");
        return 0;
      }
    }
    return -1;
  }

  public int removeProfessors(int permission, String user)
  {
    if (permission == 0) {
      try {
        removeUsers(user);
        return 1;
      } catch (SQLException e) {
        System.out.println("Wrong in removeProfessors()!");
        return 0;
      }
    }
    return -1;
  }

  public int addStudents(int permission, UserInfo userInfo, String courseID)
  {
    if (permission == 1) {
      try {
        if (userInfo.getPermission() != 2) {
          userInfo.setPermission(2);
        }
        addUsers(userInfo);
        if (!courseID.equals("-")) {
          addStudentInCourse(courseID, userInfo.getUser());
        }
        return 1;
      } catch (SQLException e) {
        System.out.println("Wrong in addStudents()!");
        return 0;
      }
    }
    return -1;
  }

  private void addStudentInCourse(String courseID, String StudentID)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement();
    String sql = "insert ignore into course_" + courseID + " values (\"" + StudentID + "\");";
    stmt.execute(sql);
  }

  public int removeStudents(int permission, String UserID, String courseID)
  {
    if (permission == 1) {
      try {
        removeStudentInCourse(courseID, UserID);
        return 1;
      } catch (SQLException e) {
        System.out.println("Wrong in removeStudents()!");
        return 0;
      }
    }
    return -1;
  }

  private void removeStudentInCourse(String courseID, String StudentID)
    throws SQLException
  {
    Statement stmt = this.conn.createStatement();
    String sql = "delete from course_" + courseID + " where student = \"" + StudentID + "\";";
    stmt.execute(sql);
  }

  public UserInfo getUserInfo(String user)
  {
    UserInfo userInfo = new UserInfo();
    try {
      String sql = "select * from users where user = \"" + user + "\";";
      PreparedStatement pstmt = this.conn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        userInfo.setUser(rs.getString(2));
        userInfo.setPassword(rs.getString(3));
        userInfo.setPermission(rs.getInt(4));
        userInfo.setName(rs.getString(5));
        userInfo.setDepartment(rs.getString(6));
        userInfo.setHisClass(rs.getString(7));
        userInfo.setEmail(rs.getString(8));
        userInfo.setTel(rs.getString(9));
      }
    }
    catch (SQLException e) {
      System.out.println("Wrong in getUserInfo()!");
    }
    return userInfo;
  }

  public List<UserInfo> getAllUserInfo(int permission)
  {
    List<UserInfo> list = new ArrayList<UserInfo>();
    if (permission == 0) {
      try {
        String sql = "select * from users;";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
          UserInfo userInfo = new UserInfo();
          userInfo.setUser(rs.getString("user"));
          userInfo.setPassword(rs.getString("password"));
          userInfo.setPermission(rs.getInt("permission"));
          userInfo.setName(rs.getString("name"));
          userInfo.setDepartment(rs.getString("department"));
          userInfo.setHisClass(rs.getString("class"));
          userInfo.setEmail(rs.getString("Email"));
          userInfo.setTel(rs.getString("Tel"));
          list.add(userInfo);
        }
      } catch (SQLException e) {
        System.out.println("Wrong in getAllUserInfo()!");
      }
    }
    return list;
  }

  public int addCourses(int permission, CourseInfo courseInfo)
  {
    if (permission == 0) {
      if (testCourseInfo(courseInfo)) {
        try {
          Statement stmt = this.conn.createStatement();

          String sql1 = "insert into courses values (\"" + courseInfo.getID() + "\",\"" + courseInfo.getName() + 
            "\",\"" + courseInfo.getProfessor() + "\"," + String.valueOf(courseInfo.getType()) + "," + 
            String.valueOf(courseInfo.getYear()) + "," + String.valueOf(courseInfo.getSeason()) + 
            ") on duplicate key update name = \"" + courseInfo.getName() + "\", professor = \"" + courseInfo.getProfessor() + 
            "\", type = " + String.valueOf(courseInfo.getType()) + ", year = " + String.valueOf(courseInfo.getYear()) + 
            ", season = " + String.valueOf(courseInfo.getSeason()) + ";";

          String sql2 = "create table if not exists course_" + courseInfo.getID() + 
            "(" + 
            "student char(12) not null primary key" + 
            ");";

          stmt.execute(sql1);
          stmt.execute(sql2);
          return 1;
        } catch (SQLException e) {
          System.out.println("Wrong in addCourses()!");
          return 0;
        }
      }
      return -2;
    }

    return -1;
  }

  public int removeCourses(int permission, String courseID)
  {
    if (permission == 0) {
      try {
        Statement stmt = this.conn.createStatement();
        String sql1 = "delete from courses where id = \"" + courseID + "\";";
        String sql2 = "drop table course_" + courseID + ";";
        stmt.execute(sql1);
        stmt.execute(sql2);
        return 1;
      } catch (SQLException e) {
        System.out.println("Wrong in removeCourses()!");
        return 0;
      }
    }
    return -1;
  }

  public List<CourseInfo> getAllCourseInfo(int permission)
  {
    List<CourseInfo> list = new ArrayList<CourseInfo>();
    if (permission == 0) {
      try {
        String sql = "select * from courses;";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
          CourseInfo courseInfo = new CourseInfo();
          courseInfo.setID(rs.getString(1));
          courseInfo.setName(rs.getString(2));
          courseInfo.setProfessor(rs.getString(3));
          courseInfo.setType(rs.getInt(4));
          courseInfo.setYear(rs.getInt(5));
          courseInfo.setSeason(rs.getInt(6));
          list.add(courseInfo);
        }
      } catch (SQLException e) {
        System.out.println("Wrong in getAllCourseInfo()!");
      }
    }
    return list;
  }

  public String addRecords(String courseID, String labID, String studentID)
  {
    if ((courseID == null) || (labID == null) || (studentID == null)) {
      return null;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    String time = dateFormat.format(new Date());
    int nextRecordID = 0;
    try {
      String sql = "select AUTO_INCREMENT from INFORMATION_SCHEMA.TABLES where TABLE_NAME=\"records\";";
      PreparedStatement pstmt = this.conn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next())
        nextRecordID = rs.getInt(1);
    }
    catch (SQLException e) {
      System.out.println("Wrong when finding nextRecordID in addRecords()!");
    }

    String tempPath = this.filePath + courseID + "/" + studentID + "/" + labID;
    File file = new File(tempPath);
    file.mkdirs();
    System.out.print(file.getAbsolutePath());

    String path = tempPath + "/" + String.valueOf(nextRecordID) + "___" + time;
    try
    {
      Statement stmt = this.conn.createStatement();
      String sql = "insert into records (studentID, courseID, labID, path, time) values(\"" + studentID + "\",\"" + courseID + "\",\"" + labID + "\",\"" + path + "\",\"" + time + "\");";
      stmt.execute(sql);
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("Wrong in addRecords()!");
      return null;
    }

    return path;
  }

  public List<RecordInfo> getRecords(String courseID, String labID, String studentID)
  {
    List<RecordInfo> list = new ArrayList<RecordInfo>();
    try {
      String sql = "select * from records where courseID = \"" + courseID + "\" and labID = \"" + labID + "\" and studentID = \"" + studentID + "\";";
      PreparedStatement pstmt = this.conn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setRecordID(rs.getInt(1));
        recordInfo.setStudentID(rs.getString(2));
        recordInfo.setCourseID(rs.getString(3));
        recordInfo.setLabID(rs.getString(4));
        recordInfo.setPath(rs.getString(5));
        recordInfo.setTime(rs.getString(6));
        list.add(recordInfo);
      }
    } catch (SQLException e) {
      System.out.println("Wrong in getRecords()!");
      return null;
    }
    return list;
  }

  public List<CourseInfo> getCourseByStudentID(String studentID)
  {
    List<CourseInfo> list = new ArrayList<CourseInfo>();
    try {
      List<CourseInfo> tempList = getAllCourseInfo(0);
      for (int i = 0; i < tempList.size(); i++) {
        CourseInfo courseInfo = (CourseInfo)tempList.get(i);
        String courseID = courseInfo.getID();

        String sql = "select * from course_" + courseID + " where student = \"" + studentID + "\";";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next())
          list.add(courseInfo);
      }
    }
    catch (SQLException e) {
      System.out.println("Wrong in getCourseByStudentID()!");
    }
    return list;
  }

  public List<CourseInfo> getCourseByProfessorID(String professorID)
  {
    List<CourseInfo> list = new ArrayList<CourseInfo>();
    try {
      String sql = "select * from courses where professor = \"" + professorID + "\";";
      PreparedStatement pstmt = this.conn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setID(rs.getString(1));
        courseInfo.setName(rs.getString(2));
        courseInfo.setProfessor(rs.getString(3));
        courseInfo.setType(rs.getInt(4));
        courseInfo.setYear(rs.getInt(5));
        courseInfo.setSeason(rs.getInt(6));
        list.add(courseInfo);
      }
    } catch (SQLException e) {
      System.out.println("Wrong in getCourseByProfessorID()!");
    }
    return list;
  }

  public List<String> getStudentsInCourse(String courseID)
  {
    List<String> list = new ArrayList<String>();
    try {
      String sql = "select * from course_" + courseID + ";";
      PreparedStatement pstmt = this.conn.prepareStatement(sql);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next())
        list.add(rs.getString(1));
    }
    catch (SQLException e) {
      System.out.println("Wrong in getStudentsInCourse()!");
    }
    return list;
  }
}