package com.fiveman.yingyan.widgets;

import com.fiveman.yingyan.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageDialog extends Dialog {

	public MessageDialog(Context context) {
		super(context);
	}

	public MessageDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static class Builder
	{
		private Context m_Context;
		private String m_Caption;
		private String m_Message;
		private View m_ContentView;
		private String m_OkText;
		private String m_CancelText;
		private DialogInterface.OnClickListener m_OkListener;
		private DialogInterface.OnClickListener m_CancelListener;
		
		public Builder(Context context)
		{
			m_Context = context;
		}
		
		public Builder setCaption(String caption)
		{
			m_Caption = caption;
			return this;
		}
		
		public Builder setCaption(int captionId)
		{
			m_Caption = (String)m_Context.getText(captionId);
			return this;
		}
		
		public Builder setMessage(String message)
		{
			m_Message = message;
			return this;
		}
		
		public Builder setMessage(int messageId)
		{
			m_Message = (String)m_Context.getText(messageId);
			return this;
		}
		
		public Builder setContentView(View v)
		{
			m_ContentView = v;
			return this;
		}
		
		public Builder setOkButton(String text, DialogInterface.OnClickListener listener)
		{
			m_OkText = text;
			m_OkListener = listener;
			return this;
		}
		
		public Builder setOkButton(int textId, DialogInterface.OnClickListener listener)
		{
			m_OkText = (String)m_Context.getText(textId);
			m_OkListener = listener;
			return this;
		}
		
		public Builder setCancelButton(String text, DialogInterface.OnClickListener listener)
		{
			m_CancelText = text;
			m_CancelListener = listener;
			return this;
		}
		
		public Builder setCancelButton(int textId, DialogInterface.OnClickListener listener)
		{
			m_CancelText = (String)m_Context.getText(textId);
			m_CancelListener = listener;
			return this;
		}
		
		public MessageDialog create()
		{
			LayoutInflater li = (LayoutInflater)m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			final MessageDialog dialog = new MessageDialog(m_Context, R.style.Dialog); //TODO
			
			View contentView = li.inflate(R.layout.widget_message_dialog, null);
			dialog.addContentView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			//标题
			((TextView)contentView.findViewById(R.id.message_dialog_caption)).setText(m_Caption);
			
			//确定按钮
			if (m_OkText != null)
			{
				((Button)contentView.findViewById(R.id.message_dialog_button_ok)).setText(m_OkText);
				if (m_OkListener != null)
				{
					((Button)contentView.findViewById(R.id.message_dialog_button_ok)).setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							m_OkListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
						}
						
					});
				}
				else
				{
					((Button)contentView.findViewById(R.id.message_dialog_button_ok)).setOnClickListener(new View.OnClickListener() {

						public void onClick(View v) {
							dialog.cancel();
						}
						
					});
				}
			}
			else
			{
				contentView.findViewById(R.id.message_dialog_button_ok).setVisibility(View.GONE);
			}
			
			if (m_CancelText != null)
			{
				((Button)contentView.findViewById(R.id.message_dialog_button_cancel)).setText(m_CancelText);
				if (m_CancelListener != null) 
				{
					((Button)contentView.findViewById(R.id.message_dialog_button_cancel)).setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							m_CancelListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
						}
						
					});	
				}
				else
				{
					((Button)contentView.findViewById(R.id.message_dialog_button_cancel)).setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							dialog.cancel();
						}
						
					});	
				}
			}
			else
			{
				contentView.findViewById(R.id.message_dialog_button_cancel).setVisibility(View.GONE);
			}
			
			if (m_Message != null)
			{
				((TextView)contentView.findViewById(R.id.message_dialog_content)).setText(m_Message);
			}
			else if (m_ContentView != null)
			{
				((LinearLayout)contentView.findViewById(R.id.message_dialog_content_container)).removeAllViews();
				((LinearLayout)contentView.findViewById(R.id.message_dialog_content_container)).addView(m_ContentView, 
						new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));				
			}
			
			dialog.setContentView(contentView);
			
			return dialog;
		}
	}

}
