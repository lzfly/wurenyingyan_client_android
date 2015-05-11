package com.fiveman.yingyan;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fiveman.yingyan.model.DeviceInfo;
import com.fiveman.yingyan.model.NoticeInfo;
import com.fiveman.yingyan.model.list.DeviceList;
import com.fiveman.yingyan.model.list.DeviceTypeList;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.HttpUtils.HttpResponseListener;
import com.fiveman.yingyan.utils.NetUtils.NetState;
import com.fiveman.yingyan.utils.NetUtils2;
import com.fiveman.yingyan.utils.ToastUtils;
import com.fiveman.yingyan.widgets.MultiColumnAdapter;
import com.fiveman.yingyan.widgets.MultiColumnView;
import com.fiveman.yingyan.widgets.PullDownRefreshView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageActivity extends BaseActivity implements Callback {
	
	class MessageAdapter extends MultiColumnAdapter
	{

		@Override
		public View getView(Context context, int index, boolean isInsert) {
			View view = LayoutInflater.from(context).inflate(R.layout.yingyan_view_one_column_message, null);
			
			Object obj = super.getItem(index);
			if (obj instanceof NoticeInfo)
			{
				NoticeInfo notice = (NoticeInfo)obj;
				
				TextView captionView = (TextView)view.findViewById(R.id.message_caption);
				captionView.setText(notice.getTitle());

				TextView messageContent = (TextView)view.findViewById(R.id.message_content);
				messageContent.setText(notice.getDisplayMessage());

				TextView messageTime = (TextView)view.findViewById(R.id.message_time);
				messageTime.setText(notice.getUpTime());
				
				if (notice.getStatus() == 0)
				{
					messageContent.getPaint().setFakeBoldText(true);
				}
				
				String type = notice.getType();
				if ("alarm".equals(type))
				{
					captionView.setTextColor(Color.rgb(252, 88, 88));
				}
				
				LinearLayout messageContainer = (LinearLayout)view.findViewById(R.id.message_content_container);
				messageContainer.setTag(R.string.tag_sensor_info, notice);
				messageContainer.setOnClickListener(messageClickListener);

				if (!isInsert)
				{
					if (m_MessageViewIndex % 2 == 0)
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background);
					}
					else
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background2);
					}
					
					m_MessageViewIndex++;
					
					m_LoadedPosition = Long.valueOf(notice.getId());					
				}
				else
				{
					if (m_MessageInsertIndex % 2 == 0)
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background2);
					}
					else
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background);
					}
					
					m_MessageInsertIndex++;
				}
			}
			
			return view;
		}

		@Override
		public View resetView(View view, int index, boolean isInsert) {
			Object obj = super.getItem(index);
			if (obj instanceof NoticeInfo)
			{
				NoticeInfo notice = (NoticeInfo)obj;
				
				TextView captionView = (TextView)view.findViewById(R.id.message_caption);
				captionView.setText(notice.getTitle());
				
				TextView messageContent = (TextView)view.findViewById(R.id.message_content);
				messageContent.setText(notice.getDisplayMessage());
				
				TextView messageTime = (TextView)view.findViewById(R.id.message_time);
				messageTime.setText(notice.getUpTime());
				
				if (notice.getStatus() == 0)
				{
					messageContent.getPaint().setFakeBoldText(true);
				}
				else
				{
					messageContent.getPaint().setFakeBoldText(false);
				}
				
				String type = notice.getType();
				if ("alarm".equals(type))
				{
					captionView.setTextColor(Color.rgb(252, 88, 88));
				}
				
				LinearLayout messageContainer = (LinearLayout)view.findViewById(R.id.message_content_container);
				messageContainer.setTag(R.string.tag_sensor_info, notice);
				
				if (!isInsert)
				{
					if (m_MessageViewIndex % 2 == 0)
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background);
					}
					else
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background2);
					}
					
					m_MessageViewIndex++;
					
					m_LoadedPosition = notice.getId();
				}
				else
				{
					if (m_MessageInsertIndex % 2 == 0)
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background2);
					}
					else
					{
						messageContainer.setBackgroundResource(R.drawable.yingyan_selector_message_round_rect_background);
					}
					
					m_MessageInsertIndex++;
				}
			}

			return view;
		}
		
	}
	
	private Handler m_Handler = new Handler(this);
	
	private ProgressDialog m_WaitingDlg;
	private ProgressDialog getWaitingDialog()
	{
		if (m_WaitingDlg == null)
		{
			m_WaitingDlg = new ProgressDialog(this);
			m_WaitingDlg.setMessage(getString(R.string.title_picture_process_waiting));
		}
		return m_WaitingDlg;
	}
	
	private void browse5Lianpai(String zipFile)
	{
		HttpUtils.get5Lianpai(zipFile, new HttpResponseListener() {

			@Override
			public void onStart() {
				ProgressDialog pd = getWaitingDialog();
				if (pd != null)
				{
					pd.show();
				}
			}

			@Override
			public void onDone(boolean succ, String result) {
				ProgressDialog pd = getWaitingDialog();
				if (pd != null)
				{
					pd.dismiss();
				}				
				
				if (succ)
				{
					Intent it = new Intent(thisObj, ThumbnailsActivity.class);
					it.putExtra("image_path", result);
					thisObj.startActivity(it);
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_not_found_picture);
				}
			}
			
		});
	}
	
	View.OnClickListener messageClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tagObj = v.getTag(R.string.tag_sensor_info);
			if (tagObj instanceof NoticeInfo)
			{
				final NoticeInfo notice = (NoticeInfo)tagObj;
				
				String pictureFile = notice.getPictureFile();
				if (pictureFile != null && !TextUtils.isEmpty(pictureFile))
				{
					browse5Lianpai(pictureFile);
				}
				
				if (notice.getStatus() == 0)
				{
					ViewParent parent = v.getParent();
					if (parent != null)
					{
						final View view = (View)parent;
						
						HttpUtils.readNotice(notice.getCode(), new HttpResponseListener() {

							@Override
							public void onStart() {
							}

							@Override
							public void onDone(boolean succ, String result) {
								if (succ)
								{
									notice.setStatus(1);
									m_UnreadCount--;
									
									Message msg = new Message();
									msg.what = 0;
									msg.obj = view;
									m_Handler.sendMessage(msg);
								}
								else
								{
									ToastUtils.show(thisObj, R.string.hint_message_read_error);
								}
							}
							
						});
					}					
				}
			}
		}
		
	};

	private LinearLayout m_MsgReceivingContainer;
