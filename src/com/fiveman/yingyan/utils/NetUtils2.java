package com.fiveman.yingyan.utils;

import java.io.InputStream;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.fiveman.yingyan.utils.NetUtils.NetState;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils2 {

	public interface TestNetworkResult
	{
		void onDone(boolean succ, NetState state);
	}
	
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient S_HTTP_CLIENT;
	
	public static synchronized HttpClient getHttpClient() {
        if (null== S_HTTP_CLIENT) {
            HttpParams params =new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, false);
            HttpProtocolParams.setUserAgent(params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 超时设置
            ConnManagerParams.setMaxTotalConnections(params, 300);
            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(300));
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 1000 * 3);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 1000 * 5);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 1000 * 5);
            
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg =new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
            S_HTTP_CLIENT = new DefaultHttpClient(conMgr, params);
            
//            return new DefaultHttpClient(conMgr, params);
        }
        return S_HTTP_CLIENT;
    }
	
	private static boolean S_NETWORK_CONNECTED = true;
	private static Date S_NETWORK_CHECK_TIME = new Date();
	private static int S_NETWORK_CHECK_INTERVAL = 1000 * 60;
	private static int S_NETWORK_CHECK_INTERVAL2 = 1000 * 5;
	private static String S_CHECK_URL = "http://www.5ren.net/test.html";
	public static void TestNetwork(Context context, TestNetworkResult callback)
	{
		final Context c = context;
		final TestNetworkResult cb = callback;

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try
				{
					ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
					final NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni != null && ni.isConnected())
					{
						if (ni.isAvailable() && ni.isConnected())
						{
							Date currTime = new Date();
							if ((currTime.getTime() - S_NETWORK_CHECK_TIME.getTime() > S_NETWORK_CHECK_INTERVAL) ||
									(!S_NETWORK_CONNECTED && currTime.getTime() - S_NETWORK_CHECK_TIME.getTime() > S_NETWORK_CHECK_INTERVAL2))
							{
								HttpClient client = getHttpClient();
								HttpGet req = new HttpGet(S_CHECK_URL);
								InputStream is = null;
								try {
									HttpResponse resp = client.execute(req);
									HttpEntity entity = resp.getEntity();
									if (entity != null)
									{
										is = entity.getContent();
									}
									
									S_NETWORK_CONNECTED = resp != null && resp.getStatusLine().getStatusCode() == 200;
									
									if (cb != null)
									{
										NetState ns = NetState.Disconnected;
										if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
										{
											ns = NetState.MobileConnected;
										}
										else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
										{
											ns = NetState.WifiConnected;
										}
										
										cb.onDone(S_NETWORK_CONNECTED, ns);
									}
								} catch (Exception e) {
									S_NETWORK_CONNECTED = false;

									if (cb != null)
									{
										cb.onDone(false, NetState.Disconnected);
									}
								}
								finally
								{
									if (is != null)
									{
										try
										{
											is.close();
										}
										catch (Exception exp)
										{
										}
									}
									
									S_NETWORK_CHECK_TIME = new Date();
//									client = null;
								}
							}
							else
							{
								if (cb != null)
								{
									NetState ns = NetState.Disconnected;
									if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
									{
										ns = NetState.MobileConnected;
									}
									else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
									{
										ns = NetState.WifiConnected;
									}
									
									cb.onDone(S_NETWORK_CONNECTED, ns);
								}
							}

//							if (cb != null)
//							{
//								NetState ns = NetState.Disconnected;
//								if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
//								{
//									ns = NetState.MobileConnected;
//								}
//								else if (ni.getType() == ConnectivityManager.TYPE_WIFI)
//								{
//									ns = NetState.WifiConnected;
//								}
//								
//								cb.onDone(true, ns);
//							}
							
						}
						else
						{
							if (cb != null)
							{
								cb.onDone(false, NetState.Disconnected);
							}
						}
					}
					else
					{
						if (cb != null)
						{
							cb.onDone(false, NetState.Disconnected);
						}
					}
				}
				catch (Exception exp)
				{
					if (cb != null)
					{
						cb.onDone(false, NetState.Disconnected);
					}
				}
			}
			
		});
		t.start();
//		FinalHttp fh = getFinalHttp();
//		fh.get(S_CHECK_URL, new AjaxCallBack<String>() {
//
//			@Override
//			public void onFailure(Throwable t, int errorNo, String strMsg) {
//				super.onFailure(t, errorNo, strMsg);
//				
//				if (cb != null)
//				{
//					cb.onDone(false);
//				}
//			}
//
//			@Override
//			public void onSuccess(String t) {
//				super.onSuccess(t);
//				
//				if (cb != null)
//				{
//					cb.onDone(true);
//				}
//			}
//			
//		});
	}
	
}
