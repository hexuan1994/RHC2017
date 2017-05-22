package edu.thu.rlab.device;

import java.io.PrintStream;

public class Device
  implements Runnable
{
  private DeviceDAO deviceDAO;
  private String id;
  private String ip;
  private int tcpPort;
  private int usbPort;
  private String location;
  private long lastHeartBeatTime;
  public int timeoutCount;
  private STATE state;
  private String userId;
  private long lastOpertaionTime;
  private long startUseTime;
  private byte[] buf = new byte[1024];

  public Device(String id, String ip, int tcpPort, int usbPort, STATE state, DeviceDAO deviceDAO)
  {
    this.id = id;
    this.ip = ip;
    this.tcpPort = tcpPort;
    this.usbPort = usbPort;
    this.state = state;
    this.lastHeartBeatTime = System.currentTimeMillis();
    this.timeoutCount = 0;
    this.deviceDAO = deviceDAO;
  }

  public void setDeviceDAO(DeviceDAO deviceDAO) {
    this.deviceDAO = deviceDAO;
  }

  public DeviceDAO getDeviceDAO() {
    return this.deviceDAO;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIp() {
    return this.ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getTcpPort() {
    return this.tcpPort;
  }

  public void setTcpPort(int tcpPort) {
    this.tcpPort = tcpPort;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public long getLastHeartBeatTime() {
    return this.lastHeartBeatTime;
  }

  public void setLastHeartBeatTime(long lastHeartBeatTime) {
    this.lastHeartBeatTime = lastHeartBeatTime;
  }

  public STATE getState() {
    return this.state;
  }

  public void setState(STATE state) {
    this.state = state;
  }

  public String getUser() {
    return this.userId;
  }

  public void setUser(String userId) {
    this.userId = userId;
  }

  public void setLastOpertaionTime(long lastOpertaionTime) {
    this.lastOpertaionTime = lastOpertaionTime;
  }

  public long getLastOpertaionTime() {
    return this.lastOpertaionTime;
  }

  public void setStartUseTime(long startUseTime) {
    this.startUseTime = startUseTime;
  }

  public long getStartUseTime() {
    return this.startUseTime;
  }

  public void run()
  {
    DeviceCmd cmd = new DeviceCmd(this);
    if (cmd.sysInit() < 0) {
      this.deviceDAO.afterDeviceSysInitFailed(this);
      System.out.println(this.id + " sync failed");
    } else {
      System.out.println(this.id + " sync success");
    }
  }

  public static enum STATE
  {
    USING, AVAILABLE, BROKEN;
  }
}