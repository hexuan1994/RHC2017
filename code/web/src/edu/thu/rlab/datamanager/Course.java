package edu.thu.rlab.datamanager;

public class Course
{
  private String ID;
  private String name;
  private String professor;
  private int type;
  private int year;
  private int season;

  public Course()
  {
  }

  public Course(String ID, String name, int type, String professor, int year, int season)
  {
    setID(ID);
    setName(name);
    setType(type);
    setProfessor(professor);
    setYear(year);
    setSeason(season);
  }

  public String getID() {
    return this.ID;
  }

  public void setID(String ID) {
    this.ID = ID;
  }
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return this.type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getProfessor() {
    return this.professor;
  }

  public void setProfessor(String professor) {
    this.professor = professor;
  }

  public int getYear() {
    return this.year;
  }
  public void setYear(int year) {
    this.year = year;
  }

  public int getSeason() {
    return this.season;
  }

  public void setSeason(int season) {
    this.season = season;
  }
}