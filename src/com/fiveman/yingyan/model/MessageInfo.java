package com.fiveman.yingyan.model;

public class MessageInfo {

	public enum MessageType
	{
		Info,
		Abnor,
		Warn
	}
	
	public enum MessageState
	{
		Unread,
		read
	}
	
	private String m_LocalId;
	private String m_ServerId;
	private String m_Title;
	private MessageType m_Type;
	private String m_From;
	private String m_Content;
	private String m_Time;
	private MessageState m_State;
	private boolean m_IsQuery;
	
	public boolean IsQuery()
	{
		return m_IsQuery;
	}
	
	public void setIsQuery(boolean isQuery)
	{
		m_IsQuery = isQuery;
	}
	
	public String getLocalId()
	{
		return m_LocalId;
	}
	
	public void setLocalId(String localId)
	{
		m_LocalId = localId;
	}
	
	public String getServerId()
	{
		return m_ServerId;	
	}
	
	public void setServerId(String serverId)
	{
		m_ServerId = serverId;
	}
	
	public String getTitle()
	{
		return m_Title;
	}
	
	public void setTitle(String title)
	{
		m_Title = title;
	}
	
	public MessageType getType()
	{
		return m_Type;
	}
	
	public void setType(MessageType type)
	{
		m_Type = type;
	}
	
	public String getFrom()
	{
		return m_From;
	}
	
	public void setFrom(String from)
	{
		m_From = from;
	}
	
	public String getContent()
	{
		return m_Content;
	}
	
	public void setContent(String content)
	{
		m_Content = content;
	}
	
	public String getTime()
	{
		return m_Time;
	}
	
	public void setTime(String time)
	{
		m_Time = time;
	}
	
	public MessageState getState()
	{
		return m_State;
	}
	
	public void setState(MessageState state)
	{
		m_State = state;
	}
	
}
