package com.fiveman.yingyan.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.DeviceTypeInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.model.list.DeviceTypeList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

public class HttpUtils {
	
	public interface HttpResponseListener
	{
		void onStart();
		void onDone(boolean succ, String result);
	}
	
	public static int S_SUCC_CODE = 10000;
	
	public static void reset()
	{
		S_BIND_CAMERAS.clear();
		S_CAMERAS.clear();
	}
	
	private static FinalHttp getFinalHttp()
	{
		FinalHttp fh = new FinalHttp();
		fh.configRequestExecutionRetryCount(ConstUtils.S_HTTP_REQUEST_RETRY_COUNT);
		fh.configTimeout(ConstUtils.S_HTTP_REQUEST_TIMEOUT);
		return fh;
	}
	
	public static void get5Lianpai(final String zipFile, final HttpResponseListener callback)
	{
		File file = new File(ConstUtils.G_IMAGE_PATH);
		if (!file.exists())
		{
			file.mkdirs();
		}
		
		final String target = ConstUtils.G_IMAGE_PATH + zipFile;
		File file2 = new File(target);
		if (file2.exists())
		{
			if (callback != null)
			{
				callback.onDone(true, target);
			}
		}
		else
		{
			FinalHttp fh = getFinalHttp();
			String url = ConstUtils.S_GET_5LIANPAI_URL + "/" + zipFile;
			fh.download(url, target, new AjaxCallBack<File>() {
	
				@Override
				public void onStart() {
					super.onStart();
					
					if (callback != null)
					{
						callback.onStart();
					}
				}
	
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					
					if (callback != null)
					{
						callback.onDone(false, null);
					}
				}
	
				@Override
				public void onSuccess(File t) {
					super.onSuccess(t);
	
					if (callback != null)
					{
						callback.onDone(t != null, target);
					}
				}
				
			});
		}
	}
	
	public static void userInit(final HttpResponseListener callback)
	{
		String url = ConstUtils.S_USER_INIT_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		AjaxParams params = new AjaxParams();
		params.put("name", GlobalContext.S_LOGIN_NAME);
		params.put("key", "PUSH_CLIENTID");
		params.put("val", GlobalContext.getInstance().getPushClientId());
		
		FinalHttp fh = getFinalHttp();
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void changePassword(String newPass, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("name", GlobalContext.S_LOGIN_NAME);
		params.put("key", "PASS");
		params.put("val", newPass);
		
		String url = ConstUtils.S_CHANGE_PASSWORD_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if  (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	//µÇÂ¼·þÎñÆ÷
	public static void login(final String name, final String pass, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();

		AjaxParams params = new AjaxParams();
		params.put("name", name);
		params.put("pass", pass);
		
		fh.post(ConstUtils.S_LOGIN_URL, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				GlobalContext.S_LOGINED = false;
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = loginObj.getJSONObject("result");
						if (resultObj != null)
						{
							GlobalContext.S_LOGIN_NAME = name;
							GlobalContext.S_LOGIN_PASS = pass;
							
							GlobalContext.S_LOGIN_SESSION = resultObj.getString("Session");
							GlobalContext.S_LOGINED = true;
						}
					}
				}
				catch (Exception exp)
				{
					GlobalContext.S_LOGINED = false;
				}
				
				if (callback != null)
				{
					callback.onDone(GlobalContext.S_LOGINED, null);
				}
			}

		});
		
	}

	public static void getDeviceTypes(final HttpResponseListener callback)
	{
		String url = ConstUtils.S_GET_DEVICE_TYPE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		fh.get(url, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = respObj.getJSONObject("result");
						if (resultObj != null)
						{
							JSONArray arrDevTypes = resultObj.getJSONArray("deviceType.list");
							if (arrDevTypes != null && arrDevTypes.size() > 0)
							{
								for (int i = 0; i < arrDevTypes.size(); i++)
								{
									JSONObject devTypeObj = arrDevTypes.getJSONObject(i);
									if (devTypeObj != null)
									{
										DeviceTypeInfo devType = new DeviceTypeInfo();
										devType.setCode(devTypeObj.getString("CODE"));
										devType.setName(devTypeObj.getString("NAME"));
										devType.setIcon(devTypeObj.getString("ICON"));
										devType.setType(devTypeObj.getString("TYPE"));
										DeviceTypeList.put(devType);
									}
								}
							}
							
							succ = true;
						}
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void readNotice(String noticeCode, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("notice_code", noticeCode);
		
		String url = ConstUtils.S_READ_NOTICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void getNoticeList(long maxId, long count, String type, String status, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("start_id", String.valueOf(maxId));
		params.put("count", String.valueOf(count));
		params.put("type", type);
		params.put("status", status);
		
		String url = ConstUtils.S_GET_NOTICE_LIST_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);

				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, t);
				}
			}
			
		});
	}
	
	public static void getUnreadNoticeCount(final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		String url = ConstUtils.S_GET_UNREAD_NOTICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				String count = "0";
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						JSONObject resultObj = respObj.getJSONObject("result");
						if (resultObj != null)
						{
							count = resultObj.getString("count");
							succ = true;
						}
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, count);
				}
			}
			
		});
	}
	
	public static void bindCamera(final String devSN, final String cameraSN, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("camera_sn", cameraSN);
		
		String url = ConstUtils.S_BIND_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, "");
				}
			}
			
		});
	}

	public static void unbindCamera(final String devSN, final String cameraSN, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("camera_sn", cameraSN);
		
		String url = ConstUtils.S_UNBIND_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new  AjaxCallBack<String>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, "");
				}
			}

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, "");
				}
			}
			
		});			
	}
	
	public static Hashtable<String, String> S_BIND_CAMERAS = new Hashtable<String, String>();
	public static void getBindCamera(final String deviceSN, final HttpResponseListener callback)
	{
		if (S_BIND_CAMERAS.containsKey(deviceSN))
		{
			if (callback != null)
			{
				callback.onDone(true, null);
			}
		}
		else
		{
			String url = ConstUtils.S_GET_BIND_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
			
			FinalHttp fh = getFinalHttp();
			
			AjaxParams params = new AjaxParams();
			params.put("device_sn", deviceSN);
			
			fh.post(url, params, new AjaxCallBack<String>() {
	
				@Override
				public void onStart() {
					super.onStart();
					
					if (callback != null)
					{
						callback.onStart();
					}
				}
	
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					
					if (callback != null)
					{
						callback.onDone(false, null);
					}
				}
	
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					
					boolean succ = false;
					try
					{
						JSONObject respObj = JSON.parseObject(t);
						int code = respObj.getIntValue("code");
						if (code == S_SUCC_CODE)
						{
							JSONObject resultObj = respObj.getJSONObject("result");
							if (resultObj != null)
							{
								JSONObject devBindCameraObj = resultObj.getJSONObject("deviceBindCamera");
								if (devBindCameraObj != null)
								{
									String devSN = devBindCameraObj.getString("DEVICE_SN");
									if (deviceSN.equals(devSN))
									{
										String cameraSN = devBindCameraObj.getString("CAMERA_SN");
										if (DeviceList.exists(cameraSN))
										{
											S_BIND_CAMERAS.put(devSN, cameraSN);
											succ = true;
										}
									}
								}
							}
						}
					}
					catch (Exception exp)
					{
					}
					
					if (callback != null)
					{
						callback.onDone(succ, null);
					}
				}
				
			});
		}
	}
	
	public static List<DeviceInfo> S_CAMERAS = new ArrayList<DeviceInfo>();
	public static void getCameras(final HttpResponseListener callback)
	{
		if (!GlobalContext.S_CAMERA_LOADED && S_CAMERAS.size() == 0)
		{
			String url = ConstUtils.S_GET_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
			
			FinalHttp fh = getFinalHttp();
			fh.post(url, new AjaxCallBack<String>() {

				@Override
				public void onStart() {
					super.onStart();
					
					if (callback != null)
					{
						callback.onStart();
					}
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					
					if (callback != null)
					{
						callback.onDone(false, null);
					}
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					
					boolean succ = false;
					try
					{
						JSONObject respObj = JSON.parseObject(t);
						int code = respObj.getIntValue("code");
						if (code == S_SUCC_CODE)
						{
							JSONObject resultObj = respObj.getJSONObject("result");
							if (resultObj != null)
							{
								JSONArray arrCamera = resultObj.getJSONArray("camera.list");
								if (arrCamera != null && arrCamera.size() > 0)
								{
									for (int i = 0; i < arrCamera.size(); i++)
									{
										JSONObject cameraObj = arrCamera.getJSONObject(i);
										if (cameraObj != null)
										{
											DeviceInfo device = new DeviceInfo();
											device.setIEEE(cameraObj.getString("SN"));
											device.setDeviceType(ConstUtils.S_CAMERA_TYPE_CODE);
											device.setSmartcenterSN(cameraObj.getString("SMARTCENTER_SN"));
											device.setName(cameraObj.getString("NAME"));
											device.setCameraIP(cameraObj.getString("IP"));
											device.setCameraPort(cameraObj.getString("PORT"));
											device.setOpen(true);
											device.setOnline(true);
											DeviceList.put(device);
											
											S_CAMERAS.add(device);
										}
									}
								}
								succ = true;
								GlobalContext.S_CAMERA_LOADED = true;
							}
						}
					}
					catch (Exception exp)
					{
					}
					
					if (callback != null)
					{
						callback.onDone(succ, null);
					}
				}
				
			});
		}
		else
		{
			if (callback != null)
			{
				callback.onDone(true, null);
			}
		}
	}
	
	public static void openDevice(String devSN, final HttpResponseListener callback)
	{
		String url = ConstUtils.S_CLOSE_DEVICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("is_open", "1");
		
		fh.post(url, params, new AjaxCallBack<String>() {
			
			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void closeDevice(String devSN, final HttpResponseListener callback)
	{
		String url = ConstUtils.S_CLOSE_DEVICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("is_open", "0");
		
		fh.post(url, params, new AjaxCallBack<String>() {
			
			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void updateCameraName(String cameraSN, String name, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("camera_sn", cameraSN);
		params.put("key", "NAME");
		params.put("val", name);
		
		String url = ConstUtils.S_UPDATE_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void updateCameraPort(String cameraSN, String port, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("camera_sn", cameraSN);
		params.put("key", "PORT");
		params.put("val", port);
		
		String url = ConstUtils.S_UPDATE_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void updateCameraIP(String cameraSN, String ip, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("camera_sn", cameraSN);
		params.put("key", "IP");
		params.put("val", ip);
		
		String url = ConstUtils.S_UPDATE_CAMERA_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void getCameraScreenshot(String cameraSN, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("camera_sn", cameraSN);
		
		String url = ConstUtils.S_GET_CAMERA_SCREENSHOT_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});
	}
	
	public static void updateDeviceName(String devSN, String name, final HttpResponseListener callback)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("device_sn", devSN);
		params.put("key", "NAME");
		params.put("val", name);
		
		String url = ConstUtils.S_UPDATE_DEVICE_NAME_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
		fh.post(url, params, new  AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				
				if (callback != null)
				{
					callback.onStart();
				}
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (callback != null)
				{
					callback.onDone(false, null);
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				boolean succ = false;
				try
				{
					JSONObject loginObj = JSON.parseObject(t);
					int code = loginObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						succ = true;
					}
				}
				catch (Exception exp)
				{
				}
				
				if (callback != null)
				{
					callback.onDone(succ, null);
				}
			}
			
		});		
	}
	
	public static void getDevices(final HttpResponseListener callback)
	{
		if (!GlobalContext.S_DEVICE_LOADED)
		{
			String url = ConstUtils.S_GET_DEVICE_URL + "?sid=" + GlobalContext.S_LOGIN_SESSION;
			
			FinalHttp fh = getFinalHttp();
			
//			AjaxParams params = new AjaxParams();
//			params.put("is_online", "1");
			//params, 
			fh.post(url, new AjaxCallBack<String>() {
	
				@Override
				public void onStart() {
					super.onStart();
					
					if (callback != null)
					{
						callback.onStart();
					}
				}
	
				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					
					if (callback != null)
					{
						callback.onDone(false, null);
					}
				}
	
				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					
					boolean succ = false;
					try
					{
						JSONObject respObj = JSON.parseObject(t);
						int code = respObj.getIntValue("code");
						if (code == S_SUCC_CODE)
						{
							JSONObject resultObj = respObj.getJSONObject("result");
							if (resultObj != null)
							{
								JSONArray arrDevice = resultObj.getJSONArray("device.list");
								if (arrDevice != null && arrDevice.size() > 0)
								{
									for (int i = 0; i < arrDevice.size(); i++)
									{
										JSONObject deviceObj = arrDevice.getJSONObject(i);
										if (deviceObj != null)
										{
											DeviceInfo device = new DeviceInfo();
											device.setIEEE(deviceObj.getString("SN"));
											device.setDeviceType(deviceObj.getString("TYPE_CODE"));
											device.setSmartcenterSN(deviceObj.getString("SMARTCENTER_SN"));
											device.setName(deviceObj.getString("NAME"));
											device.setOpen("1".equals(deviceObj.getString("IS_OPEN")));
											device.setOnline("1".equals(deviceObj.getString("IS_ONLINE")));
											DeviceList.put(device);
										}
									}
								}
								succ = true;
								GlobalContext.S_DEVICE_LOADED = true;
							}
						}
					}
					catch (Exception exp)
					{
					}
					
					if (callback != null)
					{
						callback.onDone(succ, null);
					}
				}
				
			});
		}
		else
		{
			if (callback != null)
			{
				callback.onDone(true, null);
			}
		}
	}
	
	public static void getPassword(String username, HttpResponseListener listener)
	{
		FinalHttp fh = getFinalHttp();
		
		AjaxParams params = new AjaxParams();
		params.put("name", username);
		
		final HttpResponseListener l = listener;
		
		String url = ConstUtils.S_GET_PASSWORD_URL;
		fh.post(url, params, new  AjaxCallBack<String>() {

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				
				if (l != null)
				{
					l.onDone(false, "");
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				
				try
				{
					JSONObject respObj = JSON.parseObject(t);
					int code = respObj.getIntValue("code");
					if (code == S_SUCC_CODE)
					{
						if (l != null)
						{
							l.onDone(true, "");
						}
					}
					else
					{
						if (l != null)
						{
							l.onDone(false, "");
						}
					}
				}
				catch (Exception exp)
				{
					if (l != null)
					{
						l.onDone(false, "");
					}
				}
			}
			
		});
	}
	
}
