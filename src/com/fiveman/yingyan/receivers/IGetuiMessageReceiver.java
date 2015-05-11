package com.fiveman.yingyan.receivers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.DeviceTypeInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.model.list.DeviceTypeList;
import com.fiveman.yingyan.notifications.AlarmNotification;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.SoundUtils;
import com.igexin.sdk.PushConsts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class IGetuiMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle != null && bundle.getInt(PushConsts.CMD_ACTION) == PushConsts.GET_MSG_DATA)
		{
			byte[] payload = bundle.getByteArray("payload");
			if (payload != null)
			{
				String data = new String(payload);
				byte[] byteMsg = Base64.decodeFast(data);
				try {
					String msg = new String(byteMsg, "UTF-8");
					Log.d("wxm", "透视数据：" + msg);

					JSONObject msgObj = JSON.parseObject(msg);
					if (msgObj != null)
					{
						String actionType = msgObj.getString("actionType");
						
						Log.d("wxm", "消息类型：" + msgObj.getString("actionType"));
						
						if ("notice_new".equalsIgnoreCase(actionType))
						{
							JSONObject noticeObj = msgObj.getJSONObject("noticeInfo");
							String devSN = noticeObj.getString("DEVICE_SN");

							if (DeviceList.exists(devSN))
							{
								JSONObject noticeMsgObj = noticeObj.getJSONObject("MESSAGE");
								
								DeviceInfo devInfo = DeviceList.getDevice(devSN);
								DeviceTypeInfo devTypeInfo = DeviceTypeList.getDeviceType(devInfo.getDeviceType());
								String devTypeType = devTypeInfo.getType();

								if (!GlobalContext.isBackground())
								{
									if (GlobalContext.S_DEVICE_VIEW != null)
									{
										GlobalContext.S_DEVICE_VIEW.onlineDevice(devSN);
									}
									
									if (GlobalContext.S_MESSAGE_VIEW != null)
									{
										GlobalContext.S_MESSAGE_VIEW.receivedNotice(noticeObj);
									}
								}
								
								if ("alarm".equals(devTypeType))
								{
									boolean alarm = noticeMsgObj.getBooleanValue("alarm");
									if (alarm)
									{
										if (!GlobalContext.isBackground())
										{
											if (GlobalContext.S_DEVICE_VIEW != null)
											{
												GlobalContext.S_DEVICE_VIEW.triggerAlarm(devSN);
											}
											
											SoundUtils.playRingtone();
											
											Log.d("wxm", "app is forground");
										}
										else
										{
											AlarmNotification.show();
											Log.d("wxm", "app is background!!!!");
										}
									}
								}
							}
							else if (GlobalContext.isBackground())
							{
								AlarmNotification.show();
								Log.d("wxm", "app is background -- 222");
							}
						}
						else if ("device_sync".equalsIgnoreCase(actionType))
						{
							JSONObject devObj = msgObj.getJSONObject("deviceInfo");

							DeviceInfo devInfo = new DeviceInfo();
							devInfo.setDeviceType(devObj.getString("TYPE_CODE"));
							devInfo.setIEEE(devObj.getString("SN"));
							devInfo.setName(devObj.getString("NAME"));
							devInfo.setOpen("1".equals(devObj.getString("IS_OPEN")));
							devInfo.setOnline("1".equals(devObj.getString("IS_ONLINE")));
							DeviceList.put(devInfo);
							
							if (GlobalContext.S_DEVICE_VIEW != null)
							{
								GlobalContext.S_DEVICE_VIEW.updateDevice(devInfo);
							}
						}
						else if ("camera_sync".equalsIgnoreCase(actionType))
						{
							JSONObject cameraObj = msgObj.getJSONObject("cameraInfo");
							
							DeviceInfo cameraInfo = new DeviceInfo();
							cameraInfo.setIEEE(cameraObj.getString("SN"));
							cameraInfo.setDeviceType(ConstUtils.S_CAMERA_TYPE_CODE);
							cameraInfo.setSmartcenterSN(cameraObj.getString("SMARTCENTER_SN"));
							cameraInfo.setName(cameraObj.getString("NAME"));
							cameraInfo.setOpen(true);
							cameraInfo.setOnline(true);
							DeviceList.put(cameraInfo);
							
							if (GlobalContext.S_DEVICE_VIEW != null)
							{
								GlobalContext.S_DEVICE_VIEW.updateDevice(cameraInfo);
							}
						}
						else if ("camera_screenshot_reply".equalsIgnoreCase(actionType))
						{
							if (GlobalContext.S_CAMERA_PICTURE_VIEW != null)
							{
								JSONObject cameraObj = msgObj.getJSONObject("cameraInfo");
								String userName = cameraObj.getString("from");
								if (GlobalContext.S_LOGIN_NAME != null && 
										GlobalContext.S_LOGIN_NAME.equals(userName))
								{
									String cameraSN = cameraObj.getString("cameraSN");
									String imgUrl = cameraObj.getString("image");
									GlobalContext.S_CAMERA_PICTURE_VIEW.updateCameraImage(cameraSN, imgUrl);
								}
							}
						}
					}
					
				} catch (Exception e) {
				}
			}
		}
	}

}
