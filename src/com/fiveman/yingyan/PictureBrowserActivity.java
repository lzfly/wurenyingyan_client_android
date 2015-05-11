package com.fiveman.yingyan;

import java.net.URL;
import java.util.List;

import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.GlobalContext;
import com.fiveman.yingyan.utils.HttpUtils;
import com.fiveman.yingyan.utils.ToastUtils;
import com.fiveman.yingyan.utils.HttpUtils.HttpResponseListener;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ZipUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PictureBrowserActivity extends BaseActivity implements Callback {
	
	private ImageView mImage;
	private Bitmap mCurrentBitmap;
	private String mZipPath;
	private List<String> mZipFiles;
	private int mZipIndex;
	private String m_CameraSN;
	private String m_Mode = "danzhang";
	
	private Button m_PlayButton;
	private Button m_PrevButton;
	private Button m_NextButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_view_picture);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_about);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.title_activity_view_camera);
		
		GlobalContext.S_CAMERA_PICTURE_VIEW = this;
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_back);
		backAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isstop = true;
				GlobalContext.S_CAMERA_PICTURE_VIEW = null;
				ActivityUtils.finishTop(null);
			}
			
		});
		
		m_PlayButton = (Button)findViewById(R.id.play_button);
		m_PlayButton.setOnClickListener(playButtonOnClickListener);
		
		m_PrevButton = (Button)findViewById(R.id.prev_button);
		m_PrevButton.setOnClickListener(getPrevImageListener);
		
		m_NextButton = (Button)findViewById(R.id.next_button);
		m_NextButton.setOnClickListener(getCurrentImageListener);
		
		mImage=(ImageView)findViewById(R.id.image_content);
		
		Intent intent=this.getIntent();
		mZipPath=intent.getStringExtra("zip_path");
		mZipFiles = intent.getStringArrayListExtra("zip_files");
		mZipIndex = intent.getIntExtra("zip_index", 0);
		m_CameraSN = intent.getStringExtra("camera_sn");
		m_Mode = intent.getStringExtra("mode");
		
		initUI();
	}
	
	public void updateCameraImage(String cameraSN, String url)
	{
		if(cameraSN != null && cameraSN.equals(m_CameraSN))
		{
			if(mCurrentBitmap != null && !mCurrentBitmap.isRecycled())
			{
				mCurrentBitmap.recycle();
				mCurrentBitmap = null;
			}
			
			try {
				URL picUrl = new URL(url);
				mCurrentBitmap = BitmapFactory.decodeStream(picUrl.openStream());
				mImage.setImageBitmap(mCurrentBitmap);
			} catch (Exception e) {
			}
			finally
			{
				ProgressDialog pd = getLoadingDialog();
				if (pd != null)
				{
					pd.dismiss();
				}
			}
		}
	}
	
	private boolean isstop = true;
	private synchronized void playVideo()
	{
//		if (mClickGetImage && !isstop)
//		{
//			Thread t = new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					try
//					{
//						m_LoadHandler.sendEmptyMessage(0);
//						
//						String file = ConstUtils.G_IMAGE_PATH + File.separator + "qly_camera_current.jpg";
//						ShiJieUtils.Capture(m_CameraIP, m_CameraPort, file);
//						
//						Thread.sleep(50);
//						
//						playVideo();
//					}
//					catch (Exception exp)
//					{
//					}
//					finally
//					{
//						m_LoadHandler.sendEmptyMessage(1);
//					}
//				}
//			});
//			t.start();
//		}
	}
	
	private void initUI()
	{
		if ("danzhang".equals(m_Mode))
		{
			m_PrevButton.setVisibility(View.GONE);
			m_PlayButton.setVisibility(View.VISIBLE);
			
			m_NextButton.performClick();
		}
		else if ("lianpai".equals(m_Mode))
		{
			if((mZipPath != null && !TextUtils.isEmpty(mZipPath) &&
					mZipFiles != null && mZipFiles.size() > 0))
			{
				if(mCurrentBitmap != null && !mCurrentBitmap.isRecycled())
				{
					mCurrentBitmap.recycle();
					mCurrentBitmap=null;
				}
		
				try
				{	
					String zipName = "";
					if (mZipIndex >= 0 && mZipIndex < mZipFiles.size())
					{
						zipName = mZipFiles.get(mZipIndex);
					}
					if (zipName != null && !TextUtils.isEmpty(zipName))
					{
						mCurrentBitmap = ZipUtil.getPictureItemFromZip(mZipPath, null, zipName);
					}
				}
				catch(Exception e)
				{
					mCurrentBitmap = null;
				}
		
				if(mCurrentBitmap != null && !mCurrentBitmap.isRecycled())
				{
					mImage.setImageBitmap(mCurrentBitmap);							
				}
				
				m_PrevButton.setVisibility(View.VISIBLE);
				m_PlayButton.setVisibility(View.GONE);
			}
		}
//		if((mZipPicturePath != null && !TextUtils.isEmpty(mZipPicturePath) &&
//				mZipPictureName != null && !TextUtils.isEmpty(mZipPictureName)) ||
//				(mImageFile != null && !TextUtils.isEmpty(mImageFile)))
//		{
//			if(mCurrentBitmap!=null)
//			{
//				mCurrentBitmap.recycle();
//				mCurrentBitmap=null;
//			}
//			
//			try
//			{	
//				if (mImageFile != null && !TextUtils.isEmpty(mImageFile))
//				{
//					mCurrentBitmap= Bitmap.createBitmap(BitmapFactory.decodeFile(mImageFile));
//				}
//				else if (mZipPicturePath != null && !TextUtils.isEmpty(mZipPicturePath) &&
//						mZipPictureName != null && !TextUtils.isEmpty(mZipPictureName))
//				{
//					mCurrentBitmap= ZipUtil.getPictureItemFromZip(mZipPicturePath, null, mZipPictureName);
//				}
//			}
//			catch(Exception e)
//			{
//				mCurrentBitmap=null;
//			}
//
//			if(mCurrentBitmap!=null)
//			{
//				mImage.setImageBitmap(mCurrentBitmap);							
//			}
//		}
	}
	
	View.OnClickListener playButtonOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isstop = !isstop;
			if (!isstop)
			{
				m_NextButton.setEnabled(false);
				m_PlayButton.setText(R.string.action_camera_stop);
				playVideo();
			}
			else
			{
				m_NextButton.setEnabled(true);
				m_PlayButton.setText(R.string.action_camera_play);
			}
		}
		
	};
	
	View.OnClickListener getPrevImageListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if ("lianpai".equals(m_Mode))
			{
				if (mZipIndex > 0)
				{
					mZipIndex--;
					initUI();
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_first_already);
				}
			}
		}
		
	};
	
	View.OnClickListener getCurrentImageListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if ("danzhang".equals(m_Mode))
			{
				HttpUtils.getCameraScreenshot(m_CameraSN, new HttpResponseListener() {

					@Override
					public void onStart() {
						ProgressDialog pd = getLoadingDialog();
						if (pd != null)
						{
							pd.show();
						}
					}

					@Override
					public void onDone(boolean succ, String result) {
						if (!succ)
						{
							ProgressDialog pd = getLoadingDialog();
							if (pd != null)
							{
								pd.dismiss();
							}
							
							ToastUtils.show(thisObj, R.string.err_get_camera_data);
						}
					}
					
				});
			}
			else if ("lianpai".equals(m_Mode))
			{
				if (mZipIndex < mZipFiles.size() - 1)
				{
					mZipIndex++;
					initUI();
				}
				else
				{
					ToastUtils.show(thisObj, R.string.hint_last_already);
				}
			}
		}
	};

	private ProgressDialog m_LoadingDialog;
	private ProgressDialog getLoadingDialog()
	{
		if (m_LoadingDialog == null)
		{
			m_LoadingDialog = new ProgressDialog(thisObj);
			m_LoadingDialog.setMessage(thisObj.getString(R.string.hint_receiving));
		}
		return m_LoadingDialog;
	}

//	private Handler m_LoadHandler = new Handler(this);
	
	@Override
	public boolean handleMessage(Message msg) {
		if (msg != null)
		{
			if (msg.what == 0)
			{
				if  (isstop)
				{
					ProgressDialog pd = getLoadingDialog();
					if (pd != null)
					{
						pd.show();
					}
				}
			}
			else if (msg.what == 1)
			{
				initUI();

				if (isstop)
				{
					ProgressDialog pd = getLoadingDialog();
					if (pd != null)
					{
						pd.dismiss();
					}
				}
			}
		}
		return false;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SPConfig.setPropery("last_page", "PictureBrowserActivity");
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
			isstop = true;
			GlobalContext.S_CAMERA_PICTURE_VIEW = null;
			ActivityUtils.finishTop(null);
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
	
}
