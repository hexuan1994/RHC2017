package edu.thu.rlab.database;

public class RecordInfo
{
  private int recordID;
  private String studentID;
  private String courseID;
  private String labID;
  private String path;
  private String time;

  public RecordInfo()
  {
  }

  public RecordInfo(int recordID, String studentID, String courseID, String labID, String path, String time)
  {
    setRecordID(recordID);
    setStudentID(studentID);
    setCourseID(courseID);
    setLabID(labID);
    setPath(path);
    setTime(time);
  }

  public int getRecordID() {
    return this.recordID;
  }

  public void setRecordID(int recordID) {
    this.recordID = recordID;
  }

  public String getStudentID() {
    return this.studentID;
  }

  public void setStudentID(String studentID) {
    this.studentID = studentID;
  }

  public String getCourseID() {
    return this.courseID;
  }

  public void setCourseID(String courseID) {
    this.courseID = courseID;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getTime() {
    return this.time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getLabID() {
    return this.labID;
  }

  public void setLabID(String labID) {
    this.labID = labID;
  }
}