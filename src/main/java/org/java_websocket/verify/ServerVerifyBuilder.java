package org.java_websocket.verify;

public interface ServerVerifyBuilder extends VerifyBuilder, ServerVerify {
	public void setVerifyStatus( short status );
	public void setVerifyStatusMessage( String message );
}
