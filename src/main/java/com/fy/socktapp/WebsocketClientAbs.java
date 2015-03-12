package com.fy.socktapp;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;

public abstract class WebsocketClientAbs extends WebSocketClient implements WebsocketClientInterface {

	public WebsocketClientAbs(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}

	
	
}
