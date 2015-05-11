package com.fiveman.yingyan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalHttp;

import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.model.list.DeviceTypeIconList;
import com.fiveman.yingyan.model.list.DeviceTypeList;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.ToastUtils;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.widgets.MultiColumnAdapter;
import com.fiveman.yingyan.widgets.MultiColumnView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DeviceActivity extends BaseActivity implements Callback {
	
	class DeviceAdapter extends MultiColumnAdapter
	{

		@Override
		public View getView(Context context, int index, boolean isInsert) {
			View view = LayoutInflater.from(context).inflate(R.layout.yingyan_view_one_column_device, null);
			
			Object data = getItem(index);
			if (data != null && data instanceof DeviceInfo)
			{
				DeviceInfo device = (DeviceInfo)data;
				if (device != null)
				{
					LinearLayout sensorContainer = (LinearLayout)view.findViewById(R.id.sensor_container);
					sensorContainer.setTag(R.string.tag_sensor_info, device);
					sensorContainer.setOnClickListener(sensorContainerOnClickListener);

					ImageView sensorType = (ImageView)view.findViewById(R.id.sensor_type_image);
					String sType = device.getDeviceType();
					int typeIcon = DeviceTypeIconList.getDeviceTypeIcon(sType);
					if (typeIcon != 0)
					{
						sensorType.setImageResource(typeIcon);
					}
					
					String typeName = DeviceTypeList.getDeviceTypeName(sType);
					TextView typeNameView = (TextView)view.findViewById(R.id.device_type_name);
					typeNameView.setText(typeName);
					
					TextView aliasNameView = (TextView)view.findViewById(R.id.device_alias_name);
					aliasNameView.setText(device.getName());
					
					ImageView sensorSwitch = (ImageView)view.findViewById(R.id.device_switch_button);
					sensorSwitch.setTag(R.string.tag_sensor_view, view);
					sensorSwitch.setTag(R.string.tag_sensor_info, device);
					sensorSwitch.setOnClickListener(switchOnOffClickListener);
					
					String devType = device.getDeviceType();
					if (devType == ConstUtils.S_CAMERA_TYPE_CODE || 
							device.isOpen())
					{
						openDevice(view, false);
					}
					
					Button viewCamera = (Button)view.findViewById(R.id.view_camera_button);
					viewCamera.setTag(R.string.tag_sensor_info, device);
					viewCamera.setOnClickListener(viewCameraClickListener);
					
					if (devType == ConstUtils.S_CAMERA_TYPE_CODE)
					{
						sensorSwitch.setVisibility(View.GONE);
						viewCamera.setVisibility(View.VISIBLE);
					}

					if (!device.isOnline())
					{
						View abnorView = view.findViewById(R.id.device_abnor_mark);
						abnorView.setVisibility(View.VISIBLE);
					}
				}
				
				m_DeviceViews.put(device.getIEEE(), view);
			}
			
			return view;
		}

		@Override
		public View resetView(View view, int index, boolean isInsert) {
			Object data = getItem(index);
			if (data != null && data instanceof DeviceInfo)
			{
				DeviceInfo device = (DeviceInfo)data;
				if (device != null)
				{
					LinearLayout sensorContainer = (LinearLayout)view.findViewById(R.id.sensor_container);
					sensorContainer.setTag(R.string.tag_sensor_info, device);
					
					ImageView sensorType = (ImageView)view.findViewById(R.id.sensor_type_image);
					String sType = device.getDeviceType();
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
					typeNameView.setText(typeName);
					
					TextView aliasNameView = (TextView)view.findViewById(R.id.device_alias_name);
					aliasNameView.setText(device.getName());
					
					ImageView sensorSwitch = (ImageView)view.findViewById(R.id.device_switch_button);
					sensorSwitch.setTag(R.string.tag_sensor_info, device);
					
					String devType = device.getDeviceType();
					if (devType == ConstUtils.S_CAMERA_TYPE_CODE || 
							device.isOpen())
					{
						openDevice(view, false);
					}
					else
					{
						closeDevice(view, false);
					}
					
					Button viewCamera = (Button)view.findViewById(R.id.view_camera_button);
					
					if (devType == ConstUtils.S_CAMERA_TYPE_CODE)
					{
						sensorSwitch.setVisibility(View.GONE);
						viewCamera.setVisibility(View.VISIBLE);
					}
					else
					{
						viewCamera.setVisibility(View.GONE);
						sensorSwitch.setVisibility(View.VISIBLE);
					}
					
					View abnorView = view.findViewById(R.id.device_abnor_mark);
					if (!device.isOnline())
					{
						abnorView.setVisibility(View.VISIBLE);
					}
					else
					{
						abnorView.setVisibility(View.GONE);
					}
				}

				m_DeviceViews.put(device.getIEEE(), view);
			}
			
			return view;
		}
		
	}
	
	public void closeDevice(View view, boolean needPost)
	{
		final ImageView switchImg = (ImageView)view.findViewById(R.id.device_switch_button);
		final TextView textState = (TextView)view.findViewById(R.id.device_current_state);
		final DeviceInfo device = (DeviceInfo)switchImg.getTag(R.string.tag_sensor_info);
		if (!needPost)
		{
			switchImg.setSelected(false);
			textState.setTextColor(getResources().getColor(R.color.color_dev_closed));
			textState.setText(R.string.dev_state_closed);
			device.setOpen(false);
		}
		else
		{
			if (switchImg.isSelected())
			{
				if (device.isOpen())
				{
					HttpUtils.closeDevice(device.getIEEE(), new HttpUtils.HttpResponseListener() {
						
						@Override
						public void onStart() {
						}
						
						@Override
						public void onDone(boolean succ, String result) {
							if (succ)
							{
								switchImg.setSelected(false);
								
								textState.setTextColor(getResources().getColor(R.color.color_dev_closed));
								textState.setText(R.string.dev_state_closed);
	
								device.setOpen(false);
							}
							else
							{
								ToastUtils.show(thisObj, "关闭设备失败，请稍后再试！");
							}
						}
						
					});
				}
			}
		}
	}
	
	public void openDevice(View view, boolean needPost)
	{
		final ImageView switchImg = (ImageView)view.findViewById(R.id.device_switch_button);		
		final TextView textState = (TextView)view.findViewById(R.id.device_current_state);
		final DeviceInfo device = (DeviceInfo)switchImg.getTag(R.string.tag_sensor_info);
		if (!needPost)
		{
			switchImg.setSelected(true);
			textState.setTextColor(getResources().getColor(R.color.color_dev_running));
			textState.setText(R.string.dev_state_running);
			device.setOpen(true);
		}
		else
		{
			if (!switchImg.isSelected())
			{
				if (!device.isOpen())
				{
					HttpUtils.openDevice(device.getIEEE(), new HttpUtils.HttpResponseListener() {
						
						@Override
						public void onStart() {
						}
						
						@Override
						public void onDone(boolean succ, String result) {
							if (succ)
							{
								switchImg.setSelected(true);
	
								textState.setTextColor(getResources().getColor(R.color.color_dev_running));
								textState.setText(R.string.dev_state_running);
								
								device.setOpen(true);
							}
							else
							{
								ToastUtils.show(thisObj, "打开设备失败，请稍后再试！");
							}
						}
						
					});
				}
			}
		}
	}

	private Handler m_LoadHandler = new Handler(this);
	
	View.OnClickListener viewCameraClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			browse5Lianpai("first_20141119103126.zip");
			
			Object tagSensor = v.getTag(R.string.tag_sensor_info);
			if (tagSensor instanceof DeviceInfo)
			{
				DeviceInfo camera = (DeviceInfo)tagSensor;
				if (camera.isOnline())
				{
					final String ip = camera.getCameraIP();
					final String port = camera.getCameraPort();
					if (ip != null && !TextUtils.isEmpty(ip))
					{
						if (port != null && !TextUtils.isEmpty(port))
						{
							Intent intent=new Intent(DeviceActivity.this, PictureBrowserActivity.class);
							intent.putExtra("camera_sn", camera.getIEEE());
							intent.putExtra("mode", "danzhang");
							DeviceActivity.this.startActivity(intent);
						}
						else
						{
							ToastUtils.show(thisObj, R.string.err_camera_port_setting);
						}
					}
					else
					{
						ToastUtils.show(thisObj, R.string.err_camera_ip_setting);
					}
				}
				else
				{
					ToastUtils.show(thisObj, R.string.err_camera_is_offline);
				}
			}
			
		}
	};

	private ProgressDialog m_LoadingDialog;
	private ProgressDialog getLoadingDialog()
	{
		if (m_LoadingDialog == null)
		{
			m_LoadingDialog = new ProgressDialog(thisObj);
			m_LoadingDialog.setMessage(thisObj.getString(R.string.hint_processing));
		}
		return m_LoadingDialog;
	}
	
	View.OnClickListener sensorContainerOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tagValue = v.getTag(R.string.tag_sensor_info);
			if (tagValue instanceof DeviceInfo)
			{
				DeviceInfo device = (DeviceInfo)tagValue;
				if (device.isOnline())
				{
					if (device.getDeviceType() == ConstUtils.S_CAMERA_TYPE_CODE)
					{
						ActivityUtils.switchActivity(CameraSettingActivity.class, false, 
								200, device.getIEEE());
					}
					else
					{
						ActivityUtils.switchActivity(SensorSettingActivity.class, false, 
								200, device.getIEEE());
					}
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_device_offline);
				}
			}
		}
		
	};

	View.OnClickListener switchOnOffClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			View containerView = (View)v.getTag(R.string.tag_sensor_view);
			DeviceInfo device = (DeviceInfo)v.getTag(R.string.tag_sensor_info);
			if (device.isOnline())
			{
				if (v.isSelected())
				{
					closeDevice(containerView, true);
				}
				else
				{
					openDevice(containerView, true);
				}
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_device_offline);
			}
		}
		
	};
	
	private ScrollView m_ViewContainer;

	private MultiColumnView m_MultiColumnView;
	private DeviceAdapter m_DeviceAdapter;
	
	private LinearLayout m_ReceivingContainer;
	
	private FinalHttp m_FinalHttp = new FinalHttp();
	
	private HashMap<String, View> m_DeviceViews = new HashMap<String, View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingyan_activity_device);
		
		GlobalContext.S_DEVICE_VIEW = this;
		
		m_ReceivingContainer = (LinearLayout)findViewById(R.id.device_receiving_container);
		
		m_ViewContainer = (ScrollView)findViewById(R.id.device_column_views_container);
		
		m_MultiColumnView = (MultiColumnView)findViewById(R.id.device_column_views);
		m_MultiColumnView.setScrollView(m_ViewContainer);

		m_DeviceAdapter = new DeviceAdapter();
		m_MultiColumnView.setAdapter(m_DeviceAdapter);
		
		m_FinalHttp.configRequestExecutionRetryCount(ConstUtils.S_HTTP_REQUEST_RETRY_COUNT);
		m_FinalHttp.configTimeout(ConstUtils.S_HTTP_REQUEST_TIMEOUT);

		initView();
	}
	
	private boolean m_Inited = false;
	public void initView()
	{
		if (!m_Inited && GlobalContext.S_LOGINED)
		{
			initDevice();
			m_Inited = true;
		}
	}
	
	private boolean alreadyExists(DeviceInfo device)
	{
		if (device != null)
		{
			int count = m_DeviceAdapter.getCount();
			for (int i = 0; i < count; i++)
			{
				Object item = m_DeviceAdapter.getItem(i);
				if (item instanceof DeviceInfo)
				{
					DeviceInfo currSensor = (DeviceInfo)item;
					if (currSensor.getIEEE().equalsIgnoreCase(device.getIEEE()))
					{
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	public void reset()
	{
		m_DeviceAdapter.clear();
		m_Inited = false;
	}
	
	private void initDevice()
	{
		new Handler().post(new Runnable(){
			
			@Override
			public void run() {
				HttpUtils.getCameras(null);
				HttpUtils.getDevices(new HttpUtils.HttpResponseListener() {
					
					@Override
					public void onStart() {
					}
					
					@Override
					public void onDone(boolean succ, String result) {
						if (succ)
						{
							List<DeviceInfo> devices = DeviceList.getDeviceList(true);
							List<DeviceInfo> validDevices = new ArrayList<DeviceInfo>();
							for (DeviceInfo device : devices)
							{
								if (!alreadyExists(device))
								{
									validDevices.add(device);
								}
							}
							if (validDevices.size() > 0)
							{
								m_DeviceAdapter.add(validDevices.toArray());
							}
							
							if (m_DeviceAdapter.getCount() > 0)
							{
								m_ReceivingContainer.setVisibility(View.GONE);
								m_ViewContainer.setVisibility(View.VISIBLE);
							}
						}
					}
					
				});
			}

		});
	}
	
	public synchronized void onlineDevice(String devSN)
	{
		try
		{
			if (DeviceList.exists(devSN))
			{
				boolean haved = false;
				for (int i = 0; i < m_DeviceAdapter.getCount(); i++)
				{
					Object item = m_DeviceAdapter.getItem(i);
					if (item instanceof DeviceInfo)
					{
						if (((DeviceInfo)item).getIEEE().equals(devSN))
						{
							haved = true;
							break;
						}
					}
				}
				
				if (haved)
				{
					View devView = m_DeviceViews.get(devSN);
					if (devView != null)
					{
						DeviceInfo device = DeviceList.getDevice(devSN);
						
						device.setOpen(true);
						openDevice(devView, false);
						
						device.setOnline(true);
						View abnorView = devView.findViewById(R.id.device_abnor_mark);
						abnorView.setVisibility(View.GONE);
					}
				}
			}
		}
		catch (Exception exp)
		{
		}
	}
	
	public synchronized void updateDevice(DeviceInfo devInfo)
	{
		try
		{
			String devSN = devInfo.getIEEE();
			if (DeviceList.exists(devSN))
			{
				boolean haved = false;
				for (int i = 0; i < m_DeviceAdapter.getCount(); i++)
				{
					Object item = m_DeviceAdapter.getItem(i);
					if (item instanceof DeviceInfo)
					{
						if (((DeviceInfo)item).getIEEE().equals(devSN))
						{
							haved = true;
							break;
						}
					}
				}
				
				if (haved)
				{
					View devView = m_DeviceViews.get(devSN);
					if (devView != null)
					{
						DeviceInfo device = DeviceList.getDevice(devSN);
						
						LinearLayout sensorContainer = (LinearLayout)devView.findViewById(R.id.sensor_container);
						sensorContainer.setTag(R.string.tag_sensor_info, device);
						
						TextView aliasNameView = (TextView)devView.findViewById(R.id.device_alias_name);
						aliasNameView.setText(device.getName());
						
						ImageView sensorSwitch = (ImageView)devView.findViewById(R.id.device_switch_button);
						sensorSwitch.setTag(R.string.tag_sensor_info, device);
						
						String devType = device.getDeviceType();
						if (devType == ConstUtils.S_CAMERA_TYPE_CODE || 
								device.isOpen())
						{
							openDevice(devView, false);
						}
						else
						{
							closeDevice(devView, false);
						}
						
						View abnorView = devView.findViewById(R.id.device_abnor_mark);
						if (!device.isOnline())
						{
							abnorView.setVisibility(View.VISIBLE);
						}
						else
						{
							abnorView.setVisibility(View.GONE);
						}
					}
				}
				else
				{
					m_DeviceAdapter.add(devInfo);
				
					m_ReceivingContainer.setVisibility(View.GONE);
					m_ViewContainer.setVisibility(View.VISIBLE);					
				}
			}
		}
		catch (Exception exp)
		{
		}
	}
	
	public synchronized void addNewDevice(String addr, String time)
	{
		DeviceInfo device = DeviceList.getDevice(addr);
		if (device != null)
		{
			boolean haved = false;
			
			for (int i = 0; i < m_DeviceAdapter.getCount(); i++)
			{
				Object item = m_DeviceAdapter.getItem(i);
				if (item instanceof DeviceInfo)
				{
					if (((DeviceInfo)item).getIEEE().equals(device.getIEEE()))
					{
						haved = true;
						break;
					}
				}
			}
			
			if (!haved)
			{
				m_DeviceAdapter.add(device);
			}
			else
			{
				
			}
			
			m_ReceivingContainer.setVisibility(View.GONE);
			m_ViewContainer.setVisibility(View.VISIBLE);
		}
	}
	
	public View getDeviceView(String addr)
	{
		if (addr != null && !TextUtils.isEmpty(addr))
		{
			return m_DeviceViews.get(addr);
		}
		return null;
	}
	
	public synchronized void updateSensorViewName(String sensorAddr, String sensorName)
	{
		View sensorView = getDeviceView(sensorAddr);
		if (sensorView != null)
		{
			TextView aliasNameView = (TextView)sensorView.findViewById(R.id.device_alias_name);
			aliasNameView.setText(sensorName);
			
			DeviceInfo device = DeviceList.getDevice(sensorAddr);
			if (device != null)
			{
				device.setName(sensorName);
			}
		}
	}
	
	public void sensorAbnor(DeviceInfo device)
	{
		Message m = new Message();
		m.what = 2;
		m.obj = device;
		m_LoadHandler.sendMessage(m);		
	}
	
	private synchronized void sensorAbnor(String addr)
	{
		View view = getDeviceView(addr);
		if (view != null)
		{
			View abnorMakrView = view.findViewById(R.id.device_abnor_mark);
			if (abnorMakrView != null)
			{
				abnorMakrView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void sensorRecovery(DeviceInfo device)
	{
		Message m = new Message();
		m.what = 3;
		m.obj = device;
		m_LoadHandler.sendMessage(m);		
	}
	
	private synchronized void sensorRecovery(String addr)
	{
		View view = getDeviceView(addr);
		if (view != null)
		{
			View abnorMakrView = view.findViewById(R.id.device_abnor_mark);
			if (abnorMakrView != null)
			{
				abnorMakrView.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public synchronized void triggerAlarm(String addr)
	{
		if (addr == null)
		{
			return;
		}
		
		View sensorView = getDeviceView(addr);
		if (sensorView != null)
		{
			LinearLayout container = (LinearLayout)sensorView.findViewById(R.id.sensor_container);
			final AnimationDrawable animDrawable = (AnimationDrawable)container.getBackground();
			
			if (animDrawable != null)
			{
				animDrawable.start();
				
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						animDrawable.stop();
						animDrawable.selectDrawable(0);

					}

				}, 1000 * 5);
			}
		}
	}
	
	public synchronized void closeAllDevice()
	{
		int count = m_MultiColumnView.getViewCount();
		for (int i = 0; i < count; i++)
		{
			View v = m_MultiColumnView.getViewAt(i);
			closeDevice(v, true);
		}
	}
	
	public synchronized void openAllDevice()
	{
		int count = m_MultiColumnView.getViewCount();
		for (int i = 0; i < count; i++)
		{
			View v = m_MultiColumnView.getViewAt(i);
			openDevice(v, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg != null)
		{
			if (msg.what == 0)
			{
				ProgressDialog pd = getLoadingDialog();
				if (pd != null)
				{
					pd.show();
				}
			}
			else if (msg.what == 1)
			{
				ProgressDialog pd = getLoadingDialog();
				if (pd != null)
				{
					pd.dismiss();
				}
			}
			else if (msg.what == 2)
			{
				if (msg.obj instanceof DeviceInfo)
				{
					DeviceInfo device = (DeviceInfo)msg.obj;
					sensorAbnor(device.getIEEE());
				}
			}
			else if (msg.what == 3)
			{
				if (msg.obj instanceof DeviceInfo)
				{
					DeviceInfo device = (DeviceInfo)msg.obj;
					this.sensorRecovery(device.getIEEE());
				}
			}
		}
		return false;
	}

}
