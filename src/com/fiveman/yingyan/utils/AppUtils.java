package com.fiveman.yingyan.utils;

import java.util.Date;
import java.util.List;

import com.fiveman.yingyan.R;
import com.igexin.sdk.PushManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.view.KeyEvent;

public class AppUtils extends Object {
	
	public static boolean isBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) 
        {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) 
            {
                return true;
            }
        }
        return false;
    }
	
	public static void exit()
	{
		//停止接收消息推送
		PushManager.getInstance().turnOffPush(GlobalContext.getInstance().getApplicationContext());
		PushManager.getInstance().stopService(GlobalContext.getInstance().getApplicationContext());
		
		ActivityUtils.finishAll();
		System.exit(0);
	}

	private static Date m_PrevBackTime = null;
	public static boolean exitWithBackKey(Context context, int keyCode)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (m_PrevBackTime == null)
			{
				m_PrevBackTime = new Date();
				ToastUtils.show(context, context.getResources().getString(R.string.app_exit_when_click_agin));
			}
			else
			{
				Date now = new Date();
				if (now.getTime() - m_PrevBackTime.getTime() < 3000)
				{
					AppUtils.exit();
				}
				else
				{
					m_PrevBackTime = new Date();
					ToastUtils.show(context, context.getResources().getString(R.string.app_exit_when_click_agin));
				}
			}
			
			return true;
		}
		
		return false;
	}
	
}
