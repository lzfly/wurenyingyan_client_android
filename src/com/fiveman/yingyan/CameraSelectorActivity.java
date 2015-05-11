package com.fiveman.yingyan;

import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.list.DeviceTypeIconList;
import com.fiveman.yingyan.model.list.DeviceTypeList;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.widgets.MultiColumnAdapter;
import com.fiveman.yingyan.widgets.MultiColumnView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CameraSelectorActivity extends BaseActivity {

	class CameraAdapter extends MultiColumnAdapter
	{

		@Override
		public View getView(Context context, int index, boolean isInsert) {
			View view = LayoutInflater.from(context).inflate(R.layout.yingyan_view_one_column_camera, null);
			
			Object data = getItem(index);
			if (data != null && data instanceof DeviceInfo)
			{
				DeviceInfo sensor = (DeviceInfo)data;
				if (sensor != null)
				{
					LinearLayout panel = (LinearLayout)view.findViewById(R.id.sensor_panel);
					panel.setTag(sensor.getIEEE());
					panel.setOnClickListener(bindCameraClickListener);
					
					ImageView sensorType = (ImageView)view.findViewById(R.id.sensor_type_image);
					String sType = sensor.getDeviceType();
					int typeIcon = DeviceTypeIconList.getDeviceTypeIcon(sType);
					if (typeIcon != 0)
					{
						sensorType.setImageResource(typeIcon);
					}
					
					String typeName = DeviceTypeList.getDeviceTypeName(sType);
					TextView typeNameView = (TextView)view.findViewById(R.id.device_type_name);
					typeNameView.setText("[" + typeName + "]");
					
					TextView aliasNameView = (TextView)view.findViewById(R.id.device_alias_name);
					aliasNameView.setText(sensor.getName());
				}
			}

			return view;
		}

		@Override
		public View resetView(View view, int index, boolean isInsert) {
			Object data = getItem(index);
			if (data != null && data instanceof DeviceInfo)
			{
				DeviceInfo sensor = (DeviceInfo)data;
				if (sensor != null)
				{
					view.setTag(sensor.getIEEE());
					
					ImageView sensorType = (ImageView)view.findViewById(R.id.sensor_type_image);
					String sType = sensor.getDeviceType();
					int typeIcon = DeviceTypeIconList.getDeviceTypeIcon(sType);
					if (typeIcon != 0)
					{
						sensorType.setImageResource(typeIcon);
					}
					else
					{
						sensorType.setImageResource(R.drawable.yingyan_icon_unknown_sensor);
					}
					
					String typeName = DeviceTypeList.getDeviceTypeName(sType);
					TextView typeNameView = (TextView)view.findViewById(R.id.device_type_name);
					typeNameView.setText("[" + typeName + "]");
					
					TextView aliasNameView = (TextView)view.findViewById(R.id.device_alias_name);
					aliasNameView.setText(sensor.getName());
				}
			}

			return view;
		}
		
	}
	
	View.OnClickListener bindCameraClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			if (tag != null)
			{
				Intent data = new Intent();
				data.putExtra("camera_addr", tag.toString());
				setResult(ConstUtils.S_SElECT_CAMERA_ACTIVITY_RESULT_CODE, data);

				ActivityUtils.finishTop(null);
			}
		}
	};
	
	private ScrollView m_ViewContainer;

	private MultiColumnView m_MultiColumnView;
	private CameraAdapter m_CameraAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_camera_selector);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_about);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.please_select_camera);
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_back);
		backAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityUtils.finishTop(null);
			}
			
		});
		
		m_ViewContainer = (ScrollView)findViewById(R.id.camera_column_views_container);
		
		m_MultiColumnView = (MultiColumnView)findViewById(R.id.camera_column_views);
		m_MultiColumnView.setScrollView(m_ViewContainer);
		
		m_CameraAdapter = new CameraAdapter();
		m_MultiColumnView.setAdapter(m_CameraAdapter);
		
		initCameraList();
	}
	
	private void initCameraList()
	{
		if  (HttpUtils.S_CAMERAS != null)
		{
			m_CameraAdapter.add(HttpUtils.S_CAMERAS.toArray());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "CameraSelectorActivity");
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
			ActivityUtils.finishTop(null);
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
