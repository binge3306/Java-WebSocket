package com.fy.socktapp;

import java.nio.ByteBuffer;


/**
 * 
 * 消息接收接口
 * <br>
 * 包括接收二进制消息，文本消息，异常，异常关闭
 * @author wurunzhou
 * 
 *
 */
public interface FeedbackInterface {

	/**
	 * 接收二进制消息
	 * @param msg
	 */
	public void onMessageB(ByteBuffer msg);
	
	/**
	 * 接收文本消息
	 * @param msg 文件消息内容
	 */
	public void onMessageT(ByteBuffer msg);
	
	/**
	 * 读取和写入异常		
	 * @param e 异常
	 * @param info 异常的补充消息
	 */
	public void onError(Exception e,String info);
	
	/**
	 * 异常关闭
	 * @param e 异常
	 * @param info 异常的补充消息
	 */
	public void onClose(Exception e,String info);
	
}
