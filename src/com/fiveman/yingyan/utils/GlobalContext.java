package com.fiveman.yingyan.utils;

import com.fiveman.yingyan.DeviceActivity;
import com.fiveman.yingyan.MessageActivity;
import com.fiveman.yingyan.PictureBrowserActivity;
import com.igexin.sdk.PushManager;

import android.app.ActivityGroup;
import android.app.Application;

public class GlobalContext extends Application {

	private static GlobalContext S_CONTEXT = null;
	
	public static boolean S_DEVICE_LOADED = false;
	public static boolean S_CAMERA_LOADED = false;
	
	public static MessageActivity S_MESSAGE_VIEW = null;
	public static DeviceActivity S_DEVICE_VIEW = null;
	public static PictureBrowserActivity S_CAMERA_PICTURE_VIEW = null;

	public static String S_LOGIN_NAME = "";
	public static String S_LOGIN_PASS = "";
	public static boolean S_LOGINED = false;
	public static String S_LOGIN_SESSION = null;
	
	public static void reset()
	{
		S_DEVICE_LOADED = false;
		S_CAMERA_LOADED = false;
		
		S_LOGIN_NAME = "";
		S_LOGIN_PASS = "";
		S_LOGINED = false;
		S_LOGIN_SESSION = null;
		
		if (S_MESSAGE_VIEW != null)
		{
			S_MESSAGE_VIEW.reset();
		}
		
		if (S_DEVICE_VIEW != null)
		{
			S_DEVICE_VIEW.reset();
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		S_CONTEXT = this;
		
		//个推初始化
		PushManager.getInstance().initialize(GlobalContext.getInstance().getApplicationContext());
		PushManager.getInstance().turnOnPush(GlobalContext.getInstance().getApplicationContext());
		
		SoundUtils.init();
	}

	public String getPushClientId()
	{
		return PushManager.getInstance().getClientid(this);
	}
	
	public static GlobalContext getInstance()
	{
		return S_CONTEXT;
	}
	
	public static boolean isBackground()
	{
		boolean isBackground = true;
		ActivityGroup mainActivity =  ActivityUtils.getMainActivity();
		if (mainActivity != null)
		{
			isBackground = AppUtils.isBackground(mainActivity);
		}
		return isBackground;
	}
	
}
