package edu.thu.rlab.device;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class DeviceCmd
{
  private Device device;
  private Socket socket;
  private BufferedOutputStream out;
  private BufferedInputStream in;
  private TYPE type;
  private static final int BufSize = 1024;
  private byte[] buf = new byte[1024];

  public DeviceCmd(Device device)
  {
    this.device = device;
  }

  public int writeRam(InputStream is, int len, int startAdd, int endAdd)
  {
    if (connect() < 0) {
      return -1;
    }
    try
    {
      write(TYPE.WriteRam.getCode());
      write(len);
      write(startAdd);
      write(endAdd);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return ret;
      }

      while ((len = is.read(this.buf)) > 0)
        write(this.buf, 0, len);
      flush();

      ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return -1;
    }
    System.out.println("WriteRam successful!");
    return 0;
  }

  public byte[] readRam(int len, int startAdd, int endAdd)
  {
    if (connect() < 0) {
      return null;
    }

    try
    {
      write(TYPE.ReadRam.getCode());
      write(len);
      write(startAdd);
      write(endAdd);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return null;
      }

      byte[] ram = new byte[len];
      int offset = 0;
      int length;
      while ((offset < len) && (
        (length = read(this.buf, 0, 1024)) > 0))
      {
        int length;
        System.arraycopy(this.buf, 0, ram, offset, length);
        offset += length;
      }

      write((byte)0);
      flush();

      ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
    byte[] ram;
    return ram;
  }

  public int writeRegs(int addr, int num)
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.WriteRegs.getCode());
      write(addr);
      write(num);
      flush();
      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int setType(int type)
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.ExprimentType.getCode());
      write(type);
      flush();
      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return -1;
      }
    }
    catch (IOException localIOException)
    {
    }
    return 0;
  }

  public int writeFpgaToFlash(byte flashSelector, InputStream is, int len)
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.WriteFpgaToFlash.getCode());
      write(flashSelector);
      write(len);
      flush();
      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return -1;
      }

      while ((len = is.read(this.buf)) > 0) {
        write(this.buf, 0, len);
      }
      flush();
      ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret < 0)
        return -1;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int downloadFpgaFromFlash(byte flashSelector, byte fpgaSelector) {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.DownloadFpgaFromFlash.getCode());
      write(flashSelector);
      write(fpgaSelector);
      flush();
      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return -1;
    }

    return 0;
  }

  public byte getDBStatus() {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.GetDBStatus.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return -1;
      }

      ret = read(this.buf, 0, 1);
      db = this.buf[0];
    }
    catch (IOException e)
    {
      byte db;
      e.printStackTrace();
      return -1;
    }
    byte db;
    return db;
  }

  public int openUsbByAddr(byte usbPort) {
    if (connect() < 0) {
      return -1;
    }
    try
    {
      write(TYPE.OpenUsbByAddr.getCode());
      write(usbPort);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }

    return 0;
  }

  public int resetUsb()
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.ResetUsb.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int resetCpld() {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.ResetCpld.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int downFpgaFromUsb(byte fpgaSlector, InputStream is, int len)
  {
    if (connect() < 0) {
      return -1;
    }
    try
    {
      write(TYPE.DownloadFpgaFromUsb.getCode());
      write(fpgaSlector);
      write(len);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return ret;
      }

      while ((len = is.read(this.buf)) > 0)
        write(this.buf, 0, len);
      flush();

      ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return ret;
      }

    }
    catch (IOException e)
    {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int resetExam() {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.ResetExam.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int getDeviceInfo() {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.GetDeviceInfo.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return -1;
      }

      ret = read(this.buf, 1, 2);
      if (ret != 2) {
        return -1;
      }

      this.device.setLocation("Box:" + byteToInt(this.buf[2]) + "Device:" + 
        byteToInt(this.buf[1]));
      System.out.println(this.device.getLocation());
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int setDeviceInfo()
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.SetDeviceInfo.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int setDBStatus(byte dbStatus) {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.SetDBStatus.getCode());
      write(dbStatus);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int setDataBus(int dataBusmask, int dataBus) {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.SetDataBus.getCode());
      write(dataBusmask);
      write(dataBus);
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
    return 0;
  }

  public int[] getRegs()
  {
    if (connect() < 0) {
      return null;
    }

    int[] regs = new int[256];
    try {
      write(TYPE.GetRegs.getCode());
      flush();

      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0) {
        return null;
      }

      read(this.buf, 0, 1024);
      for (int i = 0; i < 256; i++)
        regs[i] = 
          (byteToInt(this.buf[(i * 4 + 0)]) + (
          byteToInt(this.buf[(i * 4 + 1)]) << 8) + (
          byteToInt(this.buf[(i * 4 + 2)]) << 16) + (
          byteToInt(this.buf[(i * 4 + 3)]) << 24));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }

    return regs;
  }

  public int sendCycles(int clock)
  {
    if (connect() < 0)
      return -1;
    try
    {
      write(TYPE.SendCycles.getCode());
      write(clock);
      flush();
      int ret = read(this.buf, 0, 1);
      ret = this.buf[0];
      if (ret != 0)
        return -1;
    }
    catch (IOException e) {
      e.printStackTrace();
      return -1;
    }

    return 0;
  }

  public int connect()
  {
    try {
      this.socket = new Socket(this.device.getIp(), this.device.getTcpPort());

      this.out = new BufferedOutputStream(this.socket.getOutputStream());
      this.in = new BufferedInputStream(this.socket.getInputStream());
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
    this.device.setLastOpertaionTime(System.currentTimeMillis());
    return 0;
  }

  public int disconnect() {
    try {
      this.out.close();
      this.in.close();
      this.socket.close();
      this.out = null;
      this.in = null;
      this.socket = null;
    } catch (Exception e) {
      return -1;
    }
    return 0;
  }

  public int sysInit()
  {
    int ret = openUsbByAddr((byte)5);
    if (ret < 0) {
      return -1;
    }

    ret = resetUsb();
    if (ret < 0) {
      return -1;
    }

    if (userInit() < 0) {
      return -1;
    }

    ret = getDeviceInfo();
    if (ret < 0) {
      return -1;
    }

    return 0;
  }

  public int userInit()
  {
    int ret = downloadFpgaFromFlash((byte)0, (byte)0);
    if (ret < 0) {
      return -1;
    }

    return 0;
  }

  public int update() {
    int ret = getDeviceInfo();
    return ret;
  }

  public void write(byte b) throws IOException {
    this.buf[0] = b;
    this.out.write(this.buf, 0, 1);
  }
  public void write(int i) throws IOException {
    this.buf[0] = ((byte)i);
    this.buf[1] = ((byte)(i >> 8));
    this.buf[2] = ((byte)(i >> 16));
    this.buf[3] = ((byte)(i >> 24));
    this.out.write(this.buf, 0, 4);
  }
  public void write(byte[] buf, int offSet, int len) throws IOException {
    this.out.write(buf, offSet, len);
  }
  public void flush() throws IOException {
    this.out.flush();
  }
  public int read(byte[] buf, int offSet, int len) throws IOException {
    return this.in.read(buf, offSet, len);
  }

  private int byteToInt(byte b)
  {
    return (b + 256) % 256;
  }

  public TYPE getType() {
    return this.type;
  }

  public void setType(TYPE type) {
    this.type = type;
  }

  public static enum TYPE
  {
    ResetExam(63), ResetCpld(58), OpenUsbByAddr(55), ResetUsb(57), WriteRegs(229), GetRegs(226), 
    GetDBStatus(53), SetDBStatus(224), SetDataBus(225), SendCycles(227), FlipCycles(228), 
    WriteFpgaToFlash(50), DownloadFpgaFromFlash(52), DownloadFpgaFromUsb(60), WriteRam(48), 
    ReadRam(49), GetDeviceInfo(64), SetDeviceInfo(65), ExprimentType(230);

    private final int code;

    private TYPE(int code) {
      this.code = code;
    }

    public byte getCode() {
      return (byte)this.code;
    }
  }
}