package org.java_websocket.verify;

public interface ClientVerify extends Verifydata {
	/**returns the HTTP Request-URI as defined by http://tools.ietf.org/html/rfc2616#section-5.1.2*/
	public String getResourceDescriptor();
}
