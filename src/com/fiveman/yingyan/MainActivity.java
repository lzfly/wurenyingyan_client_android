package com.fiveman.yingyan;

import java.util.Hashtable;

import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.model.list.DeviceTypeList;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.AppUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.NavBarUtils;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.widgets.ContextMenu;
import com.fiveman.yingyan.widgets.MessageDialog;

import android.app.ActivityGroup;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	private ContextMenu m_MainMenu = null;
	
	private ViewGroup m_ViewContainer = null;
	
	private TextView m_UnreadNoticeHint = null;
	
	private Hashtable<Integer, View> m_Views = new Hashtable<Integer, View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_main);

		ActivityUtils.setMainActivity(this);
		AppNotification.cancel();
		
		m_ViewContainer = (ViewGroup)findViewById(R.id.activity_container);
		m_UnreadNoticeHint = (TextView)findViewById(R.id.unread_notice_hint);

		LinearLayout navbarContainer = (LinearLayout)findViewById(R.id.nav_bar_button_container);
		NavBarUtils.init(navbarContainer);
		NavBarUtils.setOnNavButtonClickListener(onNavButtonClickListener);
		
		NavBarUtils.setUnselectState();
		NavBarUtils.doClick(R.id.nav_button_message);
		
		initMenu();
		
		LinearLayout menuButton = (LinearLayout)findViewById(R.id.action_titlebar_menu);
		menuButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				m_MainMenu.show(v, 0, 0);
			}

		});
	}
	
	NavBarUtils.OnNavButtonClickListener onNavButtonClickListener = new NavBarUtils.OnNavButtonClickListener() {
		
		@Override
		public void onClick(View button, int buttonId) {
			ActivityGroup currActivity = ActivityUtils.getCurrActivity();
			
			Class<?> viewClass = null;
			if (buttonId == R.id.nav_button_message)
			{
				viewClass = MessageActivity.class;
			}
			else if (buttonId == R.id.nav_button_device)
			{
				viewClass = DeviceActivity.class;
			}
			
			if (viewClass != null)
			{
				m_ViewContainer.removeAllViews();
				if (!m_Views.containsKey(buttonId))
				{
					
					View childView = currActivity.getLocalActivityManager().startActivity(
			                "YingYan",
			                new Intent(currActivity, viewClass)
			                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
			                .getDecorView();
					m_ViewContainer.addView(childView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			        m_Views.put(buttonId, childView);
				}
				else
				{
					View childView = m_Views.get(buttonId);
					m_ViewContainer.addView(childView);
				}
			}
		}
		
	};

	private void initMenu()
	{
		m_MainMenu = new ContextMenu(this);
		m_MainMenu.setOnMenuItemClickListener(menuItemClickListener);
		
		m_MainMenu.addItem(R.string.menu_item_history_message);	//历史消息
		m_MainMenu.addItem(R.string.menu_item_change_password);	//修改密码
		m_MainMenu.addItem(R.string.menu_item_check_new_version);	//检查更新
		m_MainMenu.addItem(R.string.menu_item_about_me);	//关于我们
		m_MainMenu.addItem(R.string.menu_item_switch_account);	//切换用户
		m_MainMenu.addItem(R.string.menu_item_close_app);	//关闭应用
	}
	
	public void showNewMessageHint(int count)
	{
		m_UnreadNoticeHint.setVisibility(View.VISIBLE);
		m_UnreadNoticeHint.setText(String.valueOf(count));
	}
	
	public void hideNewMessageHint()
	{
		m_UnreadNoticeHint.setVisibility(View.GONE);
	}

	ContextMenu.OnMenuItemClickListener menuItemClickListener  = new ContextMenu.OnMenuItemClickListener() {
		
		@Override
		public void onMenuItemClickListener(int position, String label) {
			String menuViewHistory = thisObj.getString(R.string.menu_item_history_message);
			String menuChangePassword = thisObj.getString(R.string.menu_item_change_password);
			String menuCheckVersion = thisObj.getString(R.string.menu_item_check_new_version);
			String menuAboutme = thisObj.getString(R.string.menu_item_about_me);
			String menuSwitch = thisObj.getString(R.string.menu_item_switch_account);
			String menuExit = thisObj.getString(R.string.menu_item_close_app);
		
			if (TextUtils.equals(menuViewHistory, label))
			{
				
			}
			else if (TextUtils.equals(menuChangePassword, label))
			{
				ActivityUtils.switchActivity(ChangePasswordActivity.class, false, 0, "");
			}
			else if (TextUtils.equals(menuCheckVersion, label))
			{
				
			}
			else if (TextUtils.equals(menuAboutme, label))
			{
				ActivityUtils.switchActivity(AboutActivity.class, false, 0, "");
			}
			else if (TextUtils.equals(menuSwitch, label))
			{
				GlobalContext.reset();
				HttpUtils.reset();
				
				SPConfig.setPropery("password", "");
				
				DeviceTypeList.clear();
				DeviceList.clear();
				
				ActivityUtils.switchActivity(LoginActivity.class, true, 0, "");
			}
			else if (TextUtils.equals(menuExit, label))
			{
				MessageDialog.Builder builder = new MessageDialog.Builder(thisObj);
				builder.setCaption(thisObj.getString(R.string.message_dialog_caption));
				builder.setMessage(thisObj.getString(R.string.hint_for_exit));
				builder.setOkButton(R.string.action_exit_button_caption, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppUtils.exit();
					}
					
				});
				builder.setCancelButton(R.string.action_exit_cancel_button_caption, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
				
				builder.create().show();
				
			}
		}

	};
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SPConfig.setPropery("last_page", "");
		AppNotification.cancel();
		
		if (!GlobalContext.S_LOGINED)
		{
			Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "MainActivity");
		AppNotification.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.moveTaskToBack(true);
		}
		return false;
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		LinearLayout btnMenu = (LinearLayout)findViewById(R.id.action_titlebar_menu);
		m_MainMenu.show(btnMenu, 0, 0);

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("FiveMan_YingYan");
		return super.onCreateOptionsMenu(menu);
	}

}
