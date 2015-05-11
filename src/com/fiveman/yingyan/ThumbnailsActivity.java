package com.fiveman.yingyan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fiveman.yingyan.model.ZipPictureBean;
import com.fiveman.yingyan.notifications.AppNotification;
import com.fiveman.yingyan.utils.ActivityUtils;
import com.fiveman.yingyan.utils.BaseActivity;
import com.fiveman.yingyan.utils.SPConfig;
import com.fiveman.yingyan.utils.ZipUtil;
import com.fiveman.yingyan.widgets.MultiColumnAdapter;
import com.fiveman.yingyan.widgets.MultiColumnView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ThumbnailsActivity extends BaseActivity{
	
	class BrowsePicAdapter extends MultiColumnAdapter 
	{

		@Override
		public View getView(Context context, int index, boolean isInsert) {
			View view = LayoutInflater.from(context).inflate(R.layout.browser_view_info, null);			
			ImageView img = (ImageView)view.findViewById(R.id.thump_picture);
			Object data = this.getItem(index);
			if(data==null)
			{
				Log.v("jiaojc","data is null");
				return null;
			}
			ZipPictureBean item=(ZipPictureBean)data;
			
			LayoutParams lp = img.getLayoutParams();
			lp.height = m_ColumnView.getColumnWidth() * item.getBitmap().getHeight() / item.getBitmap().getWidth();
			img.setLayoutParams(lp);
			img.setImageBitmap(item.getBitmap());
			
			img.setTag(index);
			
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					//MediaItem item=(MediaItem)v.getTag();
					Integer index=(Integer)v.getTag();
					
					ZipPictureBean item = (ZipPictureBean)m_PicAdapter.getItem(index);
					String zipPath = item.getZipPath();
					String zipName = item.getZipName();
					
					Log.v("jiaojc","touch file :"+zipPath+"\tname:"+zipName);
					
					ArrayList<String> zipFiles = new ArrayList<String>();
					for (int i = 0; i < m_PicAdapter.getCount(); i++)
					{
						ZipPictureBean item2 = (ZipPictureBean)m_PicAdapter.getItem(i);
						zipFiles.add(item2.getZipName());
					}
					
					Intent intent=new Intent(ThumbnailsActivity.this, PictureBrowserActivity.class);
					intent.putExtra("zip_path", zipPath);
					intent.putStringArrayListExtra("zip_files", zipFiles);
					intent.putExtra("zip_index", index);
					intent.putExtra("mode", "lianpai");
					ThumbnailsActivity.this.startActivity(intent);
				}
			});
			
			return view;

		}

		@Override
		public View resetView(View view, int index, boolean isInsert) {
			return null;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.yingyan_browser_thumbnails);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.yingyan_titlebar_about);
		
		ActivityUtils.setCurrActivity(this);
		ActivityUtils.setTitleBarCaption(R.string.title_thumbnails_caption);
		
		LinearLayout backAction = (LinearLayout)findViewById(R.id.action_back);
		backAction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityUtils.finishTop(null);
			}
			
		});
		
		ScrollView ticeContainer = (ScrollView)findViewById(R.id.tice_container);
		m_ColumnView = (MultiColumnView)findViewById(R.id.tice_views);
		m_ColumnView.setScrollView(ticeContainer);
		m_PicAdapter = new BrowsePicAdapter();		
		
		Intent intent=this.getIntent();
		mPicturePath=intent.getStringExtra("image_path"); 

		initView();
		
		m_ColumnView.setAdapter((MultiColumnAdapter)m_PicAdapter);
	}
	
	private BrowsePicAdapter m_PicAdapter;
	private MultiColumnView m_ColumnView;
	private String mPicturePath;
	private boolean m_Inited = false;
	
	private void initView()
	{
		if (!m_Inited)
		{
			new Handler().post(new Runnable(){
	
				@Override
				public void run() {
					try {
						loadThumbPictureData();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					m_Inited = true;
				}
				
			});
		}
	}
	
	
	private void loadThumbPictureData() throws Exception
	{
		List<ZipPictureBean> objList = new ArrayList<ZipPictureBean>();
		
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds=true;
		ZipUtil.getBitmapFromZipFile(mPicturePath,options);
		options.inSampleSize = computeSampleSize(options, -1, 128*128); 
		
		options.inJustDecodeBounds = false;  
		
		objList = ZipUtil.getPicturesFromZip(mPicturePath, options);
		
		m_PicAdapter.add(objList.toArray());
	}
	
	 public static List<String> getPictureFileList(String folder,String ext)  {
	    	List<String> fileList = new ArrayList<String>();
	    	File root=new File(folder);
	    	if(!root.exists())
	    		return null;
	    	else
	    	{
	    		File[] files = root.listFiles();
	    		for(int i=0;i<files.length;i++)
	    		{
	    			if(files[i].getPath().endsWith(ext))
	    			{
	    				fileList.add(files[i].getAbsolutePath());
	    			}
	    		}
	    		return fileList;
	    	}
	    	
	    }
		
		@Override
		protected void onStop() {
			super.onStop();
			
			SPConfig.setPropery("last_page", "ThumbnailsActivity");
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
				ActivityUtils.finishTop(null);
				return false;
			}

			return super.onKeyDown(keyCode, event);
		}
	 
		public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {  
		    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);  
		    int roundedSize;  
		    if (initialSize <= 8) {  
		        roundedSize = 1;  
		        while (roundedSize < initialSize) {  
		            roundedSize <<= 1;  
		        }  
		    } else {  
		        roundedSize = (initialSize + 7) / 8 * 8;  
		    }  
		    return roundedSize;  
		}  
		  
		private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {  
		    double w = options.outWidth;  
		    double h = options.outHeight;  
		    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));  
		    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));  
		    if (upperBound < lowerBound) {  
		        // return the larger one when there is no overlapping zone.  
		        return lowerBound;  
		    }  
		    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {  
		        return 1;  
		    } else if (minSideLength == -1) {  
		        return lowerBound;  
		    } else {  
		        return upperBound;  
		    }  
		}
}
