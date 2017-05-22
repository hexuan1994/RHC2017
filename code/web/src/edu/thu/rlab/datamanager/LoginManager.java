package edu.thu.rlab.datamanager;

import edu.thu.rlab.database.DataBase;
import edu.thu.rlab.database.UserInfo;
import org.json.JSONObject;

public class LoginManager
{
  private DataBase db;

  public LoginManager()
  {
    this.db = DataBase.getInstance();
  }

  public String loginEvent(String username, String password)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "loginResult");

    int result = this.db.logInPermission(username, password);
    switch (result) {
    case -2:
      json.put("result", "fail");
      json.put("resultInfo", "usernameWrong");
      break;
    case -1:
      json.put("result", "fail");
      json.put("resultInfo", "passwordWrong");
      break;
    case 0:
      json.put("result", "succeed");
      json.put("resultInfo", "admin");
      break;
    case 1:
      json.put("result", "succeed");
      json.put("resultInfo", "teacher");
      break;
    case 2:
      json.put("result", "succeed");
      json.put("resultInfo", "student");
    }
    return json.toString();
  }

  public String getPermissionEvent(String userID)
  {
    JSONObject json = new JSONObject();
    json.put("dataType", "getPermission");

    UserInfo user = this.db.getUserInfo(userID);

    if (user.getUser() == null) {
      json.put("permission", "noSuchPerson");
    }
    else {
      switch (user.getPermission()) {
      case 0:
        json.put("permission", "admin");
        break;
      case 1:
        json.put("permission", "teacher");
        break;
      case 2:
        json.put("permission", "student");
        break;
      default:
        json.put("permission", "noSuchPerson");
      }
    }
    return json.toString();
  }
}