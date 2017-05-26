package edu.thu.rlab.device;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main
{
  public static void main(String[] args)
  {
    DeviceService service = new DeviceServiceImpl();
    service.init("./device.properties");
    try {
      Thread.sleep(10000L);
    } catch (InterruptedException e) {
      System.out.println("interput");
    }

    InputStream is = null;
    try {
      is = new FileInputStream(".project");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    String type = "Counter";
    byte[] input = new byte[2];
    service.connect("Bob");
    service.sendRbf("Bob", is, type);
    service.setInput("Bob", input, type);

    input[0] = 1;
    input[1] = 1;
    for (int i = 0; i < 20; i++) {
      service.setInput("Bob", input, type);
      byte[] result = service.getResult("Bob", type);
      for (int k = 0; k < 14; k++)
        System.out.print(result[k] + " ");
      System.out.println();
    }
  }
}