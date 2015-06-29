package org.java_websocket.exceptions;

public enum ExceptionErrorCode {

	/**
	 * 存储消息发送时间的阻塞队列益处
	 */
	OutQueTimeOverFlow(10023),
	InvalidDataException(10022);
	
	
	
	ExceptionErrorCode(int errorCode){
		this.errorCode = errorCode;
	}
	
	private int errorCode;
	
	public int getErrorCode(){
		return errorCode;
	}
}
