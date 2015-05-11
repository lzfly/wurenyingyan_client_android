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
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ChangePasswordActivity extends BaseActivity {

	private ProgressDialog m_WaitingDlg;
	
	private EditText m_PassEdit;
	private EditText m_PassEdit2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_changepassword);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_rename_device);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.title_activity_changepassword);
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_cancel);
		backAction.setOnClickListener(new View.OnClickListener() {
			
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
		
		m_PassEdit = (EditText)findViewById(R.id.user_new_password);
		m_PassEdit2 = (EditText)findViewById(R.id.user_new_password2);
		
		LinearLayout clearAction = (LinearLayout)findViewById(R.id.clear_edit_text);
		clearAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_PassEdit.setText("");
			}
			
		});
		
		LinearLayout clearAction2 = (LinearLayout)findViewById(R.id.clear_edit_text2);
		clearAction2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_PassEdit2.setText("");
			}
			
		});
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
			final String newPass = m_PassEdit.getText().toString();
			final String newPass2 = m_PassEdit2.getText().toString();
			
			if (newPass != null && !TextUtils.isEmpty(newPass))
			{
				if (newPass.equals(newPass2))
				{
					CommonUtils.closeInputboard();
					
					getWaitingDialog().show();
					
					HttpUtils.changePassword(newPass, new HttpResponseListener() {
	
						@Override
						public void onStart() {
						}
	
						@Override
						public void onDone(boolean succ, String result) {
							getWaitingDialog().dismiss();
							
							if (succ)
							{
								MessageDialog.Builder builder = new MessageDialog.Builder(thisObj);
								builder.setCaption(thisObj.getString(R.string.message_dialog_caption));
								builder.setMessage(thisObj.getString(R.string.hint_new_pass_ok));
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
								ToastUtils.show(thisObj, R.string.err_change_password_hint);
							}
						}
						
					});
				}
				else
				{
					ToastUtils.show(thisObj, R.string.err_two_password_diff);
				}
			}
			else
			{
				ToastUtils.show(thisObj, R.string.err_password_cant_null);
			}
		}
		
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "ChangePasswordActivity");
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
