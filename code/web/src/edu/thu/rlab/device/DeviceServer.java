package edu.thu.rlab.device;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class DeviceServer extends Thread
{
  private static final int MaxDeviceCount = 1024;
  private int serverPort;
  private DeviceDAO deviceDAO;

  public int getServerPort()
  {
    return this.serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }

  public DeviceDAO getDeviceDAO() {
    return this.deviceDAO;
  }

  public void setDeviceDAO(DeviceDAO deviceDAO) {
    this.deviceDAO = deviceDAO;
  }

  public void init() {
    start();
  }

  public void run()
  {
    ServerSocket server = null;
    Socket client = null;
    InetAddress clientIP = null;
    byte[] deviceBuffer = new byte[1024];
    try
    {
      server = new ServerSocket(getServerPort());
      System.out.println("Init Device Server succeed");
    } catch (Exception e) {
      System.out.println("Init Device Server wrong");
      e.printStackTrace();
      return;
    }
    while (true)
    {
      try {
        client = server.accept();

        clientIP = client.getInetAddress();
        BufferedInputStream in = new BufferedInputStream(client
          .getInputStream());
        int deviceCount = in.read(deviceBuffer);

        client.close();
        client = null;
      }
      catch (IOException e) {
        if (client != null) {
          try {
            client.close();
          } catch (IOException ce) {
            System.out.println("Close client socket error!");
            ce.printStackTrace();
          }
        }

        System.out.println("Read client stream error!");
        e.printStackTrace();
      }continue;
      int deviceCount;
      this.deviceDAO.updateDevicePool(clientIP.getHostAddress(), deviceBuffer, 
        deviceCount);
    }
  }
}