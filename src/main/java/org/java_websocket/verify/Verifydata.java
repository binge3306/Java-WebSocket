package org.java_websocket.verify;

import java.util.Iterator;

public interface Verifydata {
	public Iterator<String> iterateHttpFields();
	public String getFieldValue( String name );
	public boolean hasFieldValue( String name );
	public byte[] getContent();
}
