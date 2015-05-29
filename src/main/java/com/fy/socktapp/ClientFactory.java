package com.fy.socktapp;


/**
 * 
 * �ͻ������ӹ���
 * <br>
 * һ��Ӧ��ֻ��һ��ʵ��
 * @author wurunzhou add at 201503011
 *
 *	
 */
public class ClientFactory {

	private volatile static WebsocketClientInterface client;

	
	/**
	 * ��������ʵ��
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
