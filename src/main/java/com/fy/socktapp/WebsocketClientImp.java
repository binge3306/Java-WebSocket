package com.fy.socktapp;

import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

import org.java_websocket.handshake.ServerHandshake;

public class WebsocketClientImp extends WebsocketClientAbs {

	
	
	public WebsocketClientImp(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}

	public WebsocketClientImp(String url, int port) {
		// TODO Auto-generated constructor stub
	}

	private Socket socket = null;
	
//	public WebsocketClientImp(){
//		
//	}
//	
//	public WebsocketClientImp(String url,int port){
//		super(url,port);
//		
//	}
	
	
	@Override
	public void connection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Connection(String userKey, String virifyCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMsgBinary(ByteBuffer msg, long timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMsgBinary(List<ByteBuffer> msg, long timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMsgText(ByteBuffer msg, long timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(long timeout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		
	}

}
