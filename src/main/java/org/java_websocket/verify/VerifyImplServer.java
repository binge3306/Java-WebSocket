package org.java_websocket.verify;

public class VerifyImplServer extends VerifydataImpl1 implements ServerVerifyBuilder {
	private short verifystatus;
	private String verifystatusmessage;

	public VerifyImplServer() {
	}

	@Override
	public String getVerifyStatusMessage() {
		return verifystatusmessage;
	}

	@Override
	public short getVerifyStatus() {
		return verifystatus;
	}

	@Override
	public void setVerifyStatus(short status) {

		verifystatus = status;
	}

	@Override
	public void setVerifyStatusMessage(String message) {
		this.verifystatusmessage = message;
		
	}


}
