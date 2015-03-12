package socketapp;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import com.fy.socktapp.ClientFactory;
import com.fy.socktapp.FeedbackInterface;
import com.fy.socktapp.WebsocketClientInterface;

public class ClientMain {

	private WebsocketClientInterface websocketClient;
	private Queue<ByteBuffer> readList = null;
	
	
	/**
	 * ����
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException{
		websocketClient = ClientFactory.getClientInstance("localhost", 8877);
		
		init();
		
		// �����շ��߳�
		Thread readWork = new Thread(new ReadThread());
		readWork.start();
		Thread writeWork = new Thread(new WriteThread());
		writeWork.start();
		sendMsgAll(1000);
		readWork.join();
		writeWork.join();
	}

	/**
	 * ��ʼ������
	 */
	public void init(){
		
		websocketClient.Connection("wurunzhou", "verifycode");
	}
	
	// ģ�ⲻ�ϵķ�����Ϣ
	private void sendMsgAll(int i ){
		 
		while(true){
			i --;
			if(i<=0 ) break;
			
			sendMsg("message" + i);
			try {
				// ���߷�����Ϣ
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ������Ϣ
	 * <br>
	 * ����Ϣ�ŵ����Ͷ���
	 * @param msg �ı���Ϣ����
	 */
	private void sendMsg(String msg){
		// ����Ϣ���뷢����Ϣ�������
		


	}
	
	
	
	
	
	public static void main(String [] args){
		System.out.println("begin ");
		try {
			new ClientMain().start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("end");
		
	}
	
	class ReadThread implements Runnable,FeedbackInterface{

		
		
		@Override
		public void onMessageB(ByteBuffer msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMessageT(ByteBuffer msg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(Exception e, String info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClose(Exception e, String info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void run() {

			while(true){
				// ����������� �߳�����
			}
			
		}
		
	}
	
	/**
	 * ������Ϣ�߳�
	 * @author wurunzhou
	 *
	 */
	class WriteThread implements Runnable {

		// ����
		@Override
		public void run() {

			while (true) {
				if (readList.isEmpty()) {
					try {
						readList.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					readList.remove();
					synchronized (websocketClient) {
						ByteBuffer msgB = ByteBuffer.allocate("message"
								.getBytes().length);
						websocketClient.sendMsgText(msgB, 0);
					}
					System.out.println("���");
				}

			}

		}

	}
	
}
