package com.fy.socktapp;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 创建连接,发送消息接口
 * @author wurunzhou
 *
 */
public interface WebsocketClientInterface {

	/**
	 * 连接服务器
	 * <br>
	 * 在风控完成之后，用户验证已经交给风控处理，则可以直接连接
	 */
	public void connection();
	
	/**
	 * 连接服务器
	 * <br> 实际是完成用户验证
	 * @param userKey 用户ID（或者是用户名）
	 * @param virifyCode 用户验证码（该验证码由吾托帮随机生成）
	 */
	public void Connection(String userKey,String virifyCode);
	
	/**
	 * 发送二进制消息
	 * <br>
	 * 二进制消息（如截图和语音）
	 * @param msg 二进制消息内容
	 * @param timeout	最长阻塞时间，如果超过该阻塞时间取消发送
	 */
	public void	sendMsgBinary(ByteBuffer msg,long timeout);
	
	/**
	 * 发送如压缩文件，文档等比较大的二进制消息 
	 * @param msg  大文件消息列表
	 * @param timeout  最长阻塞时间，如果超过该阻塞时间取消发送
	 */
	public void sendMsgBinary(List<ByteBuffer> msg,long timeout);
	
	/**
	 * 发送文本消息
	 * @param msg 文本消息内容
	 * @param timeout 最长阻塞时间，如果超过该阻塞时间取消发送
	 */
	public void sendMsgText(ByteBuffer msg,long timeout);


	/**
	 * 主动关闭连接
	 * @param timeout 最长阻塞时间，如果超过该阻塞时间，强制关闭
	 */
	public void close(long timeout);
}
