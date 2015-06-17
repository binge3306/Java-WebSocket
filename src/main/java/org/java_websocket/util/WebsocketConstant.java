package org.java_websocket.util;

/**
 * @author Bryan-zhou
 * @date 2015年6月11日上午11:01:22
 **/
public enum WebsocketConstant {

	/**
	 *  接收pong应答时间间隔超过2分钟认为可以跑出接收应答异常。
	 */
	HeartbeatPongCycle("100"),
	
	/**
	 * 默认是否使用心跳（1表示使用）
	 */
	HeartbeatTrue("1"),
	
	/**
	 * 允许发送ping次数的上限(10次）
	 */
	PingConfine("3"),
	
	/**
	 *  每60秒发送一次心跳(老师说要设置为5到10分钟，但是为了测试目前保持1分钟)
	 **/
	HearbeatCycle("30"),
	
	/**
	 * 如果没有达到发送心跳时间，休眠5秒
	 */
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
