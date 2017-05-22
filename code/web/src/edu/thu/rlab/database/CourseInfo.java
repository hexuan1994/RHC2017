package edu.thu.rlab.database;

public class CourseInfo
{
  private String ID;
  private String name;
  private String professor;
  private int type;
  private int year;
  private int season;

  public CourseInfo()
  {
  }

  public CourseInfo(String ID, String name, String professor, int type, int year, int season)
  {
    setID(ID);
    setName(name);
    setProfessor(professor);
    setType(type);
    setYear(year);
    setSeason(season);
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String iD) {
    this.ID = iD;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProfessor() {
    return this.professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public int getSeason() {
    return this.season;
  }

  public void setSeason(int season) {
    this.season = season;
  }

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getYear() {
    return this.year;
  }

  public void setYear(int year) {
    this.year = year;
  }
}