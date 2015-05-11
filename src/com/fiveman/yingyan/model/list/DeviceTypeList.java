package com.fiveman.yingyan.model.list;

import java.util.Hashtable;

import android.text.TextUtils;

import com.fiveman.yingyan.model.DeviceTypeInfo;

public class DeviceTypeList {

	private static Hashtable<String, DeviceTypeInfo> S_DEVICE_TYPES = new Hashtable<String, DeviceTypeInfo>();
	
	private static Object S_LOCK = new Object();

	public static void clear()
	{
		synchronized (S_LOCK)
		{
			S_DEVICE_TYPES.clear();
		}
	}
	
	public static boolean exists(String key)
	{
		if (key != null && !TextUtils.isEmpty(key))
		{
			return S_DEVICE_TYPES.containsKey(key);
		}
		return false;
	}
	
	public static boolean exists(DeviceTypeInfo deviceType)
	{
		return exists(deviceType.getCode());
	}

	public static boolean add(DeviceTypeInfo deviceType)
	{
		if (!S_DEVICE_TYPES.containsKey(deviceType.getCode()))
		{
			synchronized (S_LOCK)
			{
				if (!S_DEVICE_TYPES.containsKey(deviceType.getCode()))
				{
					S_DEVICE_TYPES.put(deviceType.getCode(), deviceType);
					return true;
				}
			}
		}
		return false;
	}
	
	public static void put(DeviceTypeInfo deviceType)
	{
		synchronized (S_LOCK)
		{
			S_DEVICE_TYPES.put(deviceType.getCode(), deviceType);
		}
	}
	
	public static void remove(DeviceTypeInfo deviceType)
	{
		synchronized (S_LOCK)
		{
			S_DEVICE_TYPES.remove(deviceType.getCode());
		}
	}
	
	public static DeviceTypeInfo getDeviceType(String code)
	{
		if (exists(code))
		{
			return S_DEVICE_TYPES.get(code);
		}
		return null;
	}
	
	public static String getDeviceTypeName(String code)
	{
		DeviceTypeInfo devType = getDeviceType(code);
		if (devType != null)
		{
			return devType.getName();
		}
		return null;
	}

}
