package edu.thu.rlab.device;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeviceDAO extends TimerTask
{
  private Random random;
  private int tcpPortBase;
  private long deviceHeartBeatPeriod;
  private long deleteOfflinePeriod;
  private Map<String, Device> devicePool = new HashMap(0);
  private byte[] lock = new byte[0];
  private Timer timer;

  public void init()
  {
    this.random = new Random();
    this.timer = new Timer();
    this.timer.schedule(this, 0L, this.deleteOfflinePeriod);
  }

  public void updateDevicePool(String ip, byte[] deviceBuffer, int count)
  {
    int usbPort = deviceBuffer[0];
    int tcpPort = usbPort + this.tcpPortBase;
    String deviceId = ip + ":" + tcpPort;
    Device device = (Device)this.devicePool.get(deviceId);

    if (device != null) {
      synchronized (this.lock) {
        System.out.println(deviceId + " beat");
        device.setLastHeartBeatTime(System.currentTimeMillis());
        device.timeoutCount = 0;
      }
    }
    System.out.println(deviceId + " added");
    device = new Device(deviceId, ip, tcpPort, usbPort, 
      Device.STATE.AVAILABLE, this);
    new Thread(device).start();
    synchronized (this.lock) {
      System.out.println("DEBUG:Before put devicePool");
      System.out.printf("%s, %s, %d, %d\n", new Object[] { deviceId, ip, Integer.valueOf(tcpPort), Integer.valueOf(usbPort) });
      this.devicePool.put(deviceId, device);
      System.out.printf("devicePool.size = %d\n", new Object[] { Integer.valueOf(this.devicePool.size()) });
    }
  }

  public Device allocate(String userId)
  {
    Device device = null;
    ArrayList allocatable = new ArrayList();
    synchronized (this.lock) {
      Collection devices = this.devicePool.values();
      Iterator it = devices.iterator();

      while (it.hasNext()) {
        device = (Device)it.next();
        System.out.println(device.getId() + ": " + device.getState());
        if ((Device.STATE.AVAILABLE.equals(device.getState())) && (device.timeoutCount == 0)) {
          allocatable.add(device);
        }
      }

      if (allocatable.size() > 0) {
        device = (Device)allocatable.get(this.random.nextInt(allocatable.size()));
        device.setState(Device.STATE.USING);
        device.setUser(userId);
        device.setStartUseTime(System.currentTimeMillis());
        device.setLastOpertaionTime(System.currentTimeMillis());
      } else {
        return null;
      }
    }
    return device;
  }

  public void free(Device device)
  {
    synchronized (this.lock) {
      device.setState(Device.STATE.AVAILABLE);
    }
  }

  public void run()
  {
    synchronized (this.lock)
    {
      long current = System.currentTimeMillis();
      Collection devices = this.devicePool.values();
      Iterator it = devices.iterator();
      Set toDelete = new HashSet(0);
      while (it.hasNext()) {
        Device device = (Device)it.next();

        if (current - device.getLastHeartBeatTime() > this.deviceHeartBeatPeriod) {
          device.timeoutCount += 1;
          device.setLastHeartBeatTime(current);

          if (device.timeoutCount > 5)
          {
            toDelete.add(device.getId());
          }
        }
      }
      it = toDelete.iterator();
      while (it.hasNext()) {
        String str = (String)it.next();
        Device device = (Device)this.devicePool.remove(str);
        device = null;
      }
    }
  }

  public JSONArray findAll()
  {
    JSONArray da = new JSONArray();
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    synchronized (this.lock) {
      Iterator it = this.devicePool.values().iterator();
      while (it.hasNext()) {
        Device d = (Device)it.next();
        JSONObject dObj = new JSONObject();
        dObj.put("id", d.getId());
        dObj.put("ip", d.getIp().toString());
        dObj.put("port", d.getTcpPort());
        dObj.put("location", d.getLocation());
        date.setTime(d.getLastHeartBeatTime());
        dObj.put("lastHeartBeatTime", sdf.format(date));
        dObj.put("state", d.getState());
        if (d.getState().equals(Device.STATE.USING)) {
          dObj.put("userId", d.getUser());
          date.setTime(d.getStartUseTime());
          dObj.put("startUseTime", sdf.format(date));
          date.setTime(d.getLastOpertaionTime());
          dObj.put("lastOpTime", sdf.format(date));
        } else {
          dObj.put("userId", "");
          dObj.put("startUserTime", "");
          dObj.put("lastOptime", "");
        }
        da.put(dObj);
      }
    }
    return da;
  }

  public JSONObject findDevice(String id) {
    Device d = (Device)this.devicePool.get(id);
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    JSONObject dObj = new JSONObject();
    synchronized (this.lock) {
      dObj.put("id", d.getId());
      dObj.put("ip", d.getIp().toString());
      dObj.put("port", d.getTcpPort());
      dObj.put("location", d.getLocation());
      date.setTime(d.getLastHeartBeatTime());
      dObj.put("lastHeartBeatTime", sdf.format(date));
      dObj.put("state", d.getState());
      if (d.getState().equals(Device.STATE.USING)) {
        dObj.put("userId", d.getUser());
        date.setTime(d.getStartUseTime());
        dObj.put("startUseTime", sdf.format(date));
        date.setTime(d.getLastOpertaionTime());
        dObj.put("lastOpTime", sdf.format(date));
      } else {
        dObj.put("userId", "");
        dObj.put("startUseTime", "");
        dObj.put("lastOpTime", "");
      }
    }

    return dObj;
  }

  public void updateLastOperationTime(Device device)
  {
    synchronized (this.lock) {
      device.setLastOpertaionTime(System.currentTimeMillis());
    }
  }

  public void afterDeviceSysInitFailed(Device device)
  {
    synchronized (this.lock) {
      device.setState(Device.STATE.BROKEN);
    }
  }

  public void setTcpPortBase(int tcpPortBase) {
    this.tcpPortBase = tcpPortBase;
  }

  public void setDeviceHeartBeatPeriod(long deviceHeartBeatPeriod) {
    this.deviceHeartBeatPeriod = deviceHeartBeatPeriod;
  }

  public void setDeleteOfflinePeriod(long deleteOfflinePeriod) {
    this.deleteOfflinePeriod = deleteOfflinePeriod;
  }
}