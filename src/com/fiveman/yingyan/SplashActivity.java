package com.fiveman.yingyan;

import java.io.File;

import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.EncryptUtils;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.SoundUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;

public class SplashActivity extends Activity implements Callback {

	private Handler m_MsgHandler = new Handler(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingyan_activity_splash);
		
		//jiaojc
		File configPath=new File(ConstUtils.G_GLOABAL_PATH);
		if(!configPath.exists())
		{
			configPath.mkdir();
		}
		//jiaojc
		
		SoundUtils.init();
		
		String username = SPConfig.getPropertyAsString("username", "");
		String password = SPConfig.getPropertyAsString("password", "");
		if (username != null && !TextUtils.isEmpty(username) &&
				password != null && !TextUtils.isEmpty(password))
		{
			try
			{
				String pwd = EncryptUtils.decrypt(password);
				HttpUtils.login(username, pwd, loginCallback);
			}
			catch (Exception exp)
			{
				m_MsgHandler.sendEmptyMessage(0);
			}
		}
		else
		{
			m_MsgHandler.sendEmptyMessage(0);
		}
	}

	HttpUtils.HttpResponseListener loginCallback = new HttpUtils.HttpResponseListener() {
		
		@Override
		public void onDone(boolean succ, String result) {
			if (succ)
			{
				HttpUtils.userInit(null);
				
				HttpUtils.getDeviceTypes(null);
				HttpUtils.getDevices(null);
				HttpUtils.getCameras(null);				
				
				m_MsgHandler.sendEmptyMessage(1);
			}
			else
			{
				m_MsgHandler.sendEmptyMessage(0);
			}
		}
		
		@Override
		public void onStart() {
		}
		
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "SplashActivity");
		AppNotification.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SPConfig.setPropery("last_page", "");
		AppNotification.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == 1)
		{
			new Handler().postDelayed(new Runnable()
			{

				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
	                SplashActivity.this.startActivity(intent);  
	                SplashActivity.this.finish(); 
				}
				
			}, ConstUtils.LOGIN_WAIT_TIME);
		}
		else if (msg.what == 0)
		{
			new Handler().postDelayed(new Runnable()
			{
	
				@Override
				public void run() {
					//TODO 此处根据需要看是否显示Guide Page
	
					Intent guideIntent;
	//				if (mSettings.getBoolean("is_pattern_set", false)) 
	//				{
	//					guideIntent= new Intent(SplashActivity.this, PatternLoginActivity.class);					
	//				}
	//				else
	//				{
	//					guideIntent = new Intent(SplashActivity.this,LoginActivity.class);
	//				}

					guideIntent = new Intent(SplashActivity.this, LoginActivity.class);
	                SplashActivity.this.startActivity(guideIntent);  
	                SplashActivity.this.finish();
				}
				
			}, ConstUtils.SPLASH_DISPLAY_TIME);
		}
		return false;
	}

}
