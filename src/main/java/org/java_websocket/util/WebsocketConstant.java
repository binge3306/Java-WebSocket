package org.java_websocket.util;

/**
 * @author Bryan-zhou
 * @date 2015年6月11日上午11:01:22
 **/
public enum WebsocketConstant {

	HeartbeatTrue("1"),
	
	PingConfine("10"),
	
	HearbeatCycle("60"),
	
	SleepTime("5");
	
	private String parameter;
	
	private WebsocketConstant(String parameter){
		this.parameter = parameter;
	}

	/**
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}	
	
}
