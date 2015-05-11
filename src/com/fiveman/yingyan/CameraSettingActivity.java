package com.fiveman.yingyan;

import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ToastUtils;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraSettingActivity extends BaseActivity {

	private TextView m_CameraName;
	private TextView m_CameraAddr;
	private TextView m_CameraPort;
	
	private String m_CurrSensorAddr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_camera_setting);
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
		
		Button mdyAddrButton = (Button)findViewById(R.id.camera_addr_button);
		mdyAddrButton.setOnClickListener(modifyAddrClickListener);
		
		Button mdyPortButton = (Button)findViewById(R.id.camera_port_button);
		mdyPortButton.setOnClickListener(modifyPortClickListener);
		
		m_CameraName = (TextView)findViewById(R.id.sensor_name);
		m_CameraAddr = (TextView)findViewById(R.id.camera_addr_text);
		m_CameraPort = (TextView)findViewById(R.id.camera_port_text);
		
		Intent intent = this.getIntent();
		m_CurrSensorAddr = intent.getStringExtra("arg0");

		initCameraProperty();
	}
	
	private void initCameraProperty()
	{
		if (m_CurrSensorAddr != null)
		{
			DeviceInfo device = DeviceList.getDevice(m_CurrSensorAddr);
			if (device != null)
			{
				m_CameraName.setText(device.getName());
				m_CameraAddr.setText(device.getCameraIP());
				m_CameraPort.setText(device.getCameraPort());
			}
		}		
	}
	
	View.OnClickListener modifyAddrClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DeviceInfo device = DeviceList.getDevice(m_CurrSensorAddr);
			if (device != null)
			{
				ActivityUtils.switchActivity(ModifyCameraAddrActivity.class, false, 
						200, device.getIEEE(), device.getCameraIP());
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_modify_camera_addr_failed);
			}
		}
	};
	
	View.OnClickListener modifyPortClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			DeviceInfo device = DeviceList.getDevice(m_CurrSensorAddr);
			if (device != null)
			{
				ActivityUtils.switchActivity(ModifyCameraPortActivity.class, false, 
						200, device.getIEEE(), device.getCameraPort());
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_modify_camera_port_failed);
			}
		}
	};
	
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == ConstUtils.S_RENAME_SENSOR_ACTIVITY_RESULT_CODE)
		{
			String sensorAddr = data.getStringExtra("sensor_addr");
			String sensorName = data.getStringExtra("sensor_name");
			
			m_CameraName.setText(sensorName);
			
			DeviceActivity devicePage = GlobalContext.S_DEVICE_VIEW;
			if  (devicePage != null)
			{
				devicePage.updateSensorViewName(sensorAddr, sensorName);
			}
		}
		else if (resultCode == ConstUtils.S_MODIFY_CAMERA_ADDR_ACTIVITY_RESULT_CODE)
		{
			String cameraIP = data.getStringExtra("camera_ip");
			m_CameraAddr.setText(cameraIP);
		}
		else if (resultCode == ConstUtils.S_MODIFY_CAMERA_PORT_ACTIVITY_RESULT_CODE)
		{
			String cameraPort = data.getStringExtra("camera_port");
			m_CameraPort.setText(cameraPort);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "CameraSettingActivity");
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

}
