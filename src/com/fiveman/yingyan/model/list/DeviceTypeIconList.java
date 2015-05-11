package com.fiveman.yingyan.model.list;

import java.lang.reflect.Field;
import java.util.Hashtable;

import android.text.TextUtils;
import android.util.Log;

import com.fiveman.yingyan.R;
import com.fiveman.yingyan.model.DeviceTypeInfo;

public class DeviceTypeIconList {

	private static Hashtable<String, Integer> S_ICONS = new Hashtable<String, Integer>();
	
	public static int getDeviceTypeIcon(String code)
	{
		if (code != null && !TextUtils.isEmpty(code))
		{
			if (S_ICONS.containsKey(code))
			{
				return S_ICONS.get(code);
			}
			else
			{
				DeviceTypeInfo devType = DeviceTypeList.getDeviceType(code);
				if (devType != null)
				{
					String resName = devType.getIcon();
					try {
						Field iconField = R.drawable.class.getField(resName);
						if (iconField != null)
						{
							int resId = iconField.getInt(new R.drawable());
							S_ICONS.put(code, resId);
							
							return resId;
						}
					} catch (Exception e) {
						Log.d("wxm", e.getMessage());
					}
				}
			}
		}
		return 0;
	}

}