//	private ImageView m_MsgReceivingImage;
	
	private PullDownRefreshView m_PullDownRefreshView;

	private MultiColumnView m_MultiColumnView;
	private MessageAdapter m_MessageAdapter;
	
	private int m_MessageViewIndex = 0;
	private int m_MessageInsertIndex = 0;
	
	private boolean m_Inited = false;
	private long m_LoadedPosition = 0;	//加载数据的起始位置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yingyan_activity_message);
		
		GlobalContext.S_MESSAGE_VIEW = this;
		
		m_MsgReceivingContainer = (LinearLayout)findViewById(R.id.message_receiving_container);
//		m_MsgReceivingImage = (ImageView)findViewById(R.id.message_receiving_image);
		
		m_PullDownRefreshView = (PullDownRefreshView)findViewById(R.id.message_pull_refresh_view);
		m_PullDownRefreshView.setOnRefreshContentListener(refreshContentListener);
		m_PullDownRefreshView.setOnLoadMoreContentListener(loadMoreContentListener);
		
		m_MultiColumnView = (MultiColumnView)findViewById(R.id.message_column_views);
		m_MultiColumnView.setScrollView(m_PullDownRefreshView.getScrollView());
		m_MultiColumnView.setOnLoadMoreDoneListener(loadMoreDoneListener);

		m_MessageAdapter = new MessageAdapter();
		m_MultiColumnView.setAdapter(m_MessageAdapter);
		
		showMessageReceiving();
		
		initView();
	}
	
	public void reset()
	{
		m_MessageAdapter.clear();
		m_Inited = false;
		showMessageReceiving();
	}
	
	private synchronized void showMessageReceiving()
	{
		m_MsgReceivingContainer.setVisibility(View.VISIBLE);
		m_PullDownRefreshView.setVisibility(View.GONE);
		
//		AnimationDrawable animDrawable = (AnimationDrawable)m_MsgReceivingImage.getDrawable();
//		if (animDrawable != null)
//		{
//			animDrawable.start();
//		}
	}
	
	private synchronized void hideMessageReceiving()
	{
//		AnimationDrawable animDrawable = (AnimationDrawable)m_MsgReceivingImage.getDrawable();
//		if (animDrawable != null)
//		{
//			animDrawable.stop();
//		}
		
		m_PullDownRefreshView.setVisibility(View.VISIBLE);
		m_MsgReceivingContainer.setVisibility(View.GONE);
	}
	
	public synchronized void initView()
	{
		if (!m_Inited)
		{
			if (GlobalContext.S_LOGINED)
			{
				new Handler().post(new Runnable(){
		
					@Override
					public void run() {
						loadMessageData(m_LoadedPosition, null);
						updateNewMessageHint();
						
						m_Inited = true;
					}
					
				});
			}
			else
			{
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						initView();
					}
					
				}, 666);
			}
		}
	}
	
	public synchronized void cleanAllMessage()
	{
		m_MessageAdapter.clear();
		showMessageReceiving();
	}
	
	/*
	 * 重启加载
	 */
	PullDownRefreshView.OnRefreshContentListener refreshContentListener = new PullDownRefreshView.OnRefreshContentListener() {
		
		@Override
		public void onRefreshContent(PullDownRefreshView thisObj) {
			m_LoadedPosition = 0;
			m_MessageViewIndex = 0;
			m_MessageInsertIndex = 0;

			loadMessageData(m_LoadedPosition, null);
			updateNewMessageHint();
		}
		
	};
	
	/*
	 * 加载更多
	 */
	PullDownRefreshView.OnLoadMoreContentListener loadMoreContentListener = new PullDownRefreshView.OnLoadMoreContentListener() {
		
		@Override
		public void onLoadMoreContent(PullDownRefreshView thisObj) {		
			Handler handler = new Handler();
			Runnable runnable = new Runnable()
			{
				@Override
				public void run() {
					loadMessageData(m_LoadedPosition, null);
				}
				
			};
			handler.post(runnable);
		}

	};

	private boolean m_HintWhenLoadMoreNull = true;
	
	/*
	 * 加载更多完毕
	 */
	MultiColumnView.OnLoadMoreDoneListener loadMoreDoneListener = new MultiColumnView.OnLoadMoreDoneListener() {
		
		@Override
		public void onLoadMoreDone() {
			m_PullDownRefreshView.refreshDone();
			m_PullDownRefreshView.loadMoreDone();
			
			m_MultiColumnView.enableScrollToEndWhenAdded(true);
		}
		
	};
	
	public void receivedNotice(JSONObject noticeObj)
	{
		NoticeInfo notice = new NoticeInfo();
		notice.setCode(noticeObj.getString("CODE"));
		notice.setTitle(thisObj.getString(R.string.warn_title));
		notice.setSmartcenterSN(noticeObj.getString("SMARTCENTER_SN"));
		String devSN = noticeObj.getString("DEVICE_SN");
		notice.setDeviceSN(devSN);
		DeviceInfo device = DeviceList.getDevice(devSN);
		if (device != null)
		{
			String devTypeName =DeviceTypeList.getDeviceTypeName(device.getDeviceType());
			String content = String.format(thisObj.getString(R.string.warn_invade_message), 
					devTypeName, device.getName());
			notice.setDisplayMessage(content);
		}
		
		notice.setType(noticeObj.getString("TYPE"));
		notice.setMessage(noticeObj.getString("MESSAGE"));
		notice.setPictureFile(noticeObj.getString("PICTURE_FILE"));
		notice.setUpTime(noticeObj.getString("UPTIME"));
		
		m_MessageAdapter.insert(notice);
		
		m_UnreadCount++;
		m_Handler.sendEmptyMessage(1);
	}
	
	private int m_UnreadCount = 0;
	private void updateNewMessageHint()
	{
		HttpUtils.getUnreadNoticeCount(new HttpResponseListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onDone(boolean succ, String result) {
				try
				{
					if (succ)
					{
						m_UnreadCount = Integer.parseInt(result);
					}
					else
					{
						m_UnreadCount = 0;
					}
					
					MainActivity mainActivity = (MainActivity)ActivityUtils.getMainActivity();
					if (m_UnreadCount > 0)
					{
						if (mainActivity != null)
						{
							mainActivity.showNewMessageHint(m_UnreadCount);
						}
					}
					else
					{
						if (mainActivity != null)
						{
							mainActivity.hideNewMessageHint();
						}
					}
				}
				catch (Exception exp)
				{
				}
			}
			
		});
	}
	
	/**
	 * 加载通知数据
	 */
	private void loadMessageData(final long maxId, final HttpResponseListener callback)
	{
		NetUtils2.TestNetwork(thisObj, new NetUtils2.TestNetworkResult() {
			
			@Override
			public void onDone(boolean succ, NetState state) {
				if (succ)
				{
					HttpUtils.getNoticeList(maxId, 50, "alarm", "", new HttpResponseListener() {

						@Override
						public void onStart() {
							if (callback != null)
							{
								callback.onStart();
							}
						}

						@Override
						public void onDone(boolean succ, String result) {
							if (succ)
							{
								boolean ok = false;
								boolean nomessage = false;

								try
								{
									JSONObject respObj = JSON.parseObject(result);
									int code = respObj.getIntValue("code");
									if (code == HttpUtils.S_SUCC_CODE)
									{
										JSONObject resultObj = respObj.getJSONObject("result");
										if (resultObj != null)
										{
											JSONArray noticeList = resultObj.getJSONArray("notice.list");
											if (noticeList != null)
											{
												if (noticeList.size() > 0)
												{
													List<NoticeInfo> notices = new ArrayList<NoticeInfo>();
													for (int i = 0; i < noticeList.size(); i++)
													{
														JSONObject noticeItem = noticeList.getJSONObject(i);
														NoticeInfo notice = new NoticeInfo();
														notice.setId(noticeItem.getLongValue("ID"));
														notice.setCode(noticeItem.getString("CODE"));
														notice.setTitle(thisObj.getString(R.string.warn_title));
														notice.setSmartcenterSN(noticeItem.getString("SMARTCENTER_SN"));
														String devSN = noticeItem.getString("DEVICE_SN");
														notice.setDeviceSN(devSN);
														DeviceInfo device = DeviceList.getDevice(devSN);
														if (device != null)
														{
															String devTypeName =DeviceTypeList.getDeviceTypeName(device.getDeviceType());
															String content = String.format(thisObj.getString(R.string.warn_invade_message), 
																	devTypeName, device.getName());
															notice.setDisplayMessage(content);
														}
														else
														{
															Log.d("wxm", "not found device :  " + devSN);
														}
														
														notice.setType(noticeItem.getString("TYPE"));
														notice.setMessage(noticeItem.getString("MESSAGE"));
														notice.setPictureFile(noticeItem.getString("PICTURE_FILE"));
														notice.setUpTime(noticeItem.getString("UPTIME"));
														String userId = noticeItem.getString("USER_ID");
														if (userId != null && !TextUtils.isEmpty(userId))
														{
															notice.setStatus(1);
														}
														notices.add(notice);
													}
													
													if (m_LoadedPosition == 0)
													{
														m_MessageAdapter.reset(notices.toArray());
													}
													else
													{
														m_MessageAdapter.add(notices.toArray());
													}
													
													if (m_MessageAdapter.getCount() > 0)
													{
														hideMessageReceiving();
													}
													else
													{
														showMessageReceiving();
													}
												}
												else
												{
													nomessage = true;
													if (m_LoadedPosition == 0)
													{
														m_MessageAdapter.clear();
													}
												}

												ok = true;
											}
										}
									}
								}
								catch (Exception exp)
								{
								}

								m_PullDownRefreshView.refreshDone();
								m_PullDownRefreshView.loadMoreDone();
								
								if (!ok)
								{
									ToastUtils.show(thisObj, thisObj.getResources().getString(R.string.loading_exp_hint));
								}
								else
								{
									if (m_HintWhenLoadMoreNull && nomessage && m_LoadedPosition > 0)
									{
										ToastUtils.show(thisObj, thisObj.getResources().getString(R.string.message_to_end));
									}
								}
								
								if (m_MessageAdapter.getCount() > 0)
								{
									hideMessageReceiving();
								}
								else
								{
									showMessageReceiving();
									
									MainActivity mainActivity = (MainActivity)ActivityUtils.getMainActivity();
									if (mainActivity != null)
									{
										mainActivity.hideNewMessageHint();
									}
								}

								m_HintWhenLoadMoreNull = true;
							}
							else
							{
								m_PullDownRefreshView.refreshDone();
								m_PullDownRefreshView.loadMoreDone();
								ToastUtils.show(thisObj, thisObj.getResources().getString(R.string.loading_exp_hint));
								
								if (m_MessageAdapter.getCount() > 0)
								{
									hideMessageReceiving();
								}
								else
								{
									showMessageReceiving();
								}
							}
							
							if (callback != null)
							{
								callback.onDone(succ, result);
							}
						}
						
					});
					
				}
				else
				{
					m_PullDownRefreshView.refreshDone();
					m_PullDownRefreshView.loadMoreDone();
					ToastUtils.show(thisObj, thisObj.getResources().getString(R.string.no_internet));
					
					if (m_MessageAdapter.getCount() > 0)
					{
						hideMessageReceiving();
					}
					else
					{
						showMessageReceiving();
					}
					
					if (callback != null)
					{
						callback.onDone(false, "");
					}
				}
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == 0)	//阅读通知
		{
			if (msg.obj instanceof View)
			{
				View view = (View)msg.obj;
				TextView messageContent = (TextView)view.findViewById(R.id.message_content);
				messageContent.getPaint().setFakeBoldText(false);
				messageContent.invalidate();
				
				ToastUtils.show(thisObj, R.string.hint_message_isread);
				
				MainActivity mainActivity = (MainActivity)ActivityUtils.getMainActivity();
				if (mainActivity != null)
				{
					if (m_UnreadCount > 0)
					{
						mainActivity.showNewMessageHint(m_UnreadCount);
					}
					else
					{
						mainActivity.hideNewMessageHint();
					}
				}
			}
		}
		else if (msg.what == 1)		//收到新通知
		{
			MainActivity mainActivity = (MainActivity)ActivityUtils.getMainActivity();
			if (mainActivity != null)
			{
				if (m_UnreadCount > 0)
				{
					mainActivity.showNewMessageHint(m_UnreadCount);
				}
				else
				{
					mainActivity.hideNewMessageHint();
				}
			}
		}
		return false;
	}

}
