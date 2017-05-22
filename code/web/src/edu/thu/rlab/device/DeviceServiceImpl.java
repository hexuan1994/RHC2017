package edu.thu.rlab.device;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeviceServiceImpl extends TimerTask
  implements DeviceService
{
  private long userTimeoutTime;
  private long deleteTimeoutUserPeriod;
  private Timer timer;
  private DeviceDAO deviceDAO;
  private Map<String, Device> userDeviceMap = new HashMap();
  private byte[] lock = new byte[0];

  public void init(String path)
  {
    InputStream is = null;
    try {
      is = new FileInputStream(path);
    } catch (FileNotFoundException e) {
      System.out.println("Cannot find properties");
      e.printStackTrace();
    }

    Properties properties = new Properties();
    try {
      properties.load(is);
    } catch (IOException e) {
      System.out.println("Cannot load properties");
      e.printStackTrace();
    }

    DeviceDAO deviceDAO = new DeviceDAO();
    deviceDAO.setDeviceHeartBeatPeriod(Integer.parseInt(properties.getProperty("DeviceHeartBeatPeriod", "10000")));
    deviceDAO.setTcpPortBase(Integer.parseInt(properties.getProperty("TcpPortBase")));
    deviceDAO.setDeleteOfflinePeriod(Integer.parseInt(properties.getProperty("DeleteOfflinePeriod", "1324")));
    deviceDAO.init();

    DeviceServer server = new DeviceServer();
    server.setServerPort(Integer.parseInt(properties.getProperty("ServerPort", "8013")));
    server.setDeviceDAO(deviceDAO);
    server.init();

    setDeleteTimeoutUserPeriod(Integer.parseInt(properties.getProperty("DeleteTimeoutUserPeriod", "2000")));
    setUserTimeoutTime(Integer.parseInt(properties.getProperty("UserTimeoutTime", "180000")));
    setDeviceDAO(deviceDAO);

    this.timer = new Timer();
    this.timer.schedule(this, 0L, this.deleteTimeoutUserPeriod);
  }

  public JSONArray listAll() {
    return this.deviceDAO.findAll();
  }

  public JSONObject listOne(String id)
  {
    return this.deviceDAO.findDevice(id);
  }

  public int connect(String user)
  {
    synchronized (this.lock) {
      if (this.userDeviceMap.containsKey(user)) {
        return 0;
      }

      Device device = this.deviceDAO.allocate(user);
      if (device == null) {
        System.out.println(user + ":No Available Device");
        return 1;
      }

      DeviceCmd cmd = new DeviceCmd(device);
      int ret = cmd.userInit();

      this.userDeviceMap.put(user, device);
      System.out.println(user + " connected to device:" + device.getId());
    }
    Device device;
    return 0;
  }

  public void disconnect(String user)
  {
    if (!this.userDeviceMap.containsKey(user))
      return;
    Device device;
    synchronized (this.lock) {
      device = (Device)this.userDeviceMap.remove(user);
    }
    Device device;
    this.deviceDAO.free(device);
  }

  public int sendRbf(String userId, InputStream rbf, String type)
  {
    ExperimentType tp = stringToType(type);
    if (tp == ExperimentType.Error)
      return -3;
    return sendRbf(userId, rbf, tp);
  }

  public boolean setInput(String userId, byte[] input, String type)
  {
    ExperimentType tp = stringToType(type);
    if (tp == ExperimentType.Error) {
      return false;
    }
    return setInput(userId, input, tp);
  }

  public byte[] getResult(String userId, String type)
  {
    String str;
    switch ((str = type).hashCode()) { case -1672483364:
      if (str.equals("Counter"));
      break;
    case -1088050383:
      if (str.equals("Decimal"));
      break;
    case -1087880156:
      if (str.equals("Decoder"));
      break;
    case 63107310:
      if (str.equals("Adder")) break; break;
    case 80204913:
      if (str.equals("State"));
      break;
    case 1314210581:
      if (!str.equals("MultiPlexer")) { break label178;

        return getAdderResult(userId);

        return getDecoderResult(userId);
      } else {
        return getPlexerResult(userId);

        return getDecimalResult(userId);

        return getCounterResult(userId);

        return getStateResult(userId);
      }
      break; }
    label178: return null;
  }

  private int sendRbf(String userId, InputStream rbf, ExperimentType type)
  {
    Device device = null;
    synchronized (this.lock) {
      device = (Device)this.userDeviceMap.get(userId);
    }

    if (device == null) {
      return 2;
    }
    DeviceCmd cmd = new DeviceCmd(device);
    cmd.setType(type.getCode());
    try
    {
      int ret = cmd.downFpgaFromUsb((byte)1, rbf, rbf.available());
      if (ret != 0)
        return 3;
    }
    catch (IOException e) {
      return 3;
    }

    return 0;
  }

  private boolean setInput(String userId, byte[] input, ExperimentType type)
  {
    Device device = null;
    synchronized (this.lock) {
      device = (Device)this.userDeviceMap.get(userId);
    }

    if (device == null) {
      return false;
    }
    DeviceCmd cmd = new DeviceCmd(device);

    switch (type) {
    case Counter:
    case Decimal:
    case Decoder:
    case Error:
    case Multiplexer:
    case State:
      int num = 0;
      for (int i = 0; i < input.length; i++) {
        num |= input[i] << i;
      }

      int ret = cmd.writeRegs(255, num);
      if (ret != 0) {
        return false;
      }
      break;
    }
    return true;
  }

  public byte[] setSRAMInput(String userId, int startAddr, int endAddr, byte[] data) {
    Device device = null;
    synchronized (this.lock) {
      device = (Device)this.userDeviceMap.get(userId);
    }
    if (device == null) {
      return null;
    }
    DeviceCmd cmd = new DeviceCmd(device);
    byte[] res = null;

    if (data == null) {
      res = cmd.readRam((endAddr - startAddr + 1) * 4, startAddr, endAddr);
      if (res != null) {
        System.out.printf("res.length = %d\n", new Object[] { Integer.valueOf(res.length) });
        for (int i = 0; i < res.length; i++)
          System.out.printf("%d%d%d%d%d%d%d%d", new Object[] { Integer.valueOf(res[i] >> 7 & 0x1), Integer.valueOf(res[i] >> 6 & 0x1), Integer.valueOf(res[i] >> 5 & 0x1), Integer.valueOf(res[i] >> 4 & 0x1), Integer.valueOf(res[i] >> 3 & 0x1), Integer.valueOf(res[i] >> 2 & 0x1), Integer.valueOf(res[i] >> 1 & 0x1), Integer.valueOf(res[i] & 0x1) });
      }
    }
    else {
      InputStream is = new ByteArrayInputStream(data);
      int result = cmd.writeRam(is, (endAddr - startAddr + 1) * 4, startAddr, endAddr);
      if (result != 0) System.out.println("writeRam wrong");
    }
    return res;
  }

  private byte[] getAdderResult(String userId)
  {
    return getResult(userId, 5);
  }

  private byte[] getDecoderResult(String userId)
  {
    return getResult(userId, 7);
  }

  private byte[] getPlexerResult(String userId)
  {
    return getResult(userId, 1);
  }

  private byte[] getDecimalResult(String userId)
  {
    byte[] re = getResult(userId, 28);
    byte[] result = new byte[36];
    for (int i = 0; i < re.length; i++)
      result[i] = re[i];
    return result;
  }

  private byte[] getCounterResult(String userId)
  {
    return getResult(userId, 14);
  }

  private byte[] getStateResult(String userId)
  {
    return getResult(userId, 8);
  }

  private byte[] getResult(String userId, int bits)
  {
    Device device = null;
    synchronized (this.lock) {
      device = (Device)this.userDeviceMap.get(userId);
    }

    if (device == null) {
      return null;
    }
    DeviceCmd cmd = new DeviceCmd(device);
    byte[] result = new byte[bits];
    int[] re = cmd.getRegs();
    for (int i = 0; i < bits; i++) {
      result[i] = ((byte)(re['þ'] >> i & 0x1));
    }
    return result;
  }

  public void run()
  {
    synchronized (this.lock)
    {
      Collection devices = this.userDeviceMap.values();
      Iterator it = devices.iterator();
      Set toDelete = new HashSet(0);
      while (it.hasNext()) {
        Device device = (Device)it.next();
        long l1 = System.currentTimeMillis();
        if (System.currentTimeMillis() - device.getLastOpertaionTime() > this.userTimeoutTime) {
          String user = device.getUser();
          toDelete.add(user);
        }
      }

      it = toDelete.iterator();
      while (it.hasNext()) {
        String user = (String)it.next();
        Device device = (Device)this.userDeviceMap.remove(user);
        this.deviceDAO.free(device);
      }
    }
  }

  ExperimentType stringToType(String type)
  {
    String str;
    switch ((str = type).hashCode()) { case -1672483364:
      if (str.equals("Counter"));
      break;
    case -1088050383:
      if (str.equals("Decimal"));
      break;
    case -1087880156:
      if (str.equals("Decoder"));
      break;
    case 63107310:
      if (str.equals("Adder")) break; break;
    case 80204913:
      if (str.equals("State"));
      break;
    case 1314210581:
      if (!str.equals("MultiPlexer")) { break label166;

        return ExperimentType.Adder;

        return ExperimentType.Decoder;
      } else {
        return ExperimentType.Multiplexer;

        return ExperimentType.Decimal;

        return ExperimentType.Counter;

        return ExperimentType.State;
      }break; }
    label166: return ExperimentType.Error;
  }

  public void setUserTimeoutTime(long userTimeoutTime)
  {
    this.userTimeoutTime = userTimeoutTime;
  }

  public void setDeleteTimeoutUserPeriod(long deleteTimeoutUserPeriod) {
    this.deleteTimeoutUserPeriod = deleteTimeoutUserPeriod;
  }

  public void setDeviceDAO(DeviceDAO deviceDAO) {
    this.deviceDAO = deviceDAO;
  }

  public static enum ExperimentType
  {
    Error(0), Adder(1), Decoder(2), Multiplexer(3), Decimal(4), Counter(5), State(6);

    int code;

    private ExperimentType(int code) { this.code = code; }

    public int getCode()
    {
      return this.code;
    }
  }
}