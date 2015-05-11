package com.fiveman.yingyan.model;

public class DeviceInfo {

	private String m_Name;
	
	private String m_IEEE;
	
	private String m_DeviceType;

	private boolean m_IsOpen;
	
	private boolean m_IsOnline;
	
	private String m_SmartcenterSN;
	
	private String m_CameraIP;		//ÉãÏñÍ·IP
	
	private String m_CameraPort;	//ÉãÏñÍ·¶Ë¿Ú

	public String getName() {
		return m_Name;
	}

	public void setName(String m_Name) {
		this.m_Name = m_Name;
	}

	public String getIEEE() {
		return m_IEEE;
	}

	public void setIEEE(String m_IEEE) {
		this.m_IEEE = m_IEEE;
	}

	public String getDeviceType() {
		return m_DeviceType;
	}

	public void setDeviceType(String m_DeviceType) {
		this.m_DeviceType = m_DeviceType;
	}

	public boolean isOpen() {
		return m_IsOpen;
	}

	public void setOpen(boolean m_IsOpen) {
		this.m_IsOpen = m_IsOpen;
	}

	public boolean isOnline() {
		return m_IsOnline;
	}

	public void setOnline(boolean m_IsOnline) {
		this.m_IsOnline = m_IsOnline;
	}

	public String getSmartcenterSN() {
		return m_SmartcenterSN;
	}

	public void setSmartcenterSN(String m_SmartcenterSN) {
		this.m_SmartcenterSN = m_SmartcenterSN;
	}
	
	public String getCameraIP()
	{
		return m_CameraIP;
	}
	
	public void setCameraIP(String ip)
	{
		m_CameraIP = ip;
	}
	
	public String getCameraPort()
	{
		return m_CameraPort;
	}
	
	public void setCameraPort(String port)
	{
		m_CameraPort = port;
	}

}
