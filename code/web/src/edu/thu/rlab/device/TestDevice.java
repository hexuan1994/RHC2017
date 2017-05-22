package edu.thu.rlab.device;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

class TestDevice
  implements Runnable
{
  FileOutputStream out = null;
  String id;
  DeviceService service;

  public TestDevice(String id, DeviceServiceImpl service)
  {
    this.id = id;
    this.service = service;
    try {
      this.out = new FileOutputStream(id);
    } catch (FileNotFoundException e) {
      System.out.print("File cannot created!");
    }
  }

  public void run() {
    if (this.service.connect(this.id) == 0) {
      System.out.println(this.id + " connected to device");
    } else {
      System.out.println("no avilable device for " + this.id);
      return;
    }

    byte[] input = new byte[9];
    Random random = new Random();
    while (true)
    {
      String str = "input: ";
      for (int i = 0; i < input.length; i++) {
        input[i] = ((byte)random.nextInt(2));
        str = str + input[i] + " ";
      }

      str = str + "\n";
      try
      {
        this.out.write(str.getBytes());
      }
      catch (IOException localIOException) {
      }
      if (this.service.setInput(this.id, input, "Adder")) {
        System.out.println("");
      }

      byte[] result = this.service.getResult(this.id, "Adder");

      str = "output: ";
      for (int i = 0; i < result.length; i++) {
        str = str + result[i] + " ";
      }

      str = str + "\n\n";
      try {
        this.out.write(str.getBytes());
      }
      catch (IOException localIOException1)
      {
      }
    }
  }
}