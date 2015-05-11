package com.fiveman.yingyan.notifications;

import com.fiveman.yingyan.R;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.SPConfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class AppNotification {

	private static NotificationManager S_NOTIFICATION_MGR = null;
	
	private static int S_NOTIFICATION_ID = 9688;
	
	private static Notification S_NOTIFICATION = null;
	
	private static void init(Context context)
	{
		if (S_NOTIFICATION_MGR == null)
		{
			S_NOTIFICATION_MGR = (NotificationManager)context.
					getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		}
	}
	
	public static void show()
	{
		Context context = GlobalContext.getInstance();
		if (GlobalContext.isBackground())
		{
			cancel();
			
			init(context);
			
			String className = SPConfig.getPropertyAsString("last_page", "MainActivity");
			
			if (TextUtils.isEmpty(className))
			{
				className = "MainActivity";
			}
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(ComponentName.unflattenFromString("com.fiveman.yingyan/." + className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			
			if (S_NOTIFICATION == null)
			{
				S_NOTIFICATION = new Notification(R.drawable.ic_launcher, 
						context.getString(R.string.app_running), 
						System.currentTimeMillis());

				//TODO 一直响，直到用户响应
//				FLAG_INSISTENT
				S_NOTIFICATION.flags = Notification.FLAG_ONGOING_EVENT;
				
				//TODO 设置系统自定义声音
//				notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
				
				S_NOTIFICATION.setLatestEventInfo(context.getApplicationContext(), 
						context.getString(R.string.app_name), 
						context.getString(R.string.app_running), pendingIntent);
			}
			
			S_NOTIFICATION_MGR.notify(S_NOTIFICATION_ID, S_NOTIFICATION);
		}
	}

	public static void cancel()
	{
		Context context = GlobalContext.getInstance();
		init(context);
		
		S_NOTIFICATION_MGR.cancel(S_NOTIFICATION_ID);
		
		if (S_NOTIFICATION != null)
		{
			S_NOTIFICATION = null;
		}
	}
}
