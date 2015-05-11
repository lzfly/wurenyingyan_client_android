package com.fiveman.yingyan;

import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ToastUtils;
import com.fiveman.yingyan.widgets.MessageDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SensorSettingActivity extends BaseActivity implements Callback {

	private TextView m_SensorName;
	private TextView m_BindCamera;
	
	private String m_CurrSensorAddr;
	
	private Handler m_Handler = new Handler(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_sensor_setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_about);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.sensor_property_setting);
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_back);
		backAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityUtils.switchActivity(MainActivity.class, true, 0, "");
			}
			
		});
		
		Button renameButton = (Button)findViewById(R.id.rename_sensor_button);
		renameButton.setOnClickListener(renameClickListener);
		
		Button bindCameraButton = (Button)findViewById(R.id.bind_camera_button);
		bindCameraButton.setOnClickListener(bindCameraClickListener);
		
		m_SensorName = (TextView)findViewById(R.id.sensor_name);
		
		m_BindCamera = (TextView)findViewById(R.id.bind_camera_name);
		m_BindCamera.setOnClickListener(unbindCameraClickListener);
		
		Intent intent = this.getIntent();
		m_CurrSensorAddr = intent.getStringExtra("arg0");

		initSensorProperty();
	}
	
	View.OnClickListener renameClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DeviceInfo device = DeviceList.getDevice(m_CurrSensorAddr);
			if (device != null)
			{
				ActivityUtils.switchActivity(RenameDeviceActivity.class, false, 
						200, device.getIEEE(), device.getName());
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_rename_sensor_name_failed);
			}
		}
	};
	
	View.OnClickListener unbindCameraClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Object tag = m_BindCamera.getTag();
			if (tag != null)
			{
				MessageDialog.Builder builder = new MessageDialog.Builder(thisObj);
				builder.setCaption(R.string.message_dialog_caption);
				builder.setMessage(String.format(thisObj.getString(R.string.hint_unbind_camera), m_BindCamera.getText()));
				builder.setOkButton(thisObj.getString(R.string.message_dialog_button_ok), 
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						HttpUtils.unbindCamera(m_CurrSensorAddr, tag.toString(), new HttpUtils.HttpResponseListener() {
							
							@Override
							public void onStart() {
							}
							
							@Override
							public void onDone(boolean succ, String result) {
								if (succ)
								{
									ToastUtils.show(thisObj, R.string.hint_unbind_camera_succ);

									m_Handler.sendEmptyMessage(0);
									
									HttpUtils.S_BIND_CAMERAS.remove(m_CurrSensorAddr);
								}
								else
								{
									ToastUtils.show(thisObj, R.string.hint_unbind_camera_failed);
								}
							}
						});
						
						dialog.dismiss();
					}
	
				});
				builder.setCancelButton(thisObj.getString(R.string.message_dialog_button_cancel), null);
				builder.create().show();
			}
		}
	};
	
	View.OnClickListener bindCameraClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DeviceInfo sensor = DeviceList.getDevice(m_CurrSensorAddr);
			if (sensor != null)
			{
				ActivityUtils.switchActivity(CameraSelectorActivity.class, false, 200, "");
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
			}
		}
	};
	
	private void initSensorProperty()
	{
		if (m_CurrSensorAddr != null)
		{
			DeviceInfo device = DeviceList.getDevice(m_CurrSensorAddr);
			if (device != null)
			{
				m_SensorName.setText(device.getName());
			}
			
			HttpUtils.getBindCamera(m_CurrSensorAddr, new HttpUtils.HttpResponseListener() {
				
				@Override
				public void onStart() {
				}
				
				@Override
				public void onDone(boolean succ, String result) {
					if (succ)
					{
						String addr = HttpUtils.S_BIND_CAMERAS.get(m_CurrSensorAddr);
						if (addr != null && !TextUtils.isEmpty(addr))
						{
							DeviceInfo camera = DeviceList.getDevice(addr);
							if (camera != null)
							{
								m_BindCamera.setText(camera.getName());
								m_BindCamera.setTag(camera.getIEEE());
							}
							else
							{
								m_BindCamera.setText(R.string.no_camera_bind_lab);
								m_BindCamera.setTag(null);
							}
						}
						else
						{
							m_BindCamera.setText(R.string.no_camera_bind_lab);
							m_BindCamera.setTag(null);
						}
					}
					else
					{
						m_BindCamera.setText(R.string.no_camera_bind_lab);
						m_BindCamera.setTag(null);
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == ConstUtils.S_RENAME_SENSOR_ACTIVITY_RESULT_CODE)
		{
			String sensorAddr = data.getStringExtra("sensor_addr");
			String sensorName = data.getStringExtra("sensor_name");
			
			m_SensorName.setText(sensorName);
			
			DeviceActivity devicePage = GlobalContext.S_DEVICE_VIEW;
			if  (devicePage != null)
			{
				devicePage.updateSensorViewName(sensorAddr, sensorName);
			}			
		}
		else if (resultCode == ConstUtils.S_SElECT_CAMERA_ACTIVITY_RESULT_CODE)
		{
			final String cameraAddr = data.getStringExtra("camera_addr");

			Object tag = m_BindCamera.getTag();
			if (tag == null)
			{
				HttpUtils.bindCamera(m_CurrSensorAddr, cameraAddr, new HttpUtils.HttpResponseListener() {
					
					@Override
					public void onStart() {
					}
					
					@Override
					public void onDone(boolean succ, String result) {
						if (succ)
						{
							DeviceInfo camera = DeviceList.getDevice(cameraAddr);
							if (camera != null)
							{
								Message msg = new Message();
								msg.what = 1;
								msg.getData().putString("camera_name", camera.getName());
								msg.getData().putString("camera_sn", cameraAddr);
								m_Handler.sendMessage(msg);
								
								HttpUtils.S_BIND_CAMERAS.put(m_CurrSensorAddr, cameraAddr);
								
								ToastUtils.show(thisObj, R.string.hint_bind_camera_succ);
							}
							else
							{
								ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
							}
						}
						else
						{
							ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
						}
					}
				});
			}
			else
			{
				if (!cameraAddr.equals(tag))
				{
					HttpUtils.unbindCamera(m_CurrSensorAddr, tag.toString(), new HttpUtils.HttpResponseListener() {
						
						@Override
						public void onStart() {
						}
						
						@Override
						public void onDone(boolean succ, String result) {
							if (succ)
							{
								HttpUtils.S_BIND_CAMERAS.remove(m_CurrSensorAddr);
								
								HttpUtils.bindCamera(m_CurrSensorAddr, cameraAddr, new HttpUtils.HttpResponseListener() {
									
									@Override
									public void onStart() {
									}
									
									@Override
									public void onDone(boolean succ, String result) {
										if (succ)
										{
											DeviceInfo camera = DeviceList.getDevice(cameraAddr);
											if (camera != null)
											{
												Message msg = new Message();
												msg.what = 1;
												msg.getData().putString("camera_name", camera.getName());
												msg.getData().putString("camera_sn", cameraAddr);
												m_Handler.sendMessage(msg);
												
												HttpUtils.S_BIND_CAMERAS.put(m_CurrSensorAddr, cameraAddr);
												
												ToastUtils.show(thisObj, R.string.hint_bind_camera_succ);
											}
											else
											{
												ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
											}
										}
										else
										{
											ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
										}
									}
								});
							}
							else
							{
								ToastUtils.show(thisObj, R.string.hint_bind_camera_failed);
							}
						}
					});
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_bind_camera_succ);
				}
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "SensorSettingActivity");
		AppNotification.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SPConfig.setPropery("last_page", "");
		AppNotification.cancel();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			ActivityUtils.switchActivity(MainActivity.class, true, 0, "");
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean handleMessage(Message arg0) {
		if (arg0.what == 0)
		{
			m_BindCamera.setText(R.string.no_camera_bind_lab);
			m_BindCamera.setTag(null);
		}
		else if (arg0.what == 1)
		{
			String cameraName = arg0.getData().getString("camera_name");
			String cameraSN = arg0.getData().getString("camera_sn");
			m_BindCamera.setText(cameraName);
			m_BindCamera.setTag(cameraSN);
		}
		return false;
	}

}
