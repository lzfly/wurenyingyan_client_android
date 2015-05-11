package com.fiveman.yingyan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPConfig {

	private static SharedPreferences mSettings = null;
	
	public static SharedPreferences getConfig()
	{
		if (mSettings == null)
		{
			String cfgFileName = GlobalContext.getInstance().getPackageName();
			mSettings = GlobalContext.getInstance().getSharedPreferences(cfgFileName, Context.MODE_PRIVATE);
		}
		return mSettings;
	}

	public static void setProperty(String name, boolean value)
	{
		SharedPreferences sp = getConfig();
		sp.edit().putBoolean(name, value).commit();
	}
	
	public static void setProperty(String name, int value)
	{
		SharedPreferences sp = getConfig();
		sp.edit().putInt(name, value).commit();
	}
	
	public static void setProperty(String name, long value)
	{
		SharedPreferences sp = getConfig();
		sp.edit().putLong(name, value).commit();
	}
	
	public static void setPropery(String name, String value)
	{
		SharedPreferences sp = getConfig();
		sp.edit().putString(name, value).commit();
	}
	
	public static boolean getPropertyAsBoolean(String name, boolean defValue)
	{
		SharedPreferences sp = getConfig();
		return sp.getBoolean(name, defValue);
	}

	public static int getPropertyAsInt(String name, int defValue)
	{
		SharedPreferences sp = getConfig();
		return sp.getInt(name, defValue);
	}

	public static long getPropertyAsLong(String name, long defValue)
	{
		SharedPreferences sp = getConfig();
		return sp.getLong(name, defValue);
	}
	
	public static String getPropertyAsString(String name, String defValue)
	{
		SharedPreferences sp = getConfig();
		return sp.getString(name, defValue);
	}
	
}
