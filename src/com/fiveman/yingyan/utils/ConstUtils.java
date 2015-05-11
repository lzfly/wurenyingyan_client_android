package com.fiveman.yingyan.utils;

import java.io.File;

import android.os.Environment;

public class ConstUtils {

	public static int SPLASH_DISPLAY_TIME = 1000;	//封面显示时间
	public static int LOGIN_WAIT_TIME = 1000;	//登录成功后等待时间
	
	public static String S_CAMERA_TYPE_CODE = "0xFFFF";		//摄像头默认设备类型编码
	
	public static int S_HTTP_REQUEST_RETRY_COUNT = 2;			//服务器请求次数
	public static int S_HTTP_REQUEST_TIMEOUT = 1000 * 20; 		//服务器请求超时时间	

	public static String S_NET_URL="http://www.5ren.net";
	public static String S_API_URL = S_NET_URL+":"+"8007";

	public static String G_APK_NAME = "yingyan";
	public static final String G_GLOABAL_PATH = Environment.getExternalStorageDirectory() + "/"+G_APK_NAME;
	public  static String G_IMAGE_PATH = ConstUtils.G_GLOABAL_PATH + File.separator + "5lianpai/";

	public static String S_GET_PASSWORD_URL = S_API_URL + "/user/userGetBackPassword";		//找回密码接口
	public static String S_LOGIN_URL = S_API_URL + "/index/login";		//客户端登录接口
	public static String S_CHANGE_PASSWORD_URL = S_API_URL + "/user/userEdit";		//修改密码
	public static String S_USER_INIT_URL = S_API_URL + "/user/userEdit";		//初始化用户PUSH_CLIENTID
	public static String S_GET_DEVICE_TYPE_URL = S_API_URL + "/deviceType/deviceTypeList";		//获取设备类型
	public static String S_GET_DEVICE_URL = S_API_URL + "/device/deviceList";		//获取设备列表
	public static String S_GET_CAMERA_URL = S_API_URL + "/camera/cameraList";		//获取摄像头列表
	public static String S_OPEN_DEVICE_URL = S_API_URL + "/device/deviceSync";		//打开设备
	public static String S_CLOSE_DEVICE_URL = S_API_URL + "/device/deviceSync";	//关闭设备
	public static String S_GET_BIND_CAMERA_URL = S_API_URL + "/device/getBindCamera";		//获取设备绑定的摄像头
	public static String S_BIND_CAMERA_URL = S_API_URL + "/device/bindCamera";		//绑定摄像头
	public static String S_UNBIND_CAMERA_URL = S_API_URL + "/device/unbindCamera";		//解除绑定摄像头
	public static String S_UPDATE_DEVICE_NAME_URL = S_API_URL + "/device/deviceEdit";		//修改设备名称
	public static String S_UPDATE_CAMERA_URL = S_API_URL + "/camera/cameraEdit";		//修改摄像头信息
	public static String S_GET_CAMERA_SCREENSHOT_URL = S_API_URL + "/camera/getCameraScreenshot";		//获得摄像头截图
	public static String S_GET_UNREAD_NOTICE_URL = S_API_URL + "/notice/getUnreadCount";		//获得通知未读数量
	public static String S_GET_NOTICE_LIST_URL = S_API_URL + "/notice/getPageList";		//查询警告通知列表
	public static String S_READ_NOTICE_URL = S_API_URL + "/notice/read";		//阅读通知
	
	
	public static String S_DOWNLOAD_URL = S_NET_URL+":"+"8008";
	public static String S_GET_5LIANPAI_URL = S_DOWNLOAD_URL + "/pictures";		//下载5连拍图片地址
	
	public static int S_RENAME_SENSOR_ACTIVITY_RESULT_CODE = 10001;
	
	public static int S_SElECT_CAMERA_ACTIVITY_RESULT_CODE = 10002;
	
	public static int S_MODIFY_CAMERA_ADDR_ACTIVITY_RESULT_CODE = 10003;
	
	public static int S_MODIFY_CAMERA_PORT_ACTIVITY_RESULT_CODE = 10004;
	
}