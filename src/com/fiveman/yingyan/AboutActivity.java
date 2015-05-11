package com.fiveman.yingyan;

import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.SPConfig;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_activity_about);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_about);

		ActivityUtils.setCurrActivity(this);

		try {
			PackageManager pm = this.getPackageManager(); 
			PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
			String strShow=this.getResources().getString(R.string.about_version_label) + pi.versionName;

			((TextView)this.findViewById(R.id.about_version_text)).setText(strShow);
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_back);
		backAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityUtils.switchActivity(MainActivity.class, true, 0, "");
			}
			
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "AboutActivity");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
