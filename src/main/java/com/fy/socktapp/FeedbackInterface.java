package com.fy.socktapp;

import java.nio.ByteBuffer;


/**
 * 
 * ��Ϣ���սӿ�
 * <br>
 * �������ն�������Ϣ���ı���Ϣ���쳣���쳣�ر�
 * @author wurunzhou
 * 
 *
 */
public interface FeedbackInterface {

	/**
	 * ���ն�������Ϣ
	 * @param msg
	 */
	public void onMessageB(ByteBuffer msg);
	
	/**
	 * �����ı���Ϣ
	 * @param msg �ļ���Ϣ����
	 */
	public void onMessageT(ByteBuffer msg);
	
	/**
	 * ��ȡ��д���쳣		
	 * @param e �쳣
	 * @param info �쳣�Ĳ�����Ϣ
	 */
	public void onError(Exception e,String info);
	
	/**
	 * �쳣�ر�
	 * @param e �쳣
	 * @param info �쳣�Ĳ�����Ϣ
	 */
	public void onClose(Exception e,String info);
	
}
