package com.fiveman.yingyan.model.list;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.fiveman.yingyan.model.DeviceInfo;

public class DeviceList {

	private static Hashtable<String, DeviceInfo> S_DEVICES = new Hashtable<String, DeviceInfo>();
	
	private static Object S_LOCK = new Object();

	public static void clear()
	{
		synchronized (S_LOCK)
		{
			S_DEVICES.clear();
		}
	}
	
	public static boolean exists(String key)
	{
		return S_DEVICES.containsKey(key);
	}
	
	public static boolean exists(DeviceInfo device)
	{
		return exists(device.getIEEE());
	}

	public static boolean add(DeviceInfo device)
	{
		if (!S_DEVICES.containsKey(device.getIEEE()))
		{
			synchronized (S_LOCK)
			{
				if (!S_DEVICES.containsKey(device.getIEEE()))
				{
					S_DEVICES.put(device.getIEEE(), device);
					return true;
				}
			}
		}
		return false;
	}
	
	public static void put(DeviceInfo device)
	{
		synchronized (S_LOCK)
		{
			S_DEVICES.put(device.getIEEE(), device);
		}
	}
	
	public static void remove(DeviceInfo device)
	{
		synchronized (S_LOCK)
		{
			S_DEVICES.remove(device.getIEEE());
		}
	}
	
	public static DeviceInfo getDevice(String ieee)
	{
		if (exists(ieee))
		{
			return S_DEVICES.get(ieee);
		}
		return null;
	}
	
	public static List<DeviceInfo> getDeviceList(boolean onlyOnline)
	{
		List<DeviceInfo> result = new ArrayList<DeviceInfo>();
		synchronized (S_LOCK)
		{
			if (onlyOnline)
			{
				Iterator<DeviceInfo> i = S_DEVICES.values().iterator();
				while (i.hasNext())
				{
					DeviceInfo devInfo = i.next();
					if (devInfo.isOnline())
					{
						result.add(devInfo);
					}
				}
			}
			else
			{
				result.addAll(S_DEVICES.values());
			}
		}
		return result;
	}
	
}
