package com.fiveman.yingyan;

import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.CommonUtils;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.HttpUtils.HttpResponseListener;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ToastUtils;
import com.fiveman.yingyan.widgets.MessageDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

public class GetPasswordActivity extends BaseActivity {

	private ProgressDialog m_WaitingDlg;
	
	private EditText m_NameEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_getpassword);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_rename_device);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.titble_getpass_caption);
		
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
		
		m_NameEdit = (EditText)findViewById(R.id.login_user_name);
		
		LinearLayout clearAction = (LinearLayout)findViewById(R.id.clear_edit_text);
		clearAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_NameEdit.setText("");
			}
			
		});
	}

	private ProgressDialog getWaitingDialog()
	{
		if (m_WaitingDlg == null)
		{
			m_WaitingDlg = new ProgressDialog(this);
			m_WaitingDlg.setCancelable(false);
			String hint = String.format(getString(R.string.hint_get_password), 60);
			m_WaitingDlg.setMessage(hint);
		}
		return m_WaitingDlg;
	}

	private TimeCount m_TimeTicker = new TimeCount(60000, 1000);
	class TimeCount extends CountDownTimer
	{

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			getWaitingDialog().dismiss();
			
			if (m_GetPassResult)
			{
				MessageDialog.Builder builder = new MessageDialog.Builder(thisObj);
				builder.setCaption(thisObj.getString(R.string.message_dialog_caption));
				builder.setMessage(thisObj.getString(R.string.hint_get_pass_ok));
				builder.setOkButton(thisObj.getString(R.string.hint_i_unknown), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityUtils.finishTop(null);
					}
					
				});
				builder.setCancelButton(null, null);
				builder.create().show();
			}
			else
			{
				ToastUtils.show(thisObj, R.string.err_get_pass);
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			ProgressDialog pd = getWaitingDialog();
			pd.setMessage(String.format(getString(R.string.hint_get_password), millisUntilFinished / 1000));
		}
		
	}
	
	private boolean m_GetPassResult = false;
	View.OnClickListener confirmOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final String newName = m_NameEdit.getText().toString();
			
			if (newName != null && !TextUtils.isEmpty(newName))
			{
				getWaitingDialog().show();
				m_TimeTicker.start();
				
				CommonUtils.closeInputboard();
				
				HttpUtils.getPassword(newName, new HttpResponseListener() {

					@Override
					public void onStart() {
					}

					@Override
					public void onDone(boolean succ, String result) {

						m_GetPassResult = succ;
					}
				});
			}
			else
			{
				ToastUtils.show(thisObj, R.string.please_input_user_name);
			}
		}
		
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "GetPasswordActivity");
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
