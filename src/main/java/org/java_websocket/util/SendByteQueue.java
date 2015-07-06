package org.java_websocket.util;

import java.nio.ByteBuffer;

public class SendByteQueue {

	private ByteBuffer SendData;
	private boolean available = false;

	public synchronized ByteBuffer get() {
		while (available == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		available = false;
		notifyAll();
		return SendData;
	}

	public synchronized void put(ByteBuffer value) {
		while (available == true) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		SendData = value;
		available = true;
		notifyAll();
	}

}
