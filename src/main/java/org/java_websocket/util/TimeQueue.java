package org.java_websocket.util;

import java.util.Date;

public class TimeQueue {

	private Date returnDate;
	private boolean available = false;

	public synchronized Date get() {
		while (available == false) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		available = false;
		notifyAll();
		return returnDate;
	}

	public synchronized void put(Date value) {
		while (available == true) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		returnDate = value;
		available = true;
		notifyAll();
	}

}
