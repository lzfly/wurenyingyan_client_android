package com.fiveman.yingyan.utils;

import java.io.File;

import android.os.Environment;

public class ConstUtils {

	public static int SPLASH_DISPLAY_TIME = 1000;	//������ʾʱ��
	public static int LOGIN_WAIT_TIME = 1000;	//��¼�ɹ���ȴ�ʱ��
	
	public static String S_CAMERA_TYPE_CODE = "0xFFFF";		//����ͷĬ���豸���ͱ���
	
	public static int S_HTTP_REQUEST_RETRY_COUNT = 2;			//�������������
	public static int S_HTTP_REQUEST_TIMEOUT = 1000 * 20; 		//����������ʱʱ��	

	public static String S_NET_URL="http://www.5ren.net";
	public static String S_API_URL = S_NET_URL+":"+"8007";

	public static String G_APK_NAME = "yingyan";
	public static final String G_GLOABAL_PATH = Environment.getExternalStorageDirectory() + "/"+G_APK_NAME;
	public  static String G_IMAGE_PATH = ConstUtils.G_GLOABAL_PATH + File.separator + "5lianpai/";

	public static String S_GET_PASSWORD_URL = S_API_URL + "/user/userGetBackPassword";		//�һ�����ӿ�
	public static String S_LOGIN_URL = S_API_URL + "/index/login";		//�ͻ��˵�¼�ӿ�
	public static String S_CHANGE_PASSWORD_URL = S_API_URL + "/user/userEdit";		//�޸�����
	public static String S_USER_INIT_URL = S_API_URL + "/user/userEdit";		//��ʼ���û�PUSH_CLIENTID
	public static String S_GET_DEVICE_TYPE_URL = S_API_URL + "/deviceType/deviceTypeList";		//��ȡ�豸����
	public static String S_GET_DEVICE_URL = S_API_URL + "/device/deviceList";		//��ȡ�豸�б�
	public static String S_GET_CAMERA_URL = S_API_URL + "/camera/cameraList";		//��ȡ����ͷ�б�
	public static String S_OPEN_DEVICE_URL = S_API_URL + "/device/deviceSync";		//���豸
	public static String S_CLOSE_DEVICE_URL = S_API_URL + "/device/deviceSync";	//�ر��豸
	public static String S_GET_BIND_CAMERA_URL = S_API_URL + "/device/getBindCamera";		//��ȡ�豸�󶨵�����ͷ
	public static String S_BIND_CAMERA_URL = S_API_URL + "/device/bindCamera";		//������ͷ
	public static String S_UNBIND_CAMERA_URL = S_API_URL + "/device/unbindCamera";		//���������ͷ
	public static String S_UPDATE_DEVICE_NAME_URL = S_API_URL + "/device/deviceEdit";		//�޸��豸����
	public static String S_UPDATE_CAMERA_URL = S_API_URL + "/camera/cameraEdit";		//�޸�����ͷ��Ϣ
	public static String S_GET_CAMERA_SCREENSHOT_URL = S_API_URL + "/camera/getCameraScreenshot";		//�������ͷ��ͼ
	public static String S_GET_UNREAD_NOTICE_URL = S_API_URL + "/notice/getUnreadCount";		//���֪ͨδ������
	public static String S_GET_NOTICE_LIST_URL = S_API_URL + "/notice/getPageList";		//��ѯ����֪ͨ�б�
	public static String S_READ_NOTICE_URL = S_API_URL + "/notice/read";		//�Ķ�֪ͨ
	
	
	public static String S_DOWNLOAD_URL = S_NET_URL+":"+"8008";
	public static String S_GET_5LIANPAI_URL = S_DOWNLOAD_URL + "/pictures";		//����5����ͼƬ��ַ
	
	public static int S_RENAME_SENSOR_ACTIVITY_RESULT_CODE = 10001;
	
	public static int S_SElECT_CAMERA_ACTIVITY_RESULT_CODE = 10002;
	
	public static int S_MODIFY_CAMERA_ADDR_ACTIVITY_RESULT_CODE = 10003;
	
	public static int S_MODIFY_CAMERA_PORT_ACTIVITY_RESULT_CODE = 10004;
	
}