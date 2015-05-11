package com.fiveman.yingyan;

import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.CommonUtils;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ToastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ModifyCameraPortActivity extends BaseActivity {

	private ProgressDialog m_WaitingDlg;
	
	private EditText m_NameEdit;

	private String m_CurrSensorAddr;
	private String m_CurrSensorPort;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_modify_camera_port);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_rename_device);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.title_modify_camera_port);
		
		LinearLayout cancelAction = (LinearLayout)findViewById(R.id.action_cancel);
		cancelAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (CommonUtils.closeInputboard())
				{
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							ActivityUtils.finishTop(null);							
						}
						
					}, 700);
				}
				else
				{
					ActivityUtils.finishTop(null);
				}
			}
			
		});
		
		LinearLayout confirmAction = (LinearLayout)findViewById(R.id.action_confirm);
		confirmAction.setOnClickListener(confirmOnClickListener);		
		
		m_NameEdit = (EditText)findViewById(R.id.camera_new_port);
		
		LinearLayout clearAction = (LinearLayout)findViewById(R.id.clear_edit_text);
		clearAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_NameEdit.setText("");
			}
			
		});
		
		Intent intent = this.getIntent();
		m_CurrSensorAddr = intent.getStringExtra("arg0");
		m_CurrSensorPort = intent.getStringExtra("arg1");
		
		m_NameEdit.setText(m_CurrSensorPort);
	}

	private ProgressDialog getWaitingDialog()
	{
		if (m_WaitingDlg == null)
		{
			m_WaitingDlg = new ProgressDialog(this);
			m_WaitingDlg.setMessage(getString(R.string.hint_submit_waiting));
		}
		return m_WaitingDlg;
	}
	
	View.OnClickListener confirmOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final String newName = m_NameEdit.getText().toString();
			
			if (newName != null && !TextUtils.isEmpty(newName))
			{
				CommonUtils.closeInputboard();
				
				getWaitingDialog().show();
				
				if (!newName.equals(m_CurrSensorPort))
				{
					DeviceInfo sourceSensor = DeviceList.getDevice(m_CurrSensorAddr);
					if (sourceSensor != null)
					{
						if ("0xFFFF".equals(sourceSensor.getDeviceType()))
						{
							HttpUtils.updateCameraPort(m_CurrSensorAddr, newName, new HttpUtils.HttpResponseListener() {
								
								@Override
								public void onStart() {
								}
								
								@Override
								public void onDone(boolean succ, String result) {
									getWaitingDialog().dismiss();
									
									if (succ)
									{
										ToastUtils.show(thisObj, R.string.hint_modify_camera_port_succ);
										
										DeviceInfo sensor = DeviceList.getDevice(m_CurrSensorAddr);
										if (sensor != null)
										{
											sensor.setCameraPort(newName);
										}
										
										Intent data = new Intent();
										data.putExtra("camera_addr", m_CurrSensorAddr);
										data.putExtra("camera_port", newName);
										setResult(ConstUtils.S_MODIFY_CAMERA_PORT_ACTIVITY_RESULT_CODE, data);
										
										ActivityUtils.finishTop(null);
									}
									else
									{
										ToastUtils.show(thisObj, R.string.hint_modify_camera_port_failed);
									}
								}
							});
						}
						else
						{
							getWaitingDialog().dismiss();
							ToastUtils.show(thisObj, R.string.err_modify_camera_port);
						}
					}
					else
					{
						getWaitingDialog().dismiss();
						ToastUtils.show(thisObj, R.string.err_modify_camera_port);
					}
				}
				else
				{
					getWaitingDialog().dismiss();
					ToastUtils.show(thisObj, R.string.hint_modify_camera_port_succ);
					
					ActivityUtils.finishTop(null);
				}
			}
			else
			{
				ToastUtils.show(thisObj, R.string.please_input_new_camera_port);
			}
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "ModifyCameraPortActivity");
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

}
