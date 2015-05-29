package com.fy.socktapp;


/**
 * 
 * 客户端连接工程
 * <br>
 * 一个应用只有一个实例
 * @author wurunzhou add at 201503011
 *
 *	
 */
public class ClientFactory {

	private volatile static WebsocketClientInterface client;

	
	/**
	 * 创建连接实体
	 * 
	 * @param url
	 * @param port
	 * @return
	 */
	public static WebsocketClientInterface getClientInstance(String url,
			int port) {
		return getClient(url,port);
	}

	public static  WebsocketClientInterface getClient(String url, int port) {
		if (client == null) {
			synchronized (WebsocketClientInterface.class) {
				if (client == null) {
					//client = new WebsocketClientImp(new inurl);
				}
			}
		}
		return client;
	}
	
	

}
