package org.java_websocket.verify;

public class VerifyImplClient extends VerifydataImpl1 implements ClientVerifyBuilder {
	private String resourceDescriptor = "*";

	public VerifyImplClient() {
	}

	public void setResourceDescriptor( String resourceDescriptor ) throws IllegalArgumentException {
		if(resourceDescriptor==null)
			throw new IllegalArgumentException( "http resource descriptor must not be null" );
		this.resourceDescriptor = resourceDescriptor;
	}

	public String getResourceDescriptor() {
		return resourceDescriptor;
	}
}
