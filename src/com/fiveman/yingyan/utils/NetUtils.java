package com.fiveman.yingyan.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.*;

public class NetUtils {

	public enum NetState
	{
		Unknown,
		Disconnected,
		MobileConnected,
		WifiConnected
	}
	
	public static boolean openUrl() {  
		try {  
			URL url = new URL("http://www.baidu.com/index.html");  
			URLConnection urlCon = url.openConnection();  
			urlCon.setConnectTimeout(1500);  
			InputStream is = urlCon.getInputStream();  
			BufferedInputStream bis = new BufferedInputStream(is);  
			// ”√ByteArrayBufferª∫¥Ê  
			ByteArrayBuffer baf = new ByteArrayBuffer(50);  
			int current = 0;  
			while ((current = bis.read()) != -1) {  
				baf.append((byte) current);  
			}  

			//myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			bis.close();  
			is.close();
			
			return true;
		} 
		catch (Exception e) {  
			return false;
		}
		  
//		return false;  
	}  
		
	public static NetState getNetState(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected())
		{
			try
			{
				if (ni.isAvailable() && ni.isConnected())
				{
					InetAddress ia = InetAddress.getByName(S_CHECK_URL);
					if (ia != null)
//					if (openUrl())
					{
						if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
						{
							return NetState.MobileConnected;
						}
						else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
						{
							return NetState.WifiConnected;
						}
					}
				}
			}
			catch (Exception exp)
			{
			}
		}
		return NetState.Disconnected;
	}
	
	private static String S_CHECK_URL = "www.baidu.com";
	
}
