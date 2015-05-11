package com.fiveman.yingyan.model;

public class NoticeInfo {

	private long m_Id;
	private String m_Code;
	private String m_Title;
	private String m_SmartcenterSN;
	private String m_DeviceSN;
	private String m_UpTime;
	private String m_PictureFile;
	private String m_Type;
	private String m_Message;
	private int m_Status = 0;
	private String m_DisplayMessage;

	public long getId()
	{
		return m_Id;
	}
	
	public void setId(long id)
	{
		m_Id = id;
	}
	
	public String getCode()
	{
		return m_Code;
	}
	
	public void setCode(String code)
	{
		m_Code = code;
	}
	
	public String getTitle()
	{
		return m_Title;
	}
	
	public void setTitle(String title)
	{
		m_Title = title;
	}
	
	public String getSmartcenterSN()
	{
		return m_SmartcenterSN;
	}
	
	public void setSmartcenterSN(String sn)
	{
		m_SmartcenterSN = sn;
	}
	
	public String getDeviceSN()
	{
		return m_DeviceSN;
	}
	
	public void setDeviceSN(String sn)
	{
		m_DeviceSN = sn;
	}
	
	public String getUpTime()
	{
		return m_UpTime;
	}
	
	public void setUpTime(String time)
	{
		m_UpTime = time;
	}
	
	public String getPictureFile()
	{
		return m_PictureFile;
	}
	
	public void setPictureFile(String file)
	{
		m_PictureFile = file;
	}
	
	public String getType()
	{
		return m_Type;
	}
	
	public void setType(String type)
	{
		m_Type = type;
	}
	
	public String getMessage()
	{
		return m_Message;
	}
	
	public void setMessage(String msg)
	{
		m_Message = msg;
	}
	
	public int getStatus()
	{
		return m_Status;
	}
	
	public void setStatus(int status)
	{
		m_Status = status;
	}
	
	public String getDisplayMessage()
	{
		return m_DisplayMessage;
	}
	
	public void setDisplayMessage(String msg)
	{
		m_DisplayMessage = msg;
	}
	
}
