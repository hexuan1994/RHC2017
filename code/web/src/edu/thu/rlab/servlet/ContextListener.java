package edu.thu.rlab.servlet;

import edu.thu.rlab.device.DeviceService;
import edu.thu.rlab.device.DeviceServiceImpl;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener
  implements ServletContextListener
{
  private static DeviceService service = null;

  public void contextDestroyed(ServletContextEvent arg0)
  {
  }

  public void contextInitialized(ServletContextEvent arg0)
  {
    System.out.println("Init");
    service = new DeviceServiceImpl();

    File tempFile = new File(ContextListener.class.getResource("/").getFile().toString());
    String proDir = tempFile.getParent();
    try {
      proDir = URLDecoder.decode(proDir, "UTF-8");
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    System.out.println(proDir);
    service.init(proDir + System.getProperty("file.separator") + "device.properties");
  }

  public static DeviceService getService()
  {
    return service;
  }
}