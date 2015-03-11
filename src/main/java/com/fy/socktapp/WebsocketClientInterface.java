package com.fy.socktapp;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * ��������,������Ϣ�ӿ�
 * @author wurunzhou
 *
 */
public interface WebsocketClientInterface {

	/**
	 * ���ӷ�����
	 * <br>
	 * �ڷ�����֮���û���֤�Ѿ�������ش��������ֱ������
	 */
	public void connection();
	
	/**
	 * ���ӷ�����
	 * <br> ʵ��������û���֤
	 * @param userKey �û�ID���������û�����
	 * @param virifyCode �û���֤�루����֤�������а�������ɣ�
	 */
	public void Connection(String userKey,String virifyCode);
	
	/**
	 * ���Ͷ�������Ϣ
	 * <br>
	 * ��������Ϣ�����ͼ��������
	 * @param msg ��������Ϣ����
	 * @param timeout	�����ʱ�䣬�������������ʱ��ȡ������
	 */
	public void	sendMsgBinary(ByteBuffer msg,long timeout);
	
	/**
	 * ������ѹ���ļ����ĵ��ȱȽϴ�Ķ�������Ϣ 
	 * @param msg  ���ļ���Ϣ�б�
	 * @param timeout  �����ʱ�䣬�������������ʱ��ȡ������
	 */
	public void sendMsgBinary(List<ByteBuffer> msg,long timeout);
	
	/**
	 * �����ı���Ϣ
	 * @param msg �ı���Ϣ����
	 * @param timeout �����ʱ�䣬�������������ʱ��ȡ������
	 */
	public void sendMsgText(ByteBuffer msg,long timeout);


	/**
	 * �����ر�����
	 * @param timeout �����ʱ�䣬�������������ʱ�䣬ǿ�ƹر�
	 */
	public void close(long timeout);
}
