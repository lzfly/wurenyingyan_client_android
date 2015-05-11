package com.fiveman.yingyan.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptUtils {

	private static String S_KEY = "wurenkeji";
	
	private static Cipher S_ENCRYPT_CIPHER = null;
	private static Cipher S_DECRYPT_CIPHER = null;
	
	private static Key getKey()
	{
		byte[] arrSrc = S_KEY.getBytes();
		byte[] arrDES = new byte[8];
		for (int i = 0; i < arrSrc.length && i < arrDES.length; i++) {  
			arrDES[i] = arrSrc[i];  
        }
		
		return new javax.crypto.spec.SecretKeySpec(arrDES, "DES");
	}
	
	private static Cipher getDecryptCipher()
	{
		if (S_DECRYPT_CIPHER == null)
		{
			try {
				S_DECRYPT_CIPHER = Cipher.getInstance("DES");
				S_DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, getKey());
			} catch (InvalidKeyException e) {	
			} catch (NoSuchAlgorithmException e) {
			} catch (NoSuchPaddingException e) {
			}
		}
		return S_DECRYPT_CIPHER;
	}
	
	private static Cipher getEncryptCipher()
	{
		if (S_ENCRYPT_CIPHER == null)
		{
			try {
				S_ENCRYPT_CIPHER = Cipher.getInstance("DES");
				S_ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, getKey());
			} catch (InvalidKeyException e) {	
			} catch (NoSuchAlgorithmException e) {
			} catch (NoSuchPaddingException e) {
			}
		}
		return S_ENCRYPT_CIPHER;
	}
	
	private static String bytes2Hex(byte[] bytes)
	{
		int len = bytes.length;
		
        StringBuffer sb = new StringBuffer(len * 2);  
        for (int i = 0; i < len; i++) {  
            int intTmp = bytes[i];  
            // 把负数转换为正数  
            while (intTmp < 0) {  
                intTmp = intTmp + 256;  
            }  
            // 小于0F的数需要在前面补0  
            if (intTmp < 16) {  
                sb.append("0");  
            }  
            sb.append(Integer.toString(intTmp, 16));  
        }  
        return sb.toString();
	}
	
	public static byte[] encrypt(byte[] bytes)
	{
		try {
			Cipher c = getEncryptCipher();
			if (c != null)
			{
				return c.doFinal(bytes);
			}
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		return null;
	}
	
	public static String encrypt(String text)
	{
		if (text == null)
		{
			text = "";
		}
		return bytes2Hex(encrypt(text.getBytes()));
	}
	
	private static byte[] hex2Bytes(String text)
	{
		byte[] bytes = text.getBytes();  
        int len = bytes.length;  
  
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2  
        byte[] arrOut = new byte[len / 2];  
        for (int i = 0; i < len; i = i + 2) {  
            String strTmp = new String(bytes, i, 2);  
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);  
        }  
        return arrOut;
	}
	
	public static byte[] decrypt(byte[] bytes)
	{
		Cipher c = getDecryptCipher();
		if (c != null)
		{
			try {
				return c.doFinal(bytes);
			} catch (IllegalBlockSizeException e) {
			} catch (BadPaddingException e) {
			}
		}
		return null;
	}
	
	public static String decrypt(String text)
	{
		return new String(decrypt(hex2Bytes(text)));
	}
	
}
