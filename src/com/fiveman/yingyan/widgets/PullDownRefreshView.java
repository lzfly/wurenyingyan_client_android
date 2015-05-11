package com.fiveman.yingyan.widgets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fiveman.yingyan.R;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class PullDownRefreshView extends ViewGroup {

	public interface OnRefreshContentListener
	{
		public void onRefreshContent(PullDownRefreshView thisObj);
	}
	
	public interface OnLoadMoreContentListener
	{
		public void onLoadMoreContent(PullDownRefreshView thisObj);
	}
	
	public interface OnScrollingListener
	{
		public void onScrolling(int way);
	}
	
	public static final int SCROLL_UP = 0;
	public static final int SCROLL_DOWN = 1;

	private static final int STATUS_PULL_DOWN = 0;

	private static final int STATUS_RELEASE_TO_REFRESH = 1;

	private static final int STATUS_REFRESHING = 2;

	private static final int STATUS_LOAD_MOREING = 3;
	
	private static final int STATUS_READY = 4;

	private static final int SCROLL_SPEED = 5;
	private int m_TouchSlop;

	private float m_TouchYPos;
	
	private View m_HeaderView;
	private ImageView m_ActionArrow;
	private ProgressBar m_RefreshLoading;
	private TextView m_ActionHint;
	private TextView m_LastUpdateTime;
	
	private ScrollView m_BodyView;
	private View m_ContentView;
	
	private View m_FooterView;
	
	private boolean m_ChildrenResetted;
	private boolean m_Inited;
	private boolean m_ForceLayout;
	
	private int m_HeaderViewHiddenHeight;
	private int m_FooterViewShownHeight;
	
	private boolean m_AllowPullDown;
	private boolean m_AllowPullUp;
	
	private int m_CurrentStatus = STATUS_READY;
	private int m_LastStatus = STATUS_READY;
	
	private OnRefreshContentListener m_OnRefreshContentListener = null;
	private OnLoadMoreContentListener m_OnLoadMoreContentListener = null;
	
	public PullDownRefreshView(Context context) {
		this(context, null);
	}
	
	public PullDownRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);

		m_TouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		m_FooterView = LayoutInflater.from(this.getContext()).inflate(R.layout.puerlink_widget_pull_down_refresh_footer, this, false);
		addView(m_FooterView, 0);
		
		m_BodyView = new ScrollView(this.getContext());
		LayoutParams bodyLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		m_BodyView.setLayoutParams(bodyLayoutParams);
		m_BodyView.setFadingEdgeLength(0);
		m_BodyView.setBackgroundColor(Color.TRANSPARENT);
		addView(m_BodyView, 0);

		m_HeaderView = LayoutInflater.from(this.getContext()).inflate(R.layout.puerlink_widget_pull_down_refresh_header, this, false);
		m_ActionArrow = (ImageView)m_HeaderView.findViewById(R.id.current_action_arrow);
		m_RefreshLoading = (ProgressBar)m_HeaderView.findViewById(R.id.refresh_loading_pic);
		m_ActionHint = (TextView)m_HeaderView.findViewById(R.id.current_action_hint);
		m_LastUpdateTime = (TextView)m_HeaderView.findViewById(R.id.last_refresh_time);
		addView(m_HeaderView, 0);
		
		setRefreshTime();
	}

	private void setRefreshTime()
	{
		Date dtNow = new Date();
		DateFormat dtFormat = new SimpleDateFormat("MM月dd日HH时mm分");
		String firstRefreshTime = String.format(getResources().getString(R.string.pull_down_refresh_refreshtime_hint), dtFormat.format(dtNow));
		m_LastUpdateTime.setText(firstRefreshTime);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        super.measureChildren(widthSize, heightSize);
		super.setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void onFinishInflate() {	
		if (!m_ChildrenResetted)
		{
			ArrayList<View> userChilds = new ArrayList<View>();
			while (super.getChildCount() > 3)
			{
				userChilds.add(super.getChildAt(3));
				super.removeViewAt(3);
			}
			
			for (int i = 0; i < userChilds.size(); i++)
			{
				View child = userChilds.get(i);
				m_BodyView.addView(child, child.getLayoutParams());
			}
			
			if (m_BodyView.getChildCount() > 0)
			{
				m_ContentView = m_BodyView.getChildAt(0);
			}

			m_ChildrenResetted = true;
		}
		
		super.onFinishInflate();
	}
	
	private void handlerLayout(boolean changed, int l, int t, int r, int b, boolean needMeasure)
	{
		int startTop = this.getPaddingTop();;
		int width = r - l;
		int height = b - t;
		
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i ++)
		{
			View child = getChildAt(i);

			int childWidthSize = child.getMeasuredWidth();
			int childHeightSize = child.getMeasuredHeight();
						
			int marginTop = 0;
			int marginBottom = 0;
			
			LayoutParams lpChild = child.getLayoutParams();
			
			if (lpChild instanceof MarginLayoutParams)
			{
				MarginLayoutParams mlp = (MarginLayoutParams)lpChild;
				marginTop = mlp.topMargin;
				marginBottom = mlp.bottomMargin;
			}
			
			if (lpChild.width == LayoutParams.MATCH_PARENT ||
					lpChild.width == LayoutParams.FILL_PARENT)
			{
				childWidthSize = width;
			}
			
			if (lpChild.height == LayoutParams.MATCH_PARENT ||
					lpChild.height == LayoutParams.FILL_PARENT)
			{
				childHeightSize = height - marginTop - marginBottom;
			}
			startTop += marginTop;
			
			if (needMeasure)
			{
				int childWidthSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
				int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
				child.measure(childWidthSpec, childHeightSpec);
			}
			
			child.layout(l, startTop, childWidthSize, startTop + childHeightSize);

			startTop += childHeightSize;
			
			if (lpChild.height == LayoutParams.MATCH_PARENT ||
					lpChild.height == LayoutParams.FILL_PARENT)
			{
				startTop += marginBottom;
			}
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		handlerLayout(changed, l, t, r, b, true);

		if (!m_Inited)
		{
			m_HeaderViewHiddenHeight = m_HeaderView.getHeight();
			this.setPadding(0, -1 * m_HeaderViewHiddenHeight, 0, 0);	

			m_FooterViewShownHeight = m_FooterView.getHeight();
			
			handlerLayout(changed, l, t, r, b, false);
			
			m_Inited = true;
		}
		else if (m_ForceLayout)
		{
			m_ForceLayout = false;
		}
	}

	public ScrollView getScrollView()
	{
		return m_BodyView;		
	}
	
	public int getTopPosition()
	{
		return m_BodyView.getScrollY();
	}
	
	public void setOnRefreshContentListener(OnRefreshContentListener l)
	{
		m_OnRefreshContentListener = l;
	}
	
	public void setOnLoadMoreContentListener(OnLoadMoreContentListener l)
	{
		m_OnLoadMoreContentListener = l;
	}

	private void refreshPullState(MotionEvent event)
	{
		if (m_CurrentStatus != STATUS_REFRESHING)
		{
			if (m_BodyView.getChildCount() > 0)
			{
				int scrollY = m_BodyView.getScrollY();
				if (scrollY == 0 && m_BodyView.getChildAt(0).getScrollY() == 0)
				{
					m_AllowPullDown = true;
				}
				else
				{
					m_AllowPullDown = false;
				}
				
				int measureHeight = m_BodyView.getMeasuredHeight();
				if (m_ContentView != null)
				{
					measureHeight = m_ContentView.getMeasuredHeight();
				}
				if (scrollY + m_BodyView.getHeight() >= measureHeight)
				{
					m_AllowPullUp = true;
				}
				else
				{
					m_AllowPullUp = false;
				}
			}
			else
			{
				m_AllowPullDown = true;
				m_AllowPullUp = true;
			}
		}
		else
		{
			m_AllowPullDown = false;
			m_AllowPullUp = false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		refreshPullState(event);

		if (m_AllowPullDown || m_AllowPullUp)
		{
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					m_TouchYPos = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					float rawYPos = event.getRawY();
					int distance = (int)(rawYPos - m_TouchYPos);
					if (Math.abs(distance) >= m_TouchSlop)
					{
						if (m_CurrentStatus != STATUS_REFRESHING &&
								m_CurrentStatus != STATUS_LOAD_MOREING)
						{
							if (distance > 0)
							{
								if (m_AllowPullDown)
								{
									return true;
								}
							}
							else
							{
								if (m_AllowPullUp)
								{
									new LoadMoreViewTask().execute();
									return true;
								}
							}
						}
					}
					
					break;
			}
		}

		return super.onInterceptTouchEvent(event);
	}	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				float rawYPos = event.getRawY();
				int distance = (int)(rawYPos - m_TouchYPos);
		
				if (m_CurrentStatus != STATUS_REFRESHING &&
						m_CurrentStatus != STATUS_LOAD_MOREING)
				{
					if (distance > 0)
					{
						if (m_AllowPullDown)
						{
							if ((distance / 2) > m_HeaderViewHiddenHeight)
							{
								m_CurrentStatus = STATUS_RELEASE_TO_REFRESH;
							}
							else
							{
								m_CurrentStatus = STATUS_PULL_DOWN;
							}

							this.scrollTo(0, -1 * (distance / 2));
							
							if (m_CurrentStatus == STATUS_PULL_DOWN ||
									m_CurrentStatus == STATUS_RELEASE_TO_REFRESH)
							{
								updateHeaderView(m_CurrentStatus);
							}
							
							return true;
						}
					}
				}

				break;
			default:
				if (m_CurrentStatus == STATUS_RELEASE_TO_REFRESH)
				{
					new RefreshViewTask().execute();
					return true;
				}
				else if (m_CurrentStatus == STATUS_PULL_DOWN)	
				{
					new HideHeaderViewTask().execute(false);
					return true;
				}

				break;
		}
		
		return super.onTouchEvent(event);
	}

	private void updateHeaderView(int state)
	{
		if (state != m_LastStatus)
		{
			if (state == STATUS_PULL_DOWN)
			{
				reverseArrow();
				m_ActionHint.setText(R.string.pull_down_refresh_action_hint);
			}
			else if (state == STATUS_RELEASE_TO_REFRESH)
			{
				reverseArrow();
				m_ActionHint.setText(R.string.pull_down_refresh_action_hint2);
			}
			m_LastStatus = state;
		}
	}
	
	private void reverseArrow()
	{
		if (m_CurrentStatus != STATUS_REFRESHING)
		{
			float pivotX = m_ActionArrow.getWidth() / 2f;
			float pivotY = m_ActionArrow.getHeight() / 2f;
			float fromDegrees = 0f;
			float toDegrees = 0f;

			if (m_CurrentStatus == STATUS_PULL_DOWN) {
				fromDegrees = 180f;
				toDegrees = 360f;
			} else if (m_CurrentStatus == STATUS_RELEASE_TO_REFRESH) {
				fromDegrees = 0f;
				toDegrees = 180f;
			}

			RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
			animation.setDuration(0);
			animation.setFillAfter(true);
			m_ActionArrow.startAnimation(animation);
		}
	}
	
	public void refreshDone()
	{
		if (m_CurrentStatus == STATUS_REFRESHING)
		{
			m_CurrentStatus = STATUS_READY;
			m_AllowPullDown = false;
			m_AllowPullUp = false;
			
			new HideHeaderViewTask().execute(true);
		}
	}
	
	public void loadMoreDone()
	{
		if (m_CurrentStatus == STATUS_LOAD_MOREING)
		{
			m_CurrentStatus = STATUS_READY;
			m_AllowPullDown = false;
			m_AllowPullUp = false;
			
			this.scrollTo(0, 0);
		}
	}
	
	private PullDownRefreshView thisObj = this;
	
	class LoadMoreViewTask extends AsyncTask<Void, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			m_CurrentStatus = STATUS_LOAD_MOREING;
			
			thisObj.scrollTo(0, m_FooterViewShownHeight);
			
			new ScrollToBottomTask().execute();
			
			if (m_OnLoadMoreContentListener != null)
			{
				m_OnLoadMoreContentListener.onLoadMoreContent(thisObj);
			}
		}
		
	}
	
	class ScrollToBottomTask extends AsyncTask<Void, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(Void... arg0) {
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (m_BodyView.getChildCount() > 0)
			{
				int offsetY = m_BodyView.getChildAt(0).getMeasuredHeight() - m_BodyView.getHeight();
				m_BodyView.scrollTo(0, offsetY);
			}
		}
		
	}
	
	class RefreshViewTask extends AsyncTask<Void, Integer, Integer>
	{

		@Override
		protected Integer doInBackground(Void... arg0) {
			int scrollY = thisObj.getScrollY();;
			while (true)
			{
				scrollY += SCROLL_SPEED;
				if (scrollY >= -1 * m_HeaderViewHiddenHeight)
				{
					scrollY = -1 * m_HeaderViewHiddenHeight;
					break;
				}
				publishProgress(scrollY);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return scrollY;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			m_CurrentStatus = STATUS_REFRESHING;
			
			thisObj.scrollTo(0, -1 * m_HeaderViewHiddenHeight);
			
			m_ActionArrow.clearAnimation();
			m_ActionArrow.setVisibility(View.INVISIBLE);
			m_RefreshLoading.setVisibility(View.VISIBLE);
			
			m_ActionHint.setText(R.string.pull_down_refresh_refreshing_hint);
			
			if (m_OnRefreshContentListener != null)
			{
				m_OnRefreshContentListener.onRefreshContent(thisObj);
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			thisObj.scrollTo(0, values[0]);
		}		
		
	}
	
	class HideHeaderViewTask extends AsyncTask<Boolean, Integer, Integer>
	{
		
		private boolean m_IsFromRefresh = false;

		@Override
		protected Integer doInBackground(Boolean... arg0) {
			if (arg0.length > 0)
			{
				m_IsFromRefresh = arg0[0];
			}
			
			int scrollY = thisObj.getScrollY();
			
			while (true)
			{
				scrollY += SCROLL_SPEED;
				if (scrollY >= 0)
				{
					scrollY = 0;
					break;
				}
				publishProgress(scrollY);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return scrollY;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			thisObj.scrollTo(0, 0);
			if (m_IsFromRefresh)
			{
				m_ActionArrow.setVisibility(View.VISIBLE);
				m_RefreshLoading.setVisibility(View.INVISIBLE);
				setRefreshTime();
			}
			
			m_CurrentStatus = STATUS_READY;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			thisObj.scrollTo(0, values[0]);
		}
		
	}
	
}
