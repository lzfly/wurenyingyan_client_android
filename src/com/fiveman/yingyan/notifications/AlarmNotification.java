package com.fiveman.yingyan.notifications;

import com.fiveman.yingyan.R;
import com.fiveman.yingyan.utils.GlobalContext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class AlarmNotification {

	private static NotificationManager S_NOTIFICATION_MGR = null;
	
	private static int S_NOTIFICATION_ID = 4444;
	
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
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(ComponentName.unflattenFromString("com.fiveman.yingyan/.MainActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.putExtra("from_notification", true);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			
			if (S_NOTIFICATION == null)
			{
				S_NOTIFICATION = new Notification(R.drawable.yingyan_icon_warning, 
						context.getString(R.string.warn_title), 
						System.currentTimeMillis());

				S_NOTIFICATION.defaults = Notification.DEFAULT_ALL;

				//TODO 一直响，直到用户响应		FLAG_INSISTENT
				S_NOTIFICATION.flags = Notification.FLAG_AUTO_CANCEL 
						| Notification.FLAG_SHOW_LIGHTS;
				
				S_NOTIFICATION.setLatestEventInfo(context.getApplicationContext(), 
						context.getString(R.string.app_name), 
						context.getString(R.string.warn_new_message), pendingIntent);
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
