package com.fiveman.yingyan.utils;

import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup;

public class NavBarUtils {

	enum NavButtonState
	{
		Normal,
		Selected
	}
	
	public interface OnNavButtonClickListener
	{
		public void onClick(View button, int buttonId);
	}	
	
	private static ViewGroup m_Container = null;
	
	private static HashMap<Integer, View> m_NavButtons = new HashMap<Integer, View>();
	
	private static int m_SelectedId = -1;
	
	private static OnNavButtonClickListener m_ClickListener = null;
	
	public static void init(ViewGroup container)
	{
		m_Container = container;
		
		int count = m_Container.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View childView = m_Container.getChildAt(i);
			int childId = childView.getId();
			if (childId > -1)
			{
				childView.setOnClickListener(navButtonClickListener);
				m_NavButtons.put(childId, childView);
			}
		}
	}
	
	public static void setOnNavButtonClickListener(OnNavButtonClickListener listener)
	{
		m_ClickListener = listener;
	}
	
	public static void setUnselectState()
	{
		m_SelectedId = -1;
	}
	
	public static int getSelectedButtonId()
	{
		return m_SelectedId;
	}
	
	public static void changeButtonState(int buttonId, NavButtonState state)
	{
		if (m_NavButtons.containsKey(buttonId))
		{
			View childView = m_NavButtons.get(buttonId);
			if (state == NavButtonState.Selected)
			{
				if (!childView.isSelected())
				{
					if (m_SelectedId > -1)
					{
						changeButtonState(m_SelectedId, NavButtonState.Normal);
					}
					
					childView.setSelected(true);
					
					int count = ((ViewGroup)childView).getChildCount();
					for (int i = 0; i < count; i++)
					{
						View childChildView = ((ViewGroup)childView).getChildAt(i);
						childChildView.setSelected(true);
					}
					
					m_SelectedId = buttonId;
				}
			}
			else if (childView.isSelected())
			{
				childView.setSelected(false);
				
				int count = ((ViewGroup)childView).getChildCount();
				for (int i = 0; i < count; i++)
				{
					View childChildView = ((ViewGroup)childView).getChildAt(i);
					childChildView.setSelected(false);
				}
				
				m_SelectedId = -1;
			}
		}
	}
	
	public static void doClick(int buttonId)
	{
		if (m_NavButtons.containsKey(buttonId))
		{
			View view = m_NavButtons.get(buttonId);
			navButtonClickListener.onClick(view);
		}
	}
	
	private static View.OnClickListener navButtonClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int childId = v.getId();
			if (childId != m_SelectedId)
			{
				changeButtonState(childId, NavButtonState.Selected);
				
				if (m_ClickListener != null)
				{
					m_ClickListener.onClick(v, childId);
				}
			}
		}
		
	};
	
}
