package com.fiveman.yingyan.utils;

import java.util.Enumeration;
import java.util.Stack;

import com.fiveman.yingyan.R;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class ActivityUtils extends Object {
	
	private static ActivityGroup S_MAIN_ACTIVITY = null;
	private static ActivityGroup S_CURR_ACTIVITY = null;
	private static Stack<BaseActivity> S_ACTIVITIES = new Stack<BaseActivity>();
	
	public static void setMainActivity(BaseActivity activity)
	{
		S_MAIN_ACTIVITY = activity;
		S_CURR_ACTIVITY = activity;
	}
	
	public static ActivityGroup getMainActivity()
	{
		return S_MAIN_ACTIVITY;
	}
	
	public static void setCurrActivity(BaseActivity activity)
	{
		if (activity != null)
		{
			S_CURR_ACTIVITY = activity;
			S_ACTIVITIES.push(activity);
		}
	}
	
	public static ActivityGroup getCurrActivity()
	{
		return S_CURR_ACTIVITY;
	}

	public static void setTitleBarCaption(String text)
	{
		TextView tvTitleBar = (TextView)S_CURR_ACTIVITY.findViewById(R.id.text_titlebar_caption);
		if (tvTitleBar != null)
		{
			tvTitleBar.setText(text);
		}
	}
	
	public static void setTitleBarCaption(int resId)
	{
		TextView tvTitleBar = (TextView)S_CURR_ACTIVITY.findViewById(R.id.text_titlebar_caption);
		if (tvTitleBar != null)
		{
			String text = S_CURR_ACTIVITY.getResources().getString(resId);
			tvTitleBar.setText(text);
		}
	}
	
	public static View findViewById(int id)
	{
		return S_CURR_ACTIVITY.findViewById(id);
	}
	
	public static boolean switchActivity(Class<?> activityClass, boolean finishCurrent, int resultCode, String...args)
	{
		if (S_CURR_ACTIVITY != null)
		{
			Intent intent = new Intent(S_CURR_ACTIVITY, activityClass);
			
			intent.putExtra("count", args.length);
			for (int i = 0; i < args.length; i++)
			{
				intent.putExtra("arg" + i, args[i]);
			}
			
			if (resultCode > 0)
			{
				S_CURR_ACTIVITY.startActivityForResult(intent, resultCode);
			}
			else
			{
				S_CURR_ACTIVITY.startActivity(intent);
			}
			
			if (finishCurrent)
			{
				S_CURR_ACTIVITY.finish();
				S_ACTIVITIES.remove(S_CURR_ACTIVITY);
			}

			return true;
		}
		return false;
	}
	
	public static void finishTop(Activity activity)
	{
		if (activity == null)
		{
			activity = S_CURR_ACTIVITY;
		}
		
		if (activity != null && activity != S_MAIN_ACTIVITY)
		{
			S_ACTIVITIES.remove(activity);
			activity.finish();
			if (S_ACTIVITIES.size() > 0)
			{
				S_CURR_ACTIVITY = S_ACTIVITIES.lastElement();
			}
			else
			{
				S_CURR_ACTIVITY = S_MAIN_ACTIVITY;
			}
		}
	}

	public static void finishAll()
	{
		Enumeration<BaseActivity> e = S_ACTIVITIES.elements();
		while (e.hasMoreElements())
		{
			BaseActivity currActivity = e.nextElement();
			if (currActivity != null)
			{
				currActivity.finish();
			}
		}
		S_ACTIVITIES.clear();
		
		if (S_MAIN_ACTIVITY != null)
		{
			S_MAIN_ACTIVITY.finish();
		}
	}
	
}
