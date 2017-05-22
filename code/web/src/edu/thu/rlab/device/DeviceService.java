package edu.thu.rlab.device;

import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract interface DeviceService
{
  public abstract void init(String paramString);

  public abstract JSONArray listAll();

  public abstract JSONObject listOne(String paramString);

  public abstract int connect(String paramString);

  public abstract void disconnect(String paramString);

  public abstract int sendRbf(String paramString1, InputStream paramInputStream, String paramString2);

  public abstract boolean setInput(String paramString1, byte[] paramArrayOfByte, String paramString2);

  public abstract byte[] setSRAMInput(String paramString, int paramInt1, int paramInt2, byte[] paramArrayOfByte);

  public abstract byte[] getResult(String paramString1, String paramString2);
}