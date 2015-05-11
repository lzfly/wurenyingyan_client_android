package com.fiveman.yingyan;

import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.AppUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.ConstUtils;
import com.fiveman.yingyan.utils.EncryptUtils;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.NetUtils.NetState;
import com.fiveman.yingyan.utils.NetUtils2;
import com.fiveman.yingyan.utils.NetUtils2.TestNetworkResult;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.SoundUtils;
import com.fiveman.yingyan.utils.ToastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ProgressDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseActivity implements Callback {

	private Handler m_NetworkHandler = new Handler(this);
	
	private ProgressDialog m_WaitingDlg;
	
	private EditText m_UserName;
	private EditText m_Password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingyan_activity_login);
		
		ActivityUtils.setCurrActivity(this);
		
		m_UserName = (EditText)findViewById(R.id.edit_login_user);
		m_Password = (EditText)findViewById(R.id.edit_login_pass);
		
		Button loginBtn = (Button)findViewById(R.id.login_button);
		loginBtn.setOnClickListener(loginButtonClickListener);
		
		TextView actionGetPass = (TextView)findViewById(R.id.action_get_password);
		
		actionGetPass.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ActivityUtils.switchActivity(GetPasswordActivity.class, false, 0, "");
			}
			
		});
		
		SoundUtils.init();
		
		String userName = SPConfig.getPropertyAsString("username", "");		
		m_UserName.setText(userName);
	}
	
	View.OnClickListener loginButtonClickListener = new View.OnClickListener()
	{

		@Override
		public void onClick(View v) {
			String username = m_UserName.getText().toString();
			if (username != null && !TextUtils.isEmpty(username))
			{
				String password = m_Password.getText().toString();
				if (password != null && !TextUtils.isEmpty(password))
				{
					getWaitingDialog().show();
					
					NetUtils2.TestNetwork(thisObj, new TestNetworkResult() {

						@Override
						public void onDone(boolean succ, NetState state) {
							if (succ)
							{
								m_NetworkHandler.sendEmptyMessage(1);
							}
							else
							{
								m_NetworkHandler.sendEmptyMessage(0);
							}
						}
						
					});
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_password);
				}
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_user_name);
			}
		}
		
	};
	
	private ProgressDialog getWaitingDialog()
	{
		if (m_WaitingDlg == null)
		{
			m_WaitingDlg = new ProgressDialog(this);
			m_WaitingDlg.setMessage(getString(R.string.hint_login_waiting));
		}
		return m_WaitingDlg;
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
				
				new Handler().postDelayed(new Runnable()
				{

					@Override
					public void run() {
						SPConfig.setPropery("username", m_UserName.getText().toString());
						String pwd = EncryptUtils.encrypt(m_Password.getText().toString());
						SPConfig.setPropery("password", pwd);
						
						getWaitingDialog().dismiss();
						
						ActivityUtils.switchActivity(MainActivity.class, true, 0, "");
					}
					
				}, ConstUtils.LOGIN_WAIT_TIME);
			}
			else
			{
				ToastUtils.show(thisObj, R.string.hint_login_failed);
				
				getWaitingDialog().dismiss();
			}
		}

		@Override
		public void onStart() {
		}
		
	};

	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "LoginActivity");
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
		if (!AppUtils.exitWithBackKey(this, keyCode))
		{
			return super.onKeyDown(keyCode, event);
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean handleMessage(Message arg0) {
		if (arg0.what == 0)
		{
			ToastUtils.show(thisObj, R.string.no_internet);

			getWaitingDialog().dismiss();
		}
		else if (arg0.what == 1)
		{
			String username = m_UserName.getText().toString();
			String password = m_Password.getText().toString();
			HttpUtils.login(username, password, loginCallback);
		}
		return false;
	}

}
