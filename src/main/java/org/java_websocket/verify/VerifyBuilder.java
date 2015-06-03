package org.java_websocket.verify;

public interface VerifyBuilder extends Verifydata {
	public abstract void setContent( byte[] content );
	public abstract void put( String name, String value );
}
